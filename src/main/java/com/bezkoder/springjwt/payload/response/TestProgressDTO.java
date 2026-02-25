package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestProgressDTO {
    private String date;          // formatted like "Jan 10"
    private double score;         // student's score
    private double classAverage;  // average score of the class on that test
}
