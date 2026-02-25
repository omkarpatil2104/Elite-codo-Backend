package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.SubjectMaster;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfflineTestQueryResponse {

    private Integer id;

    private String name;

    private List<SubjectMasterResponse> subjects;

    private Date date;

    private Double totalMarks;

    private String status;

    private LocalTime endTime;


    public OfflineTestQueryResponse(Integer id, String name, Date date, Double totalMarks, String status, LocalTime endTime) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.totalMarks = totalMarks;
        this.status = status;
        this.endTime = endTime;
    }
}
