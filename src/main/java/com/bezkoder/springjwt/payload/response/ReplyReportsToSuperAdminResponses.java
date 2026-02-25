package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReplyReportsToSuperAdminResponses {
    private Integer reportsToSuperAdminId;
    private Long userId;
    private String userType;
    private String userName;
    private String userAvatar;
    private String message;
    private Date date;
    private String status;
    private String priority;
    private String attachments;
    private String email;
    private ReplyReportsToSuperAdminResponse reply;
}
