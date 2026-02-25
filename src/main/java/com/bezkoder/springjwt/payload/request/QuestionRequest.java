package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.util.Set;

@Data
public class QuestionRequest {
    private Integer questionId;

    private Long id;

    private Integer entranceExamId;

    private Integer standardId;

    private Integer subjectId;

    private Integer chapterId;

    private Integer topicId;

    private Integer subTopicId;

    private Integer yearOfAppearanceId;

    private Integer questionTypeId;

    private Integer questionLevelId;

    private Integer marks;

    private String question;

    private String option1;

    private String option2;

    private String option3;

    private String option4;

    private String yearOfAppearance;

    private String answer;

    private String explanation;

    private String status;

    private Set<String> multiAnswers;

    private Integer patternId;

    private String solution;

    private String questionCategory;

    private Boolean asked;
}
