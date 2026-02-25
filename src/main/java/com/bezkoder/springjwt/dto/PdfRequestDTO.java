package com.bezkoder.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfRequestDTO {

    /* ------------- pick header ------------- */
    private String institute;

    // meta-data for header
    private String examName;      // e.g. "NEET Physics"
    private String date;          // display-ready string, e.g. "15-Apr-2018"
    private String duration;      // "10:00 AM – 12:00 PM"
    private Integer totalMarks;   // 30

    // layout options
    private boolean twoColumn = false;   // true → 2-column, false → single

    private String watermark;     // nullable
    private float angle = 45f;    // default
    private float opacity = 0.1f; // optional – can add to JSON

    // the actual questions
    private List<QuestionDTO> questions;
}
