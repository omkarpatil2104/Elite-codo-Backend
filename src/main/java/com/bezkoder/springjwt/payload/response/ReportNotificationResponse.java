package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ReportNotificationResponse {
    private Long id;
    private String title;
    private String content;
    private Date date;
    private String type;
    private String uploadedFile;
    private String reportType;

}
