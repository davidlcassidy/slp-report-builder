package com.davidlcassidy.reportbuilder.repository;

import com.davidlcassidy.reportbuilder.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, String> {
    List<Interview> findByUserId(Long userId);
    Interview findByUserIdAndId(Long userId, Long interviewId);
    List<Interview> findAllByUserIdAndCompletedDateIsNotNull(Long userId);
    void deleteByUserId(Long userId);
}