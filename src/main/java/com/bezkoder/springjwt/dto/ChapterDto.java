package com.bezkoder.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDto {
    private String name;
    private List<QuestionDTO> questions;
}
