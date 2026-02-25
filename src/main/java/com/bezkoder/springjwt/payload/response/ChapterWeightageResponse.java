package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChapterWeightageResponse {

    private Integer chapterWeightageId;

    private Double weightage;

    private String status;

    private Date date;

    private EntranceExamMasterResponse EntranceExamMaster;

    private SubjectMastersResponse subjectMaster;

    private ChapterMasterResponse chapterMaster;


}
