package com.davidlcassidy.reportbuilder.service;

import com.davidlcassidy.reportbuilder.enumerated.ReportSection;
import com.davidlcassidy.reportbuilder.model.InterviewAnswer;
import com.davidlcassidy.reportbuilder.model.Interview;
import com.davidlcassidy.reportbuilder.model.InterviewQuestion;
import com.davidlcassidy.reportbuilder.model.User;
import com.davidlcassidy.reportbuilder.payload.QuestionAnswerDTO;
import com.davidlcassidy.reportbuilder.repository.InterviewRepository;
import com.davidlcassidy.reportbuilder.repository.InterviewQuestionRepository;
import com.davidlcassidy.reportbuilder.repository.InterviewAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {

    static ReportSection INITIAL_SECTION = ReportSection.PATIENT;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewQuestionRepository interviewQuestionRepository;

    @Autowired
    private InterviewAnswerRepository interviewAnswerRepository;

    public Long createNewInterview(User user){
        Interview newInterview = Interview.builder().user(user).build();
        newInterview = interviewRepository.save(newInterview);
        return newInterview.getId();
    }

    public List<InterviewAnswer> saveResponses(Interview interview, List<QuestionAnswerDTO> responses) throws Exception {
        List<InterviewAnswer> interviewAnswerEntities = new ArrayList<>();
        for (QuestionAnswerDTO responseDTO : responses) {
            InterviewQuestion interviewQuestion = interviewQuestionRepository.findById(responseDTO.getQuestionId()).orElse(null);
            if (interviewQuestion == null) {
                throw new Exception();
            }

            InterviewAnswer interviewAnswer = new InterviewAnswer();
            interviewAnswer.setInterview(interview);
            interviewAnswer.setInterviewQuestion(interviewQuestion);
            interviewAnswer.setResponse(responseDTO.getResponse());
            interviewAnswerEntities.add(interviewAnswer);
        }

        interviewAnswerRepository.saveAll(interviewAnswerEntities);

        return interviewAnswerEntities;
    }

    public List<InterviewQuestion> getNextInterviewQuestions(List<InterviewAnswer> previousresponses){

        if (previousresponses.isEmpty()) {
            return interviewQuestionRepository.findBySection(INITIAL_SECTION);
        } else {
            ReportSection previousSection = previousresponses.get(0).getInterviewQuestion().getSection();
            ReportSection nextSection = null;
            switch(previousSection) {
                case PATIENT:
                    nextSection = ReportSection.BACKGROUND;
                    break;
                case BACKGROUND:
                    Long interviewId = previousresponses.get(0).getInterview().getId();
                    markInterviewAsCompleted(interviewId);
                    break;
            }

            if (nextSection == null) {
                return new ArrayList<>();
            }

            return interviewQuestionRepository.findBySection(nextSection);
        }
    }

    private void markInterviewAsCompleted(Long interviewId) {
        Optional<Interview> optionalInterview = interviewRepository.findById(String.valueOf(interviewId));
        optionalInterview.ifPresent(interview -> {
            interview.setCompletedDate(new Date());
            interviewRepository.save(interview);
        });
    }
}