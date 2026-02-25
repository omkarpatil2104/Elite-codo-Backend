package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ThemeManagementResponse {
    private Integer themeId;

    private String themeName;

    private String colorCode;

    private Long id;

    private String firstName;

    private String lastName;

    private Date date;

    private String status;
}
