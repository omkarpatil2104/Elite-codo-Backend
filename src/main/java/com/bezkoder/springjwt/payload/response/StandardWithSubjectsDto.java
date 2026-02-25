package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class StandardWithSubjectsDto {
    private Integer standardId;
    private String  standardName;
    private List<SubjectDto> subjects = new ArrayList<>();
}
