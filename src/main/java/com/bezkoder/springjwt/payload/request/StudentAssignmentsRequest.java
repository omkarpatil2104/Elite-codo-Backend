package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class StudentAssignmentsRequest {
    private Long studentId;
    private Long teacherId;
    private List<StudentEntranceExamRequest> entranceExamRequests;
}
