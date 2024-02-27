package com.davidlcassidy.reportbuilder.controller;

import com.davidlcassidy.reportbuilder.enumerated.ReportSection;
import com.davidlcassidy.reportbuilder.model.InterviewQuestion;
import com.davidlcassidy.reportbuilder.model.InterviewAnswer;
import com.davidlcassidy.reportbuilder.payload.InitialInterviewResponse;
import com.davidlcassidy.reportbuilder.model.Interview;
import com.davidlcassidy.reportbuilder.payload.SaveInterviewAnswersRequest;
import com.davidlcassidy.reportbuilder.repository.InterviewRepository;
import com.davidlcassidy.reportbuilder.repository.InterviewQuestionRepository;
import com.davidlcassidy.reportbuilder.repository.InterviewAnswerRepository;
import com.davidlcassidy.reportbuilder.service.InterviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Tag(name = "Interview Controller", description = "")
@RequestMapping("/api/interview")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewQuestionRepository interviewQuestionRepository;

    @Autowired
    private InterviewAnswerRepository interviewAnswerRepository;

    @PostMapping("")
    public ResponseEntity<InitialInterviewResponse> startNewInterview() {
        InitialInterviewResponse response = InitialInterviewResponse.builder()
                .interviewId(interviewService.createNewInterview())
                .sections(Arrays.asList(ReportSection.values()))
                .questionResponses(interviewService.getNextInterviewQuestions(new ArrayList<>()))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/responses")
    public ResponseEntity<List<InterviewQuestion>> addNewInterviewResponses(@RequestBody SaveInterviewAnswersRequest request) {
        Interview interview = interviewRepository.findById(String.valueOf(request.getInterviewId())).orElse(null);
        if (interview == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            List<InterviewAnswer> previousResponses = interviewService.saveResponses(interview, request.getQuestionResponses());
            List<InterviewQuestion> response = interviewService.getNextInterviewQuestions(previousResponses);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @GetMapping("/completed")
    public ResponseEntity<List<Interview>> addNewInterviewResponses() {
        return ResponseEntity.status(HttpStatus.OK).body(interviewRepository.findAllByCompletedDateNotNull());
    }

}
