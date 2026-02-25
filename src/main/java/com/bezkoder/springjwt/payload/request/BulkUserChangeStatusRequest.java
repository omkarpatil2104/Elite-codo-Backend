package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkUserChangeStatusRequest {
    private List<Long> userIds;
    private String status;
    private Long acceptBy;
}
