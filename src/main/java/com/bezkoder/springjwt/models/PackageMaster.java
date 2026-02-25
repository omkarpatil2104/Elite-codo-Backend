package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class PackageMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer packageId;

    private String packageName;

    private Double packagePrize;

    private Integer days;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;
}
