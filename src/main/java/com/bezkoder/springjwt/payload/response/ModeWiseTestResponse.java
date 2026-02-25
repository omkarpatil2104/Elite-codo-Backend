package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeWiseTestResponse {
        private int id;
        private String title;
        private List<String> subject;
        private Date date;
        private int totalMarks;
        private String status;
    }