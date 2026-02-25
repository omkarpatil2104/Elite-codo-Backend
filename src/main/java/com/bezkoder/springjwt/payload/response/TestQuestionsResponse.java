package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
//@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestQuestionsResponse {
    private Integer questionId;
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
    private Integer subTopicId;
    private String subTopicName;
    private Integer yearOfAppearanceId;
    private String yearOfAppearance;
    private Integer questionTypeId;
    private String questionType;
    private Integer questionLevelId;
    private String questionLevel;
    private Integer patternId;
    private String patternName;
    private String patternActualName;
    private String status;

    private Integer questionCounts;

    //
    private Integer marks;

    private String question;

    private String option1;

    private String option2;

    private String option3;

    private String option4;


    private Date date;

//    private Set<String> multiAnswers;

    private String solution;

//    private String questionCategory;

    private String explanation;

    private Set<String> multiAnswers;

//

    public TestQuestionsResponse(Integer questionId, Integer entranceExamId, String entranceExamName, Integer standardId, String standardName, Integer subjectId, String subjectName, Integer chapterId, String chapterName, Integer topicId, String topicName, Integer subTopicId, String subTopicName, Integer yearOfAppearanceId, String yearOfAppearance, Integer questionTypeId, String questionType, Integer questionLevelId, String questionLevel, Integer patternId, String patternName, String patternActualName, String status, Integer marks, String question, String option1, String option2, String option3, String option4, Date date, String solution, String explanation) {
        this.questionId = questionId;
        this.entranceExamId = entranceExamId;
        this.entranceExamName = entranceExamName;
        this.standardId = standardId;
        this.standardName = standardName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.topicId = topicId;
        this.topicName = topicName;
        this.subTopicId = subTopicId;
        this.subTopicName = subTopicName;
        this.yearOfAppearanceId = yearOfAppearanceId;
        this.yearOfAppearance = yearOfAppearance;
        this.questionTypeId = questionTypeId;
        this.questionType = questionType;
        this.questionLevelId = questionLevelId;
        this.questionLevel = questionLevel;
        this.patternId = patternId;
        this.patternName = patternName;
        this.patternActualName = patternActualName;
        this.status = status;
        this.marks = marks;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.date = date;
        this.solution = solution;
        this.explanation = explanation;
    }

    public TestQuestionsResponse(Integer questionId, Set<String> multiAnswers) {
        this.questionId = questionId;
        this.multiAnswers = multiAnswers;
    }


//    public TestQuestionsResponse(Integer questionId, Integer entranceExamId, String entranceExamName, Integer standardId, String standardName, Integer subjectId, String subjectName, Integer chapterId, String chapterName, Integer topicId, String topicName, Integer subTopicId, String subTopicName, Integer yearOfAppearanceId, String yearOfAppearance, Integer questionTypeId, String questionType, Integer questionLevelId, String questionLevel, Integer patternId, String patternName, String patternActualName, String status, Integer marks, String question, String option1, String option2, String option3, String option4, Date date, Set<String> multiAnswers, String solution, String questionCategory) {
//        this.questionId = questionId;
//        this.entranceExamId = entranceExamId;
//        this.entranceExamName = entranceExamName;
//        this.standardId = standardId;
//        this.standardName = standardName;
//        this.subjectId = subjectId;
//        this.subjectName = subjectName;
//        this.chapterId = chapterId;
//        this.chapterName = chapterName;
//        this.topicId = topicId;
//        this.topicName = topicName;
//        this.subTopicId = subTopicId;
//        this.subTopicName = subTopicName;
//        this.yearOfAppearanceId = yearOfAppearanceId;
//        this.yearOfAppearance = yearOfAppearance;
//        this.questionTypeId = questionTypeId;
//        this.questionType = questionType;
//        this.questionLevelId = questionLevelId;
//        this.questionLevel = questionLevel;
//        this.patternId = patternId;
//        this.patternName = patternName;
//        this.patternActualName = patternActualName;
//        this.status = status;
//        this.marks = marks;
//        this.question = question;
//        this.option1 = option1;
//        this.option2 = option2;
//        this.option3 = option3;
//        this.option4 = option4;
//        this.date = date;
//        this.multiAnswers = multiAnswers;
//        this.solution = solution;
//        this.questionCategory = questionCategory;
//    }



}