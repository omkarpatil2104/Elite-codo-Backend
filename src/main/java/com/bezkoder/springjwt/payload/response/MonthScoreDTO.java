package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthScoreDTO {
    private String month;  // e.g. "Jan"
    private double score;  // e.g. 82
}