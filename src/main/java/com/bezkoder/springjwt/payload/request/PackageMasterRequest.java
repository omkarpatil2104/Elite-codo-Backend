package com.bezkoder.springjwt.payload.request;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PackageMasterRequest {
    private Integer packageId;

    private String packageName;

    private Double packagePrize;

    private Integer days;

    private Date date;

    private String status;
}
