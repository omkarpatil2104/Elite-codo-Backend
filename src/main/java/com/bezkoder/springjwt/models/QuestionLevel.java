package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class QuestionLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionLevelId;

    private String questionLevel;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;
}
