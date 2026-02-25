package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportantQuestionDTO {
    private Integer  questionId;
    private String   testName;       // NEW
    private String   standardName;
    private String   chapterName;
    private Integer  marks;
    private String   question;
    private String   option1;
    private String   option2;
    private String   option3;
    private String   option4;
    private String   yearOfAppearance;
    private Date date;
    private String   status;
    private Set<String> multiAnswers;
    private String   explanation;
    private String   solution;
    private String   questionCategory;
}
