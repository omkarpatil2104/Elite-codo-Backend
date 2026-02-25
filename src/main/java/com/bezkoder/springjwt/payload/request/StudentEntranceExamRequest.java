package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class StudentEntranceExamRequest {
    private Integer entranceExamId;
    private List<TeacherStandardRequest> standards;

}
