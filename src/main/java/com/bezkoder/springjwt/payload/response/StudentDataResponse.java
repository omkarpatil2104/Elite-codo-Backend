package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentDataResponse {
    private Integer id;
    private String name;
    private List<StudentChaptersResponse> chapters;
}
