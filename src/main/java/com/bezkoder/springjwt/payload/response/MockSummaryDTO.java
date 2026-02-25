package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MockSummaryDTO {
    private long   completedTests;
    private long   pendingTests;
    private double averageScore;
}
