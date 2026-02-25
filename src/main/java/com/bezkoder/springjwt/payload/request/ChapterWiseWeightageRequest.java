package com.bezkoder.springjwt.payload.request;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.SubjectMaster;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
public class ChapterWiseWeightageRequest {
    private Integer chapterWeightageId;
    private Integer entranceExamId;
    private Integer subjectId;
    private Integer chapterId;
    private Double weightage;
    private String status;
}
