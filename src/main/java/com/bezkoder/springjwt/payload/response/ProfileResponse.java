package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.ERole;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String status;
    private String profilePicture;
    private ERole role;
    private Integer questionCount;

    private Integer pendingQuestionCount;

    private Integer acceptedQuestionCount;

    private Integer rejectedQuestionCount;

    private Boolean printAccess;

    private Boolean ormSheetAccess;

    private Boolean editAccess;

    public ProfileResponse(Long id, String firstName, String lastName, String mobile, String email, String status, String profilePicture, Boolean editAccess, ERole role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.status = status;
        this.profilePicture = profilePicture;
        this.editAccess = editAccess;
        this.role = role;
    }
}
