package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestAllInformationResponse {
    private Integer testId;
    private String testName;
    private Date startTime;
    private Date endTime;
    private Double marks;
    private Date testDate;
    private Integer testTypeId;
    private String testTypeName;
}
