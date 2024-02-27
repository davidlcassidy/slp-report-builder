package com.davidlcassidy.reportbuilder.controller;

import com.davidlcassidy.reportbuilder.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/{interviewId}")
    public ResponseEntity<byte[]> generateReport(@PathVariable("interviewId") String interviewId) {
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
