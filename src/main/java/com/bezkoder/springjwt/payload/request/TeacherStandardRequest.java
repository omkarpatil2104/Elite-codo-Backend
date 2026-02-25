package com.bezkoder.springjwt.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeacherStandardRequest {

    private Integer standardId;

    private List<Integer> subjectIds;

}
