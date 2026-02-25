package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResultDTO {
    private Long id;              // e.g., studentId or submissionId
    private String name;          // e.g., "John Doe"
    private double score;         // e.g., 78
    private String grade;         // e.g., "C"
    private String status;        // "Pass" or "Fail"
    private String submissionTime; // e.g., "2024-01-15 10:30 AM"

    private AnswerStats answers;   // breakdown of correct/incorrect/skipped

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerStats {
        private int correct;
        private int incorrect;
        private int skipped;
    }
}
