package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsersCountResponse {
    private String title;
    private String bgClass;
    private Integer total;
    private Integer active;
    private Integer inactive;
    private String icon;
    private Double trend;
}
