package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class InstituteRequest {
    private Integer instituteId;

    private String name;

    private String photo;

    private String address;

    private String email;

    private String logo;

    private String waterMarkImage;

    private String slogan;

    private Long createdBy;

    private Date createdDate;

    private Date expiryDate;

    private String status;
}
