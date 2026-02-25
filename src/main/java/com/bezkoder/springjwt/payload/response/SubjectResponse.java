package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.SubjectMaster;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class SubjectResponse {

    public Integer standardId;

    private String standardName;

//    private Integer entranceExamId;
//
//    private String entranceExamName;

    List<SubjectMaster> subjectMasters = new ArrayList<>();
}
