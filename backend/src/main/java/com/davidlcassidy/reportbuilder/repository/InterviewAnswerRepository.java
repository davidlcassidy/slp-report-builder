package com.davidlcassidy.reportbuilder.repository;

import com.davidlcassidy.reportbuilder.model.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, String> {
    List<InterviewAnswer> findByInterview_Id(Long interviewId);
}