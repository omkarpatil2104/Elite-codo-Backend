package com.bezkoder.springjwt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentTestResultDTO {
    private int testId;
    private String testName;
    private LocalDateTime submittedAt;
    private String subject;
    private long totalMarks;
    private long obtainedMarks;
    private String typeOfTest;


}
