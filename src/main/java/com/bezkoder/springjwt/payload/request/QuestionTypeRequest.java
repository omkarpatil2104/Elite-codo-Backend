package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class QuestionTypeRequest {
    private Integer questionTypeId;

    private String questionType;

    private Date date;

    private String status;
}
