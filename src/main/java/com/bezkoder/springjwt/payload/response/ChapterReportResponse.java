package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterReportResponse {
    private int id;            // e.g., "ch1"
    private String name;          // e.g., "Mechanics"
    private int questionCount;    // e.g., 280
    private int topicCount;       // e.g., 8
}