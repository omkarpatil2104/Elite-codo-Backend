package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSummaryResponse {

    private Integer id;
    private String title;
    private int participantCount;
    private double avgScore;
    private double passRate;
    private double avgTime; // In minutes
}
