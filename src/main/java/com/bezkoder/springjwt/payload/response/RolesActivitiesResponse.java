package com.bezkoder.springjwt.payload.response;


import com.bezkoder.springjwt.models.Role;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RolesActivitiesResponse {
    private Long id;
    private String name;
    private String role;
    private String email;
    private Date lastActive;
    private String status;
}
