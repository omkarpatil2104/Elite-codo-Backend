package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionTypeId;

    private String questionType;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;
}
