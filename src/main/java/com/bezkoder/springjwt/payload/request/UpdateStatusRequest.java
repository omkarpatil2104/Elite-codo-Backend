package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class UpdateStatusRequest {
    private Long id;
    private String status;
}
