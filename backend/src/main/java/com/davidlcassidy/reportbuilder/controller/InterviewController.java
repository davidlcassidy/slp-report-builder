package com.davidlcassidy.reportbuilder.controller;

import com.davidlcassidy.reportbuilder.enumerated.ReportSection;
import com.davidlcassidy.reportbuilder.model.InterviewQuestion;
import com.davidlcassidy.reportbuilder.model.InterviewAnswer;
import com.davidlcassidy.reportbuilder.model.User;
import com.davidlcassidy.reportbuilder.payload.InitialInterviewResponse;
import com.davidlcassidy.reportbuilder.model.Interview;
import com.davidlcassidy.reportbuilder.payload.SaveInterviewAnswersRequest;
import com.davidlcassidy.reportbuilder.repository.InterviewRepository;
import com.davidlcassidy.reportbuilder.repository.InterviewQuestionRepository;
import com.davidlcassidy.reportbuilder.repository.InterviewAnswerRepository;
import com.davidlcassidy.reportbuilder.service.InterviewService;
import com.davidlcassidy.reportbuilder.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Interview Controller", description = "")
@RequestMapping("/api/v1/interview")
public class InterviewController {

    @Autowired
    private UserService userService;

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewQuestionRepository interviewQuestionRepository;

    @Autowired
    private InterviewAnswerRepository interviewAnswerRepository;

    @PostMapping("")
    public ResponseEntity<InitialInterviewResponse> startNewInterview(HttpServletRequest request) {
        User user;
        try {
            user = userService.validateUserAuthenticationToken(request);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        InitialInterviewResponse response = InitialInterviewResponse.builder()
                .interviewId(interviewService.createNewInterview(user))
                .sections(Arrays.asList(ReportSection.values()))
                .questionResponses(interviewService.getNextInterviewQuestions(new ArrayList<>()))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/responses")
    public ResponseEntity<List<InterviewQuestion>> addNewInterviewResponses(
            HttpServletRequest request,
            @RequestBody SaveInterviewAnswersRequest requestBody
    ) {
        try {
            userService.validateUserAuthenticationTokenAndInterview(request, requestBody.getInterviewId());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Interview interview = interviewRepository.findById(String.valueOf(requestBody.getInterviewId())).orElse(null);
        if (interview == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            List<InterviewAnswer> previousResponses = interviewService.saveResponses(interview, requestBody.getQuestionResponses());
            List<InterviewQuestion> response = interviewService.getNextInterviewQuestions(previousResponses);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/completed")
    public ResponseEntity<List<Interview>> addNewInterviewResponses(HttpServletRequest request) {
        User user;
        try {
            user = userService.validateUserAuthenticationToken(request);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(interviewRepository.findAllByUserIdAndCompletedDateIsNotNull(user.getId()), HttpStatus.OK);
    }

}
