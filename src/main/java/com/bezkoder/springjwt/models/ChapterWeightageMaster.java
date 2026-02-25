package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class ChapterWeightageMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chapterWeightageId;

    private Integer entranceExamId;

    private Integer subjectId;

    private Integer chapterId;

    private Double weightage;

    private String status;

    @Temporal(TemporalType.DATE)
    private Date date;
}
