package com.bezkoder.springjwt.payload.request;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.response.PatternMasterResponse;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class UpdateTeacherRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobile;
    private Long creatorId;
    private String profilePicture;
    private String email;
    private String logoImage;
    private String watermarkImage;
    private String className;
//    private Set<SubjectMaster> subjects;
//    private Set<EntranceExamMaster> entranceExamMasters;
//    private Set<StandardMaster> standardMasters;
    private String status;
    private Integer studentKeys;
    private String colorTheme;
    private Boolean printAccess;
    private Boolean ormSheetAccess;
    private Boolean editAccess;
    private Date expiryDate;
    private String address;
    private Set<Integer> patternIds;
}
