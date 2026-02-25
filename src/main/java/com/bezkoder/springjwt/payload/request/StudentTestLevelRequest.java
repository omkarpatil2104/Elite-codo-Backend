package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class StudentTestLevelRequest {
    private Integer easy;
    private Integer medium;
    private Integer hard;
}
