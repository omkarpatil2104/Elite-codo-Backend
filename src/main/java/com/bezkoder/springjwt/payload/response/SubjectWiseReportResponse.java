package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectWiseReportResponse {

    private String name;       // e.g., "Mathematics"
    private Double marks;         // e.g., 45
    private String grade;      // e.g., "A+"
    private String teacher;    // e.g., "Mr. Smith"
    private String remarks;
}
