package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentActivityDTO {

    private String name;        // e.g. “Create Subject Wise Test”
    private String subject;     // e.g. “Mathematics”
    private String date;        // ISO-8601 string
    private String status;      // Completed / Graded / Excellent …
    private String statusColor; // green / blue / purple …
    private String icon;
}
