package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestMasterResponse {
    private Integer testId;

    private String testName;

    private String status;

    private LocalTime startTime;

    private LocalTime endTime;

    private Date testDate;

}
