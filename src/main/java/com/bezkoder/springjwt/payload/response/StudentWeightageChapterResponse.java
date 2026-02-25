package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentWeightageChapterResponse {
    private Integer chapterId;
    private Double weightage;
}
