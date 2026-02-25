package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestQuestionsStudentResponse {
    private Integer testId;
    private String testName;
    private Date testDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double marks;
    private List<QuestionResponseForStudent> questionResponses;
    private Long createdBy;
}

