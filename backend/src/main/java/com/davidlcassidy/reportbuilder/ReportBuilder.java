package com.davidlcassidy.reportbuilder;

import com.davidlcassidy.reportbuilder.model.InterviewQuestion;
import com.davidlcassidy.reportbuilder.repository.InterviewQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
public class ReportBuilder {

    public static void main(String[] args) {
        SpringApplication.run(ReportBuilder.class, args);
    }

    @Component
    public class DataLoader implements ApplicationRunner {

        @Autowired
        private InterviewQuestionRepository interviewQuestionRepository;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            if (interviewQuestionRepository.count() == 0) {
                ObjectMapper objectMapper = new ObjectMapper();
                InputStream inputStream = getClass().getResourceAsStream("/initial-questions.json");
                List<InterviewQuestion> interviewQuestions = objectMapper.readValue(inputStream, new TypeReference<List<InterviewQuestion>>() {});
                interviewQuestionRepository.saveAll(interviewQuestions);
            }
        }
    }
}
