package com.davidlcassidy.reportbuilder.controller;

import com.davidlcassidy.reportbuilder.model.User;
import com.davidlcassidy.reportbuilder.repository.InterviewRepository;
import com.davidlcassidy.reportbuilder.service.ReportService;
import com.davidlcassidy.reportbuilder.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Tag(name = "Report Controller", description = "")
@RequestMapping("/api/v1/report")
public class ReportController {

    @Autowired
    private UserService userService;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private ReportService reportService;

    @GetMapping("/{interviewId}")
    public ResponseEntity<byte[]> generateReport(
            HttpServletRequest request,
            @PathVariable("interviewId") String interviewId
    ) {
        try {
            userService.validateUserAuthenticationTokenAndInterview(request, Long.valueOf(interviewId));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            byte[] documentBytes = reportService.generateReportDocument(Long.valueOf(interviewId));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("filename", "document.docx");
            return new ResponseEntity<>(documentBytes, headers, HttpStatus.OK);
        } catch (Docx4JException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
