package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class TestSubmissionRequest {
    private Long studentId;
    private Integer testId;
    private List<QuestionSubmission> answers;
    private List<Integer> importantQuestions;

    @Data
    public static class QuestionSubmission {
        private Integer questionId;
        private List<String> selectedAnswers;
        private int timeSpent;
        private int visits;
    }
}