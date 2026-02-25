package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestGroupResponse {

    private String month;       // e.g. "Jan"
    private int count;          // number of tests created in that month
    private double averageScore;// average student score across those tests
}
