package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.SubjectMaster;
import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubjectMasterResponse {
    private Integer subjectId;

    private String subjectName;

    private Date date;

    private String subjectStatus;

    private Integer entranceExamId;

    private String entranceExamName;

    private List<StandardResponse1> standardResponses;

    public SubjectMasterResponse(Integer subjectId,
                                 String  subjectName,
                                 Date    date,
                                 String  subjectStatus,
                                 Integer entranceExamId,
                                 String  entranceExamName) {
        this.subjectId        = subjectId;
        this.subjectName      = subjectName;
        this.date             = date;
        this.subjectStatus    = subjectStatus;
        this.entranceExamId   = entranceExamId;
        this.entranceExamName = entranceExamName;
        this.standardResponses = new ArrayList<>(); // initialise empty list
    }

}
