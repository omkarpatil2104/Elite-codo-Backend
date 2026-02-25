package com.bezkoder.springjwt.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class QuestionFilterDTO {
    // For "global" search
    private String searchTerm;

    // For discrete filters

    private String exam;            // entranceExamMaster.name
    private String subject;         // subjectMaster.name
    private String chapter;         // chapterMaster.name
    private String topic;           // topicMaster.name
    private String status;
    private String subTopic;        // subTopicMaster.name
    private String questionType;    // questionType.name
    private String questionCategory;
    private String pattern;         // patternMaster.name
    private Boolean isPYQ;          // asked = true/false
}
