package com.bezkoder.springjwt.payload.response;

import lombok.Data;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class TestResultResponse {
    private int testId;
    private String testName;
    private Date testDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double marks;
    private List<QuestionResponseDTO> questionResponses;
    private Long createdBy;

    @Data
    public static class QuestionResponseDTO {
        private int questionNumber;
        private String question;
        private String option1;
        private String option2;
        private String option3;
        private String option4;
        private int marks;
        private String explanation;
        private int timeSpentSeconds;
        private int visits;
        private boolean answered;
        private Set<String> multiAnswers;
        private List<String> userSelectedOptions;
    }
}

