package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkParentStatusChangeRequest {
    private Long acceptBy;
    private List<Long> userIds;
    private String status;
}
