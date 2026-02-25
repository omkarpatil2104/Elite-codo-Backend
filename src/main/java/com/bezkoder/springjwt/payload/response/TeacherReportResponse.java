package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherReportResponse {

    private Long id;
    private String name;
    private int studentCount;
    private int testCount;
    private double avgScore;
    private double completionRate;
}
