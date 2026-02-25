package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.payload.response.PatternMasterResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePatternResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
    private String status;
    private String profilePicture;
    private ERole role;
    private Integer questionCount;

    private Integer pendingQuestionCount;

    private Integer acceptedQuestionCount;

    private Integer rejectedQuestionCount;

    private Boolean printAccess;

    private Boolean ormSheetAccess;

    private Boolean editAccess;

    private Set<PatternMasterResponse> patternIds;

    public void setAssignedPatterns(Set<PatternMasterResponse> patternIds) {
        this.patternIds = patternIds;
    }

}
