package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPerformanceDTO {

    private Long id;           // e.g., 45 (studentId)
    private String rollNo;     // e.g., "001"
    private String name;       // e.g., "John Doe"
    private String clazz;      // e.g., "X-A"
    private double average;    // e.g., 85
    private String status;     // e.g., "Excellent"
    private double attendance; // e.g., 95
    private boolean expand;    // default false

    // Month-wise trend
    private List<MonthScoreDTO> trend;

    // Subject-wise results
    private List<SubjectResultDTO> results;
}