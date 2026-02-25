package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDataDTO {
    private String name;
    private double score;         // student’s average % in this subject
    private double classAverage;  // class average % in this subject
}
