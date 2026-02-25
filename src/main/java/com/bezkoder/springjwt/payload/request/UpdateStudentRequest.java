package com.bezkoder.springjwt.payload.request;

import lombok.*;
import java.util.Date;

@Data
public class UpdateStudentRequest {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobile;
    private String email;
    private String address;
    private String profilePicture;
    private Date expiryDate;

    // ADD THIS ONE LINE
    private Integer teacherId;
}