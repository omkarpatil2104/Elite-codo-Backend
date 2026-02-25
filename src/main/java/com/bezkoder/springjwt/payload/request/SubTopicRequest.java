package com.bezkoder.springjwt.payload.request;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubTopicRequest {
    private Integer subTopicId;

    private String subTopicName;

    private Integer entranceExamId;

    public Integer standardId;

    private Integer subjectId;

    private Integer chapterId;

    private Integer topicId;

    private Date date;

    private String status;
}
