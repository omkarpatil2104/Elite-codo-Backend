package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class SubTopicMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subTopicId;

    private String subTopicName;

    @ManyToOne
    private EntranceExamMaster entranceExamMaster;

    @ManyToOne
    private StandardMaster standardMaster;

    @ManyToOne
    private SubjectMaster subjectMaster;

    @ManyToOne
    private ChapterMaster chapterMaster;

    @ManyToOne
    private TopicMaster topicMaster;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;
}
