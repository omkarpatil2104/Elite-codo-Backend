package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.ERole;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PendingRolesList {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private Long creatorId;
    private ERole role;
    private Date date;
    TeacherResponse creator;

    public PendingRolesList(Long id, String firstName, String lastName, String email, String status, Long creatorId, ERole role, Date date) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.creatorId = creatorId;
        this.role = role;
        this.date = date;
    }
}
