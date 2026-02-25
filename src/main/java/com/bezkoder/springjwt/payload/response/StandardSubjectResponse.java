package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StandardSubjectResponse {
    private Integer id;
    private String name;
    private List<StudentDataResponse> subjects;
}
