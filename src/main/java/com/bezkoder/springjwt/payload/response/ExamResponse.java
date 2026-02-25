package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {
    private Integer examId;
    private String examName;
    private Date date;
    private int totalStudents;
    private double passPercentage;
    private List<SubjectRes1> subjects;
}
