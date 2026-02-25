package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashBoardDetails {
    private Integer totalPendingCount;
    private Integer total;
    private String role;
    private String icon;
    private String nzIcon;
    private String color;
    private String message;
    private Integer trend;
    private List actions = new ArrayList();
}
