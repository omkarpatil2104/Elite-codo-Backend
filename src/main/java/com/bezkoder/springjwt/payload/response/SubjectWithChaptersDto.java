package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.dto.ChapterSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectWithChaptersDto {
    private Integer subjectId;
    private String  subjectName;
    private List<ChapterSummaryDto> chapters = new ArrayList<>();

}
