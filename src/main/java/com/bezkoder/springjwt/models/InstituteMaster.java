package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class InstituteMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer instituteId;

    private String instituteName;

    private String photo;

    private String address;

    private String email;

    private String logoImage;

    private String waterMarkImage;

    private String slogan;

    private Long createdBy;

    private Integer teacherKeys;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Temporal(TemporalType.DATE)
    private Date expiryDate;

    private String status;

    private Long registrationId;
}
