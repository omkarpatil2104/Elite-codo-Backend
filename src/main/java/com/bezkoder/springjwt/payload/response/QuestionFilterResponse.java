package com.bezkoder.springjwt.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Data
@Getter
@Setter
public class QuestionFilterResponse {

    private Integer questionId;

    private String standardName;

    private String chapterName;

    private String subjectName;

    private String username;

    private String entranceExamName;

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

    private String solution;

    private String questionCategory;
}
