package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.models.UserManagementMaster;
import com.bezkoder.springjwt.payload.request.*;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.services.impl.ProfilePatternResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    MainResponse updateStudent(UpdateStudentRequest updateStudentRequest);

    UserDetails1 getById(Long id);

    List<UserDetails> getAll();

    void deleteAll();


    List<UserDetails> getRoleWiseList(String role);

    List<UserDetails> getAllActiveRoleWiseList(String role);

    MainResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    MainResponse newPassword(NewPasswordRequest newPasswordRequest);

    MainResponse changePasswordRequest(ChangePasswordRequest changePasswordRequest);

    User userAllDetails(Long id);

    List<TeacherResponse> getSubjectWiseTeachers(Integer subjectId);

    Integer studentCount();

    Integer teacherCount();

    MainResponse updateTeacher(UpdateTeacherRequest updateTeacherRequest);

    StudentDetailsResponse studentDetailsById(Long id);

    List<ProfilePatternResponse> allTeachers();

    List<UserDetails> allStudents();

    CountOfStudentTeacherQuestionResponse countOfStudentTeacherQuestion();

    MainResponse teacherAssignments(TeacherRequestAssignment teacherRequestAssignment);

    TeacherAssessmentDetails teacherAssessmentDetails(Long id);

    TeacherMappedData teacherMappedData(Long id);

    VerificationOTPResponse VerificationOtp(VerificationOTPRequest verificationOtpReq);

    MainResponse deleteUser(Long id);

    MainResponse updateUserPassword(NewPasswordRequest newPasswordRequest);

    MainResponse addStudentAssignments(StudentAssignmentsRequest studentAssignmentsRequest);

    MainResponse addTeacherAssignments(TeacherRequestAssignment teacherRequestAssignment);

    MainResponse updateTeacherAssignments(TeacherRequestAssignment teacherRequestAssignment);

    MainResponse deleteMappedDataOfTeacher(Long id, Integer entranceExamId, Integer standardId,String role);

    StudentMappedData studentMappedData(Long id);

    List<PendingRolesList> pendingTeacherList();

    List<PendingRolesList> pendingStudentList();

    List<PendingRolesList> pendingParentList();

    List<PendingRolesList> pendingAdminList();

    MainResponse acceptUser(Long id, Long acceptby);

    List<DashBoardDetails> dashBoardDetails();

    UserManagementResponse teacherIdWiseAccessManagements(Long id);

    List<UsersCountResponse> usersCounts();

    List<ActivationCountResponse> activationCounts();

    MainResponse bulkUserChangeStatus(BulkUserChangeStatusRequest bulkUserChangeStatusRequest);

    UserManagementResponse userIdAndEntranceExamIdWiseData(Long id, Integer entranceExamId);

    MainResponse addStudentAssignment(TeacherRequestAssignment teacherRequestAssignment);

    UserManagementResponse studentIdWiseAccessManagements(Long id);

    MainResponse updateStudentAssignments(TeacherRequestAssignment teacherRequestAssignment);

    UserManagementResponse studentIdAndEntranceExamIdWiseData(Long id, Integer entranceExamId);

    ParentDetails studentWiseParent(Long id);

    MainResponse acceptParent(Long id, Long acceptby);

    MainResponse updateParent(ParentInformationUpdateRequest parentInformationUpdateRequest);

    MainResponse bulkParentStatusChange(BulkParentStatusChangeRequest bulkParentStatusChangeRequest);

    List<RolesActivitiesResponse> rolesActivities();

    MainResponse updateStatus(UpdateStatusRequest updateStatusRequest);

    List<UserDetails> allActiveAdmins(String role);

    List<InstituteDetailsResponse> getAllActiveInstitutes(String role);

    InstituteDetailsResponse getInstituteById(Long id);

    MainResponse deleteInstitute(Long instituteId);

    MainResponse updateInstitute(UpdateInstituteRequest updateInstituteRequest);

//    List<InstituteDetailsResponse> getAllPendingInstitute();

    List<InstituteDetailsResponse> getAllInActiveInstitutes();

    InstituteCountsResponse countsOfInstitutesUsers(Long id);

    List<PendingRolesList> pendingInstituteList();

    List<UserDetails> allActiveInstitutes(String role);

    List<SubjectMastersResponse> userAndEntranceExamAndStandardIdWiseSubjects(Long id, Integer entranceExamId, Integer standardId);

    Set<StandardMasterResponse> userAndEntranceExamIdWiseStandards(Long id, Integer entranceExamId);

    Set<EntranceExamResponse> teacherWiseEntranceExams(Long id);

    Set<EntranceExamResponse> studentEntranceExams(Long id);

    List<StandardSubjectResponse> studentData(Integer entranceExamId, Long id);

     ResponseEntity<?> registerUser(SignupRequest signUpRequest);

//    List<String> processBulkSignup(MultipartFile file,Long creatorId);

    List<String> processBulkSignup1(MultipartFile file,Long creatorId) throws IOException;

     User createUserFromRequest(SignupRequest signupRequest);

//    CompletableFuture<Void> sendEmailAsync(User user, String password);

    void deleteAllByIds(List<Long> userIds);

    List<UserDetails> getAllStudentByTeachId(Long id);

    UserInfo getInfoById(Long id);

    List<StandardResponse1> findSubjectsAssignedToStudentId(Long id);

    List<TestNameResponse> getAllTestByStudent(Long id);

    TeacherDashboardCountResponse getTeacherDashboardCounts(Long teacherId);

    List<ProfilePatternResponse> allTeachersByCreatorId(Long id);

    List<UserDetails> allStudentsByCreatorId(Long id);

    List<InstituteDetailsResponse> getAllActiveInstitutesById(String role, Long id);

    List<UserStatResponse> getUserStats(Long id);

    List<ExpiringUserResponse> getUsersExpiringInNextDays(int i,Long id);

    void updateSubscription(Long userId, Date expiryDate);

    UserAccess getEditAccessById(Long id);
    void assignTeacherToStudent(Long studentId, Long teacherId);

//    MainResponse teacherAssignmentsUpdate(TeacherRequestAssignment teacherRequestAssignment);
}
