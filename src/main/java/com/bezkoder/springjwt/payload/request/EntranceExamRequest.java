package com.bezkoder.springjwt.payload.request;

import com.bezkoder.springjwt.models.StandardMaster;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class EntranceExamRequest {
    private Integer entranceExamId;

    private String image;

    private String entranceExamName;

    private Date date;

    private String status;

    private Set<Integer> standardIds = new HashSet<>();
}
