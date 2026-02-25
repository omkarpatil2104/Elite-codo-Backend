package com.bezkoder.springjwt.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Data
@Entity
@ToString(exclude = {
        "assignedPatterns",
        "subjectMasters",
        "entranceExamMasters",
        "standardMasters",
        "teacher"
})
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
//  @NotBlank(message = " username is mandatory")
//  @Size(max = 20)
  private String username;
  private String firstName;
  private String lastName;
  private String mobile;
  private String status;
  private String profilePicture;

  @Column(name = "is_logged_in")
  private Boolean isLoggedIn=false;
  @Temporal(TemporalType.DATE)
  private Date date;
  @NotBlank(message = " email is mandatory")
  @Size(max = 50)
  @Email
  private String email;
  private Integer otp;

  @NotBlank
  @Size(max = 120)
  private String password;

  private String confirmPassword;

  private String className;
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(  name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

//  @Json
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "teacher_patterns",
          joinColumns = @JoinColumn(name = "teacher_id"),
          inverseJoinColumns = @JoinColumn(name = "pattern_id"))
  private Set<PatternMaster> assignedPatterns = new HashSet<>();

  public boolean hasRole(ERole role) {
    if (roles == null) return false;
    return roles.stream()
            .map(Role::getName)   // Role → ERole
            .anyMatch(role::equals);
  }
  public boolean isSuperAdmin() { return hasRole(ERole.ROLE_SUPER_ADMIN); }
  public boolean isAdmin()      { return hasRole(ERole.ROLE_ADMIN); }

  private Long creatorId;
  private String logoImage;
  private String watermarkImage;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_subjects",
  joinColumns = @JoinColumn(name = "user_id"),
  inverseJoinColumns = @JoinColumn(name = "subject_id"))
  private Set<SubjectMaster> subjectMasters = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_entrance_exams",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "entranceExamId"))
  private Set<EntranceExamMaster> entranceExamMasters = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_standard",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "standard_id"))
  private Set<StandardMaster> standardMasters = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "students_teachers",
  joinColumns = @JoinColumn(name = "student_id"),
  inverseJoinColumns = @JoinColumn(name = "teacher_id"))
  private Set<User> teacher  = new HashSet<>();

  private Integer studentKeys;

  private String colorTheme;

  private Boolean printAccess;

  private Boolean ormSheetAccess;

  private Boolean editAccess;

  private Long acceptBy;

  private Long studentId;

  private String parentStatus;

  private String address;

  // Institute fields
  private String instituteName;

  private Integer teacherKeys;

  private String slogan;

  private String photo;

  @Temporal(TemporalType.DATE)
  private Date expiryDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastLogin;

  @Temporal(TemporalType.TIMESTAMP)
  private Date currentLogin;

//  public void assignPattern(PatternMaster pattern) {
//    this.assignedPatterns.add(pattern);
//  }
//
//  public boolean hasAccessToPattern(PatternMaster pattern) {
//    return assignedPatterns.contains(pattern);
//  }


  public String getColorTheme() {
    return colorTheme;
  }

  public void setColorTheme(String colorTheme) {
    this.colorTheme = colorTheme;
  }

  public User() {
  }

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public User(Long id, String firstName, String lastName, String mobile, String email, String status) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.mobile = mobile;
    this.email = email;
    this.status = status;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Date getDate() {
    return date;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public Integer getOtp() {
    return otp;
  }

  public void setOtp(Integer otp) {
    this.otp = otp;
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

  public Set<SubjectMaster> getSubjectMasters() {
    return subjectMasters;
  }

  public void setSubjectMasters(Set<SubjectMaster> subjectMasters) {
    this.subjectMasters = subjectMasters;
  }

  public Set<EntranceExamMaster> getEntranceExamMasters() {
    return entranceExamMasters;
  }

  public void setEntranceExamMasters(Set<EntranceExamMaster> entranceExamMasters) {
    this.entranceExamMasters = entranceExamMasters;
  }

  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public Set<StandardMaster> getStandardMasters() {
    return standardMasters;
  }

  public void setStandardMasters(Set<StandardMaster> standardMasters) {
    this.standardMasters = standardMasters;
  }

  public Set<User> getTeacher() {
    return teacher;
  }

  public void setTeacher(Set<User> teacher) {
    this.teacher = teacher;
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

  public Integer getStudentKeys() {
    return studentKeys;
  }

  public void setStudentKeys(Integer studentKeys) {
    this.studentKeys = studentKeys;
  }

  public Boolean getPrintAccess() {
    return printAccess;
  }

  public void setPrintAccess(Boolean printAccess) {
    this.printAccess = printAccess;
  }

  public Boolean getOrmSheetAccess() {
    return ormSheetAccess;
  }

  public void setOrmSheetAccess(Boolean ormSheetAccess) {
    this.ormSheetAccess = ormSheetAccess;
  }

  public Long getAcceptBy() {
    return acceptBy;
  }

  public void setAcceptBy(Long acceptBy) {
    this.acceptBy = acceptBy;
  }

  public Long getStudentId() {
    return studentId;
  }

  public void setStudentId(Long studentId) {
    this.studentId = studentId;
  }

  public String getParentStatus() {
    return parentStatus;
  }

  public void setParentStatus(String parentStatus) {
    this.parentStatus = parentStatus;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Boolean getIsLoggedIn() {
    return isLoggedIn != null && isLoggedIn;
  }

  public void setIsLoggedIn(Boolean isLoggedIn) {
    this.isLoggedIn = isLoggedIn;
  }


  //  public AccessManagement getAccessManagement() {
//    return accessManagement;
//  }
//
//  public void setAccessManagement(AccessManagement accessManagement) {
//    this.accessManagement = accessManagement;
//  }
}
