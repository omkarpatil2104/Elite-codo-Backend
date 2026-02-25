package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountOfStudentTeacherQuestionResponse {
    private Integer studentCount;

    private Integer teacherCount;

    private Integer questionCount;
}
