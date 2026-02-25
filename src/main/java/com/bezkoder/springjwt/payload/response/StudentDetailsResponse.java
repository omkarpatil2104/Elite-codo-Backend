package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.models.User;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentDetailsResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String mobile;

    private String status;

    private String profilePicture;

    private Date date;

    private Date expiryDate;

    private String email;

    private Long creatorId;

    private String colorTheme;

    private Set<SubjectMaster> subjectMasters;

    private Set<EntranceExamMaster> entranceExamMasters;

    private Set<StandardMaster> standardMasters;

    private TeacherResponse teacher;

    private String address;
}
