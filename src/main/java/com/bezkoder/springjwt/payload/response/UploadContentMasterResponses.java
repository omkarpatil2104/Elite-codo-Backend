package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.models.UploadContentMaster;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadContentMasterResponses {
    private Integer uploadContentId;
    private EntranceExamMasterResponse entranceExamMaster;
    private StandardMaster standardMaster;
    private SubjectMastersResponse subjectMaster;
    private ChapterMasterResponse chapterMaster;
    private TopicMasterResponse topicMaster;
    private String contentType;
    private String url;
    private String type;
    private String title;
    private String description;
    private Date date;
    private String status;
    private Long uploaderId;
    private String examYear;

}
