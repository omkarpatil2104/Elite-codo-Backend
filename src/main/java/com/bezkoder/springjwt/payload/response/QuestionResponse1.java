package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionResponse1 {
    private Integer questionId;

    private Long id;

    private Integer entranceExamId;

    private String entranceExamName;

    private Integer standardId;

    private String standardName;

    private Integer subjectId;

    private String subjectName;

    private Integer chapterId;

    private String chapterName;

    private Integer topicId;

    private String topicName;

    private Integer subTopicId;

    private String subTopicName;

    private Integer yearOfAppearanceId;

    private String yearOfAppearance;

    private Integer questionTypeId;

    private String questionType;

    private Integer questionLevelId;

    private String questionLevel;

    private Integer marks;

    private String question;

    private String option1;

    private String option2;

    private String option3;

    private String option4;

    private String answer;

    private String explanation;

    private String status;

    private Set<String> multiAnswers;

    private Integer patternId;

    private String patternName;

    private String solution;
}
