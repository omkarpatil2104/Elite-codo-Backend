package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class ThemeManagementRequest {
    private Integer themeId;

    private String themeName;

    private String colorCode;

    private Long id;

    private Date date;

    private String status;
}
