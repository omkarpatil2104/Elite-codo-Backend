package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.inject.Named;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectRes1 {

    private String subjectName;
    private double passPercentage;
    private double avgScore;
    private int highestScore;
    private int lowestScore;
}
