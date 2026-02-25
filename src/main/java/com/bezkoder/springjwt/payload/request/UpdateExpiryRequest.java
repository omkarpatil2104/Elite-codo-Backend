package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateExpiryRequest {
    private Long userId;
    private Date expiryDate;


}
