package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSubReportResponse {
    private String studentName;
    private String studentClass;
    private String rollNumber;
    private String term;
    private String academicYear;
    private List<SubjectWiseReportResponse> subjects = new ArrayList<>();
    private double attendance;
    private double totalMarks;
    private double percentage;
    private int rank;
}
