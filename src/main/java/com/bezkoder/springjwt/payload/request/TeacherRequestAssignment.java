package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class TeacherRequestAssignment {
    private Long teacherId;
    private Integer entranceExamId;
    private List<TeacherStandardRequest> standards;
//    private List<TeacherEntranceExamRequest> entranceExamRequests;   //   for student assignment request
//    private Boolean isPrint;
//    private Boolean isOcr;

}
