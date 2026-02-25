package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthWiseReportDTO {
    private String month;      // e.g. "Jan", "Feb"
    private double average;    // e.g. 82
    private double attendance; // e.g. 94
}