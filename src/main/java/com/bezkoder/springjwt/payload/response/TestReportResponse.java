package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestReportResponse {

    private Integer id;              // testId
    private String examName;         // testName
    private String subject;          // if multiple subjects, you can store them in a list or combine as a string
    private String date;             // e.g., "2024-01-15"
    private String duration;         // e.g., "2 hours"
    private int totalStudents;       // e.g., 45
    private double passPercentage;   // e.g., 85
    private double averageScore;     // e.g., 72
    private double highestScore;     // e.g., 98
    private String status;           // e.g., "Completed"
    private int totalQuestions;      // e.g., 50
    private double passingScore;     // e.g., 60

    private List<SectionDTO> sections;
    private List<StudentResultDTO> studentResults;
}
