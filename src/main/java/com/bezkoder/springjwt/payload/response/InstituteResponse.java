package com.bezkoder.springjwt.payload.response;

import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstituteResponse {
    private Integer instituteId;

    private String name;

    private String photo;

    private String address;

    private String email;

    private String logo;

    private String waterMarkImage;

    private String slogan;

    private Long createdBy;

    private Date createdDate;

    private Date expiryDate;

    private String status;
}
