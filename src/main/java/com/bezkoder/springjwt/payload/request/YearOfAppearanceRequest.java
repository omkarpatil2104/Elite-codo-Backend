package com.bezkoder.springjwt.payload.request;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class YearOfAppearanceRequest {
    private Integer yearOfAppearanceId;

    private String yearOfAppearance;

    private Date date;

    private String status;
}
