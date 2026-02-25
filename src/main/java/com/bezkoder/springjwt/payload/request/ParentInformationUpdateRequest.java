package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class ParentInformationUpdateRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String colorTheme;
    private String address;
}
