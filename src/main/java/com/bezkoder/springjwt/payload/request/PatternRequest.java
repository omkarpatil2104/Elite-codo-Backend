package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class PatternRequest {
    private Integer patternId;

    private String patternName;

    private String patternActualName;

    private String status;
}
