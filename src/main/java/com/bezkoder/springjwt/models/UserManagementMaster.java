package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class UserManagementMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userManagementId;
//    @Column(name="teacher_id")
    private Long teacherId;
    private Integer entranceExamId;
    private Integer standardId;
    private Integer subjectId;
    private String status;



}
