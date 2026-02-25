package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllOfflineTestByIdResponse {

    private Integer id;

    private String name;

    private List<String> subjects;

    private Date date;

    private Double totalMarks;

    private String status;

    private LocalTime endTime;


}
