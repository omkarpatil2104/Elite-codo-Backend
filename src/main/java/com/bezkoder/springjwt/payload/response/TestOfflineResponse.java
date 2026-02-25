package com.bezkoder.springjwt.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TestOfflineResponse {

    private Integer id;
    private String name;
    private List<SubjectOfflineResponse> subjects;
    private List<StudentGradeResponse> students;


    public TestOfflineResponse(Integer id,String testName){
          this.id =id;
          this.name=testName;
        this.subjects = new ArrayList<>();  // Initialize to avoid null values
        this.students = new ArrayList<>();
    }

}
