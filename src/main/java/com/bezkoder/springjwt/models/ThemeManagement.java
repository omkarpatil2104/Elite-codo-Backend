package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class ThemeManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer themeId;

    private String themeName;

    private String colorCode;

    @ManyToOne
    private User user;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;
}
