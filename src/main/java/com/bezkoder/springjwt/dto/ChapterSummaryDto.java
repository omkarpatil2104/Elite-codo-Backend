package com.bezkoder.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterSummaryDto {
    private Integer chapterId;
    private String chapterName;
    private Long questionCount;
}
