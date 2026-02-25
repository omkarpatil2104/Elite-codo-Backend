package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class StandardRequest {
    public Integer standardId;

    private Integer entranceExamId;

    private String standardName;

    private Date date;

    private String status;
}
