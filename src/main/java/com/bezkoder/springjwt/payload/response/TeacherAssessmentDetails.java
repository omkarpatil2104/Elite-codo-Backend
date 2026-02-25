package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.Role;
import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherAssessmentDetails {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobile;
    private String status;
    private String profilePicture;
    private Date date;
    private String email;
    private String className;
    private Set<Role> roles = new HashSet<>();
    private Long creatorId;
    private String logoImage;
    private String watermarkImage;
    private List<TeacherAssessmentEntranceExams> teacherAssessmentEntranceExams;

    private Integer studentKeys;

    private Integer accessManagementId;
    private Boolean isPrint;
    private Boolean isOcr;
}
