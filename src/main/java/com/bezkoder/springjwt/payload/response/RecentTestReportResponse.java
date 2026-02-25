package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentTestReportResponse {
    private Long id;
    private List<String> subjectNames;
    private String testName;
    private String date;
    private int totalQuestions;
    private int correctAnswers;
    private String timeTaken;
    private int score;
    private int improvement;
}
