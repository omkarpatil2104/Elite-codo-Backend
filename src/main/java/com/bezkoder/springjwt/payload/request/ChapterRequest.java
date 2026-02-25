package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class ChapterRequest {
    private Integer chapterId;

    private Integer entranceExamId;

    private Integer subjectId;

    private Integer standardId;

    private String chapterName;

    private Date date;

    private String status;
}
