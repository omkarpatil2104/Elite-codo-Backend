package com.bezkoder.springjwt.payload.request;

import lombok.Data;
import java.util.Date;

@Data
public class UploadContentRequest {

    private Long uploaderId;

    private Integer uploadContentId;

    private Integer entranceExamId;   // <-- change here

    private Integer standardId;       // <-- change here

    private Integer subjectId;        // <-- change here

    private Integer chapterId;        // <-- change here

    private Integer topicId;          // <-- change here

    private String contentType;

    private String url;

    private String type;

    private String title;

    private String description;

    private Date date;

    private String status;

    private String examYear;
}
