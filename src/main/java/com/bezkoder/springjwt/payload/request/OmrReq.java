package com.bezkoder.springjwt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmrReq {
    private String instituteName;
    private Integer totalQuestions;
    private Boolean isPdf;
}
