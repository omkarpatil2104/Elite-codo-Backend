package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails1 {

    private Long id;

    private String firstName;

    private String lastName;

    private String mobile;

    private String email;

    private String status;

    private String parentStatus;

    private String address;

    private String profilePicture;

    private Date expiryDate;

    private Integer teacherId;
    private String teacherName;
}
