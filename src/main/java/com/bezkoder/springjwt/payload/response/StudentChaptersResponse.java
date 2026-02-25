package com.bezkoder.springjwt.payload.response;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentChaptersResponse {
    private Integer id;
    private String name;
    private Integer topicCount;
    private Long questionCount;     // <-- NEW FIELD
    private List<StudentTopicResponse> topics;
}
