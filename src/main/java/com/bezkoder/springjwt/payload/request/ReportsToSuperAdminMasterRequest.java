package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class ReportsToSuperAdminMasterRequest {
    private Integer reportsToSuperAdminId;

    private String title;

    private Integer replyReportsToSuperAdminId;

    private Long senderId;

    private String attachments;

    private String category;

    private String description;

    private String email;//not required

    private String priority;

    private String type;

    private String status;
}
