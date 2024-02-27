package com.davidlcassidy.reportbuilder.payload;

import com.davidlcassidy.reportbuilder.enumerated.ReportSection;
import com.davidlcassidy.reportbuilder.model.InterviewQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitialInterviewResponse {
    private Long interviewId;
    private List<ReportSection> sections;
    private List<InterviewQuestion> questionResponses;
}
