package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreRangeDTO {

    private String range;  // e.g. "0-20%"
    private long count;    // number of submissions in this bucket
}
