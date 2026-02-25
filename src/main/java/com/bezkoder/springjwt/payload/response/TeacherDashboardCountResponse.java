package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDashboardCountResponse {
    private long studentCount;
    private long parentCount;
    private long questionCount;
    private long testCount;
}
