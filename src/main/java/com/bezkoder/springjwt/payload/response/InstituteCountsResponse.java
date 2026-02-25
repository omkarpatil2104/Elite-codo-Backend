package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InstituteCountsResponse {
    private Integer totalUsers;
    private Integer totalActiveUsers;
    private Integer totalInActiveUsers;

    private Integer totalTeachers;
    private Integer totalActiveTeachers;
    private Integer totalInActiveTeachers;

    private Integer totalStudents;
    private Integer totalActiveStudents;
    private Integer totalInActiveStudents;

    private Integer totalParents;
    private Integer totalActiveParents;
    private Integer totalInActiveParents;

}
