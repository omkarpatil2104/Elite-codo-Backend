package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerKeyRes {
    private String institute;
    private String address;
    private String contactDetails;
    private String examName;
    private String date;
    private String duration;
    private int totalMarks;
    private String examSetName;
    private HashMap<Integer, String> answers = new HashMap<>();

}
