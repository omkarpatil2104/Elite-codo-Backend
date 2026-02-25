package com.bezkoder.springjwt.payload.request;

import com.bezkoder.springjwt.dto.QuestionDTO;
import lombok.Data;

import java.util.List;

@Data

public class ExamRequestDTO {
    private Long creatorId;
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
    private String examSetName;
    private int angle;
    private double opacity;
    private List<QuestionDTO> questions;
    private Boolean isPdf;
}