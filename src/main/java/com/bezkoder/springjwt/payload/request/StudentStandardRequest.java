package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class StudentStandardRequest {
    private Integer standardId;

    private List<Integer> subjectIds;
}
