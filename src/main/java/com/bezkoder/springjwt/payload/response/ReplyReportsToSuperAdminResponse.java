package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReplyReportsToSuperAdminResponse {
    private Integer reportsToSuperAdminId;
    private String message;
    private Date date;
    private String status;
    private String attachments;
}
