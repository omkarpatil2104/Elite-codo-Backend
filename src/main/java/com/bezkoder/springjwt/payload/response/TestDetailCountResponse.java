package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDetailCountResponse {
    private long totalTestCount;
    private int completedTestCount;
    private int upcomingTestCount;
    private long studentCount;
}
