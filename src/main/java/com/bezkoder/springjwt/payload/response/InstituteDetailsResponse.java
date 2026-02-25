package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstituteDetailsResponse {
    private Long id;
    private String instituteName;
    private Integer teacherKeys;
    private String slogan;
    private String photo;
    private Date date;
    private Date expiryDate;
    private String address;
    private Long creatorId;
    private String logoImage;
    private String watermarkImage;
    private String email;
    private String mobile;
    private String status;
}
