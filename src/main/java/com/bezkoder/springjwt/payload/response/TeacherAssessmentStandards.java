package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherAssessmentStandards {
    public Integer standardId;
    private String standardName;
    private String standardStatus;
    private List<TeacherAssessmentSubjects> teacherAssessmentSubjects;
}
