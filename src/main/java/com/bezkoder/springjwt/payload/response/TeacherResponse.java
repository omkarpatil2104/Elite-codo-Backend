package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
}
