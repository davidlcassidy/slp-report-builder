package com.davidlcassidy.reportbuilder.repository;

import com.davidlcassidy.reportbuilder.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, String> {
    List<Interview> findAllByCompletedDateNotNull();
}