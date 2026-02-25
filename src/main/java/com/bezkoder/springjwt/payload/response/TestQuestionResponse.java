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
public class TestQuestionResponse {
    private Integer testId;
    private String testName;
    private Date testDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double marks;
    private List<QuestionResponse> questionResponses;
    private Long createdBy;

}
