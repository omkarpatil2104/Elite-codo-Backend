package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllTestReportByTeachId {

    private Integer testId;
    private String examName;
    private List<String> subject;
    private Date date;
    private int totalStudents;
    private double passPercentage;
    private double averageScore;
    private double highestScore;
    private String status;
}
