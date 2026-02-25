package com.bezkoder.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDFCrunchReq {
    private Long creatorId;
    private String instituteName;
    private String organization;
    private String examName;
    private String standard;
    private String examSetName;
    private int totalQuestions;
    private int totalMarks;
    private boolean twoColumn;
    private boolean teacherKey;
    private String watermark;
    private int angle;
    private double opacity;
    private String date;
    private String time;
    private List<QuestionDTO> questions;
    private Boolean isPdf;
}
