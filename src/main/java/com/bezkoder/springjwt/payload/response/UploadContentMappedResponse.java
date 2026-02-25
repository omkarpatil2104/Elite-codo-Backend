package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UploadContentMappedResponse {
    private List<UploadContentSubjectMasterResponse> subjects;
}
