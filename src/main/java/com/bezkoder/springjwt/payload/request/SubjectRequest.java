package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Data
public class SubjectRequest {
    private Integer subjectId;
    private String subjectName;
    private String status;
    private Set<Integer> standardIds = new HashSet<>();
    private Integer entranceExamId;
}
