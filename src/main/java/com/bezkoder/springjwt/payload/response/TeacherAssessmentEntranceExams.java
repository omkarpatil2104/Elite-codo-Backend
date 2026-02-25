package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherAssessmentEntranceExams {
    private Integer entranceExamId;
    private String entranceExamName;
    private String entranceStatus;
    private List<TeacherAssessmentStandards> teacherAssessmentStandards;

}
