package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class TestTypeRequest {
    private Integer testTypeId;

    private String testTypeName;

    private Date date;

    private String status;
}
