package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopicResponse {
    private Integer topicId;

    private String topicName;

    private Integer entranceExamId;

    public Integer standardId;

    private Integer subjectId;

    private Integer chapterId;

    private Date date;

    private String status;
}
