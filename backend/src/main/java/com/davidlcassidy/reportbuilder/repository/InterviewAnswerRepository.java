package com.davidlcassidy.reportbuilder.repository;

import com.davidlcassidy.reportbuilder.model.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, String> {
    List<InterviewAnswer> findByInterview_Id(Long interviewId);
    void deleteByInterview_Id(Long interviewId);

    @Modifying
    @Query("DELETE FROM InterviewAnswer ia WHERE ia.interview.user.id = :userId")
    void deleteByUserId(Long userId);
}