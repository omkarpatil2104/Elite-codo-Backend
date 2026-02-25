package com.bezkoder.springjwt.payload.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentTopicResponse {
    private Integer id;
    private String name;
    private Integer questionCount;
}
