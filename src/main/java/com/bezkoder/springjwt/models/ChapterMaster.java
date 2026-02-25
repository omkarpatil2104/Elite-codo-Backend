package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class ChapterMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chapterId;

    @Column(unique=true)
    private String chapterName;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

    private Double weightage;

    @ManyToOne
    private SubjectMaster subjectMaster;

    @ManyToOne
    private StandardMaster standardMaster;

    @ManyToOne
    private EntranceExamMaster entranceExamMaster;
}
