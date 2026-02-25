package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResultDTO {

    private Long id;         // e.g., 5 (testId or submissionId if needed)
    private String subject;  // e.g. "Mathematics"
    private double marks;    // e.g. 85
    private double total;    // e.g. 100
    private String grade;    // e.g. "A"
    private String date;     // e.g. "2025-01-15"
    private double improvement; // e.g. 5
}