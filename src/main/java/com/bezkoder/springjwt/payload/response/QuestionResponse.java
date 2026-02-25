package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Getter
@Setter
public class QuestionResponse {

    private Integer questionId;

    private String standardName;

    private String chapterName;

    private Integer marks;

    private String question;

    private String option1;

    private String option2;

    private String option3;

    private String option4;

    private String yearOfAppearance;

    private Date date;

    private String status;

    private Set<String> multiAnswers;

    private String explanation;

    private String solution;

    private String questionCategory;

    public QuestionResponse(Integer questionId, String standardName, String chapterName, Integer marks, String question, String option1, String option2, String option3, String option4, String yearOfAppearance, Date date, String status, String solution, String questionCategory) {
        this.questionId = questionId;
        this.standardName = standardName;
        this.chapterName = chapterName;
        this.marks = marks;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.yearOfAppearance = yearOfAppearance;
        this.date = date;
        this.status = status;
        this.solution = solution;
        this.questionCategory = questionCategory;
    }


    public QuestionResponse(Integer questionId, String standardName, String chapterName, Integer marks, String question, String option1, String option2, String option3, String option4, String yearOfAppearance, Date date, String status, Set<String> multiAnswers, String solution, String questionCategory) {
        this.questionId = questionId;
        this.standardName = standardName;
        this.chapterName = chapterName;
        this.marks = marks;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.yearOfAppearance = yearOfAppearance;
        this.date = date;
        this.status = status;
        this.multiAnswers = multiAnswers;
        this.solution = solution;
        this.questionCategory = questionCategory;
    }

}
