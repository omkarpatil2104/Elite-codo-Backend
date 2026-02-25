package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class YearOfAppearance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer yearOfAppearanceId;

    private String yearOfAppearance;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;
}
