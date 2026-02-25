package com.bezkoder.springjwt.payload.request;


import lombok.Data;
import java.util.List;

@Data
public class NotificationGeneralRequest {
    private Long id;
    private String type;
    private String uploadedFile;
    private String title;
    private String content;
    private List<Long> students;
    private String reportType;
    private String toRole;
}
