package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class TopicMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer topicId;

    private String topicName;

    @ManyToOne
    private EntranceExamMaster entranceExamMaster;

    @ManyToOne
    private StandardMaster standardMaster;

    @ManyToOne
    private SubjectMaster subjectMaster;

    @ManyToOne
    private ChapterMaster chapterMaster;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

}
