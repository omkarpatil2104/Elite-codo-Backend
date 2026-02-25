package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long id;
    private String name;
    private  String email;
    private String userType;
    private String Status;
    private String registrationDate;
    private String contactNumber;
    private String avatarUrl;


}
