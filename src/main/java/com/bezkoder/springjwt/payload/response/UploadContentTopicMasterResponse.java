package com.bezkoder.springjwt.payload.response;

import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadContentTopicMasterResponse {
    private Integer id;
    private String name;
    private List<UploadContents> content;


}
