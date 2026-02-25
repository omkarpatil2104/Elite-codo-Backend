package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class StudentTestSubjectsRequest {
    private Integer subjectId;
    private List<StudentTestChaptersRequest> chapters;
}
