package com.bezkoder.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long questionId;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private HashMap<Integer, String> answers = new HashMap<>();
    private String explanation;

//    public QuestionDTO(Long questionId, String question, String option1, String option2, String option3, String option4) {
//        this.questionId = questionId;
//        this.question = question;
//        this.option1 = option1;
//        this.option2 = option2;
//        this.option3 = option3;
//        this.option4 = option4;
//    }
//
//    public QuestionDTO(String question, String option1, String option2, String option3, String option4) {
//    }
}
