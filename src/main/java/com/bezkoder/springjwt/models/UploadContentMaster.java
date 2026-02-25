package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class UploadContentMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uploadContentId;

    private Long uploaderId;

    private Integer entranceExamId;

    public Integer standardId;

    private Integer subjectId;

    private Integer chapterId;

    private Integer topicId;

    private String contentType;

    private String url;

    private String type;

    private String title;

    private String description;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

    private String examYear;
}
