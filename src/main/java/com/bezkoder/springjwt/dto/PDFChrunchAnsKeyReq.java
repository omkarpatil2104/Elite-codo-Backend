package com.bezkoder.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDFChrunchAnsKeyReq {
    private Long creatorId;      //
    private String instituteName;
    private String organization;
    private String examName;     //
    private String standard;
    private int totalQuestions;
    private int totalMarks;
    private String date;     //
    private String time;
    private String examSetName;       //
//    private List<Integer> questionIds;
    private Boolean isPdf;
}
