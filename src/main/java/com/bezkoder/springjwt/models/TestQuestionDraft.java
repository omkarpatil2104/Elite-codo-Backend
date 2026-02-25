package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class TestQuestionDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer draftId;
    private Integer questionId;
    private Long userId;
    private Integer entranceExamId;
    public Integer standardId;
    private Integer subjectId;
    private Integer chapterId;
    private Integer topicId;
    private Integer subTopicId;
    private Integer yearOfAppearanceId;
    private Integer questionTypeId;
    private Integer questionLevelId;
    private Integer patternId;
    private String questionCategory;
    private String typeOfTest;
}
