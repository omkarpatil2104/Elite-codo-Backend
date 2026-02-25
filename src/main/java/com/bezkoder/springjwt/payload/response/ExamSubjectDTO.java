package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.dto.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExamSubjectDTO {
    private String name;
    private List<QuestionDTO> questions;

    public ExamSubjectDTO() {

    }
}
