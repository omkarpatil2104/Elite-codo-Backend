package com.bezkoder.springjwt.payload.request;

import lombok.Data;
import java.util.Date;

@Data
public class TopicRequest {
    private Integer topicId;

    private String topicName;

    private Integer entranceExamId;

    public Integer standardId;

    private Integer subjectId;

    private Integer chapterId;

    private Date date;

    private String status;

}
