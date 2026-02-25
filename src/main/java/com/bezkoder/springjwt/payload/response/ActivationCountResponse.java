package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActivationCountResponse {
    private String title;

    private Integer count;

    private String icon;

    private String colorClass;

    private String bgClass;
}
