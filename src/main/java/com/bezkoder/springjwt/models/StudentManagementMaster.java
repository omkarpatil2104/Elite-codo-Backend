package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class StudentManagementMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentManagementId;

    private Long studentId;

    private Long teacherId;

    private Integer entranceExamId;

    private Integer standardId;

    private Integer subjectId;

    private Long createdBy;

    private String status;
}
