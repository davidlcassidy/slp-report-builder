package com.davidlcassidy.reportbuilder.model;

import com.davidlcassidy.reportbuilder.enumerated.QuestionType;
import com.davidlcassidy.reportbuilder.enumerated.ReportSection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String internalId;

    @Enumerated(EnumType.STRING)
    private ReportSection section;
    private String subSection;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    private String text;
    private String[] answerOptions;
}

