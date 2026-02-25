package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherAssessmentSubjects {
    private Integer userManagementId;
    private Integer subjectId;
    private String subjectName;
    private String subStatus;

    public TeacherAssessmentSubjects(Integer subjectId, String subjectName, String subStatus) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subStatus = subStatus;
    }
}
