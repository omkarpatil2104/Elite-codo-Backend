package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.TopicMaster;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopicMasterResponse {
    private Integer topicId;

    private String topicName;

    private Date date;

    private String status;
}
