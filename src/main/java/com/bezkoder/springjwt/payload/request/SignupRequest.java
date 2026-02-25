package com.bezkoder.springjwt.payload.request;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignupRequest {
  private Long id;
  private String firstName;
  private String lastName;
  private String mobile;
  private Long creatorId;
  private String profilePicture;
  private Integer studentKeys;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private String role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  private String confirmPassword;

  private String logoImage;

  private String watermarkImage;

  private String className;

//  private Set<SubjectMaster> subjects;
//
//  private Set<EntranceExamMaster> entranceExamMasters;
//
//  private Set<StandardMaster> standardMasters;

  private Long teacherId;

  private String status;

  private String colorTheme;

  private Boolean printAccess;

  private Boolean ormSheetAccess;

  private Boolean editAccess;

  private String instituteName;

  private String address;

  private String photo;

  private Integer teacherKeys;

  private String slogan;

  private Long studentId;

  private String parentStatus;

  private Date expiryDate;

  private Set<Integer> patternIds = new HashSet<>();

  public Set<Integer> getPatternIds() {
    return patternIds;
  }

  public void setPatternIds(Set<Integer> patternIds) {
    this.patternIds = patternIds;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public String getLogoImage() {
    return logoImage;
  }

  public void setLogoImage(String logoImage) {
    this.logoImage = logoImage;
  }

  public String getWatermarkImage() {
    return watermarkImage;
  }

  public void setWatermarkImage(String watermarkImage) {
    this.watermarkImage = watermarkImage;
  }

//  public Set<SubjectMaster> getSubjects() {
//    return subjects;
//  }
//
//  public void setSubjects(Set<SubjectMaster> subjects) {
//    this.subjects = subjects;
//  }
//
//  public Set<EntranceExamMaster> getEntranceExamMasters() {
//    return entranceExamMasters;
//  }
//
//  public void setEntranceExamMasters(Set<EntranceExamMaster> entranceExamMasters) {
//    this.entranceExamMasters = entranceExamMasters;
//  }
//
//  public Set<StandardMaster> getStandardMasters() {
//    return standardMasters;
//  }
//
//  public void setStandardMasters(Set<StandardMaster> standardMasters) {
//    this.standardMasters = standardMasters;
//  }

  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

    public Integer getStudentKeys() {
    return studentKeys;
  }

  public void setStudentKeys(Integer studentKeys) {
    this.studentKeys = studentKeys;
  }

  public Long getTeacherId() {
    return teacherId;
  }

  public void setTeacherId(Long teacherId) {
    this.teacherId = teacherId;
  }



}
