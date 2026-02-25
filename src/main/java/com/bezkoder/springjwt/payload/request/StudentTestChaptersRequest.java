package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class StudentTestChaptersRequest {
    private Integer chapterId;
    private String chapterName;
    private Integer questionCount;
}
