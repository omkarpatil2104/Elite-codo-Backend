package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class TestQuestionRequest {
    private Long createdBy;
    private Integer questionId;
    public Integer standardId;
    private Integer subjectId;
    private Integer chapterId;
    private Integer entranceExamId;
    private Integer topicId;
    private Integer subTopicId;
    private Integer yearOfAppearanceId;
    private Integer questionTypeId;
    private Integer questionLevelId;
    private Integer patternId;
    private String questionCategory;
    private Boolean used;
//    private Boolean notUsed;
    private String pyq;

//    --------------  for random and count wise questions api
    private String type;   // --> Random / Serial
//    private Integer count;
    private Integer start;
    private Integer end;

}
