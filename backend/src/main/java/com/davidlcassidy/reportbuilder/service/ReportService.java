package com.davidlcassidy.reportbuilder.service;

import com.davidlcassidy.reportbuilder.enumerated.ReportSection;
import com.davidlcassidy.reportbuilder.model.InterviewAnswer;
import com.davidlcassidy.reportbuilder.repository.InterviewQuestionRepository;
import com.davidlcassidy.reportbuilder.repository.InterviewRepository;
import com.davidlcassidy.reportbuilder.repository.InterviewAnswerRepository;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewQuestionRepository interviewQuestionRepository;

    @Autowired
    private InterviewAnswerRepository interviewAnswerRepository;

    public byte[] generateReportDocument(Long interviewId) throws Docx4JException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Docx4J.save(getReportContent(interviewId), outputStream);
        return outputStream.toByteArray();
    }

    private WordprocessingMLPackage getReportContent(Long interviewId) throws IOException, InvalidFormatException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

        List<ReportSection> reportSections = Arrays.asList(ReportSection.PATIENT, ReportSection.BACKGROUND);

        for (ReportSection section : reportSections) {
            wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1", section.name());
            String reportSectionContent = replacePlaceholdersWithAnswers(interviewId, getReportSectionContent(section));
            wordMLPackage.getMainDocumentPart().addParagraphOfText(reportSectionContent);
        }

        return wordMLPackage;
    }

    private String getReportSectionContent(ReportSection reportSection) throws IOException {
        String reportSectionText = "report_sections/patient.txt";

        switch(reportSection) {
            case PATIENT:
                reportSectionText = "report_sections/patient.txt";
                break;
            case BACKGROUND:
                reportSectionText = "report_sections/background.txt";
                break;
        }

        ClassPathResource resource = new ClassPathResource(reportSectionText);
        InputStream inputStream = resource.getInputStream();
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String replacePlaceholdersWithAnswers(Long interviewId, String reportSectionText) {
        List<InterviewAnswer> interviewAnswers = interviewAnswerRepository.findByInterview_Id(interviewId);

        Map<String, String> answerMap = interviewAnswers.stream()
                .collect(Collectors.toMap(answer -> answer.getInterviewQuestion().getInternalId(), InterviewAnswer::getResponse));

        String replacedText = reportSectionText;
        for (InterviewAnswer answer : interviewAnswers) {
            String response = answerMap.getOrDefault(answer.getInterviewQuestion().getInternalId(), "");
            replacedText = replacedText.replace("[[" + answer.getInterviewQuestion().getInternalId() + "]]", response);
        }

        return replacedText;
    }
}