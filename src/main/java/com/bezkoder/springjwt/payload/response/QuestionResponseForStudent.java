package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionResponseForStudent {
    private Integer questionNumber;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private Integer marks;
    private boolean answered;
    private Set<String> multiAnswers;
}
