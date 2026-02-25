package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.SubjectMaster;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadContentSubjectMasterResponse {
    private Integer id;
    private String name;
    private String icon;
    private List<UploadContentChapterMasterResponse> chapters;
}
