package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserManagementResponse {
    private List<TeacherAssessmentEntranceExams> teacherAssessmentEntranceExams;
}
