package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportsToSuperAdminResponse {
    private Integer reportsToSuperAdminId;
    private Long userId;
    private String userType;
    private String userName;
    private String userAvatar;
    private String message;
    private Date date;
    private String status;
    private String priority;

    public ReportsToSuperAdminResponse(Long userId, String userType, String userName, String userAvatar, String message, Date date, String status, String priority) {
        this.userId = userId;
        this.userType = userType;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.message = message;
        this.date = date;
        this.status = status;
        this.priority = priority;
    }
}
