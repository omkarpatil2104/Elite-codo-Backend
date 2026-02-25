package com.bezkoder.springjwt.payload.request;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionLevelRequest {
    private Integer questionLevelId;

    private String questionLevel;

    private Date date;

    private String status;
}
