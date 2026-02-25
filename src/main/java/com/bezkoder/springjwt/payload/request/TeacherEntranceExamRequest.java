package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class TeacherEntranceExamRequest {
    private Integer entranceExamId;
    private List<TeacherStandardRequest> standards;
}
