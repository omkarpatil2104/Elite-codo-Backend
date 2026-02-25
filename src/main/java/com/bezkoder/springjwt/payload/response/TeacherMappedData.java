package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherMappedData {
    private Long id;
    private List<TeacherAssessmentEntranceExams> teacherAssessmentEntranceExams;

    private Integer studentKeys;

//    private Integer accessManagementId;
//    private Boolean isPrint;
//    private Boolean isOcr;
}
