package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSummaryResponse {
    private Long id;
    private String name;
    private int testsCompleted;
    private double avgScore;
    private double highestScore;
    private int rank;
}
