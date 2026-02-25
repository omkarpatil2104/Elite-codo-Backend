package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StudentGradeResponse {

    private Long id;
    private String name;
    private List<GradeResponse> grades;
}
