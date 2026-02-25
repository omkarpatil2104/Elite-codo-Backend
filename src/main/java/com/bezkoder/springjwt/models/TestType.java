package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class TestType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testTypeId;

    private String testTypeName;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;
}
