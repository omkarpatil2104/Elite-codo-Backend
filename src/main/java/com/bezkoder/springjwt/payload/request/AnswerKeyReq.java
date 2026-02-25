package com.bezkoder.springjwt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerKeyReq {
    private Long creatorId;  //
    private String institute;
    private String address;
    private String contactDetails;
    private String examName;  //
    private String date;  //
    private String duration;
    private int totalMarks;
    private String examSetName;   //
//    private List<Integer> questionIds;
    private Boolean isPdf;
}
