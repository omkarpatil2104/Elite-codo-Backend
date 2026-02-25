package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadContentChapterMasterResponse {
    private Integer id;
    private String name;
    private String icon;
    private List<UploadContentTopicMasterResponse> topics;
    private List<UploadContents> content;
}
