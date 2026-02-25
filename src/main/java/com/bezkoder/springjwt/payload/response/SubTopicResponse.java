package com.bezkoder.springjwt.payload.response;


import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubTopicResponse {
    private Integer subTopicId;

    private String subTopicName;

    private Integer entranceExamId;

    private String entranceExamName;

    public Integer standardId;

    private String standardName;

    private Integer subjectId;

    private String subjectName;

    private Integer chapterId;

    private String chapterName;

    private Integer topicId;

    private String topicName;

    private Date date;

    private String status;
}
