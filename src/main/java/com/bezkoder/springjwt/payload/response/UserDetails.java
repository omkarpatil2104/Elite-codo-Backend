package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetails {
    private Long id;

    private String firstName;

    private String lastName;

    private String mobile;

    private String email;

    private String status;

    private String parentStatus;

    private String address;


}
