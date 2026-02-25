package com.bezkoder.springjwt.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class ExamResponseDTO {
    private String institute;
    private String address;
    private String contactDetails;
    private String examName;
    private String date;
    private String duration;
    private int totalMarks;
    private boolean twoColumn;
    private boolean teacherKey;
    private String watermark;
    private int angle;
    private double opacity;
    private List<ExamSubjectDTO> subjects;
    private Boolean isPdf;
}