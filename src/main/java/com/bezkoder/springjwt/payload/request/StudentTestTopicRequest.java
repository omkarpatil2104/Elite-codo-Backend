package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class StudentTestTopicRequest {
    private Integer topicId;
    private Integer questionCount;
}
