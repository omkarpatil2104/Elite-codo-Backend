package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatResponse {
    private String title;
    private String bgClass;
    private long total;
    private long active;
    private long inactive;
    private String icon;
    private double trend;
}
