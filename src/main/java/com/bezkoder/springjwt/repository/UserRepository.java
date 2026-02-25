package com.bezkoder.springjwt.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.response.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    // Add this method to your UserRepository
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.teacher t " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithTeacher(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.creatorId = :instituteId ")
    List<User> findStudentsByInstituteId(@Param("instituteId") Long instituteId);

    // Students assigned to teachers in that institute
    @Query("SELECT s FROM User s JOIN s.teacher t WHERE t.creatorId = :instituteId ")
    List<User> findStudentsByTeacherInstituteId(@Param("instituteId") Long instituteId);

    @Query(
            "SELECT u.id AS id, "
                    + "       CONCAT(u.firstName, ' ', u.lastName) AS name "
                    + "FROM   User u "
                    + "JOIN   u.roles r "
                    + "WHERE  u.creatorId    = :instituteId "
                    + "  AND  r.name         = 'ROLE_TEACHER'"
    )
    List<TeacherIdNameProjection> findTeachersByInstituteId(
            @Param("instituteId") Long instituteId
    );


    // 1) Find immediate children by creatorId
    @Query("SELECT u.id FROM User u WHERE u.creatorId = :creatorId")
    List<Long> findIdsByCreatorId(@Param("creatorId") Long creatorId);

    // 2) Count users with a certain role among a set of user IDs
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r " +
            "WHERE u.id IN :userIds AND r.name = :roleName")
    long countByIdsAndRole(@Param("userIds") Set<Long> userIds,
                           @Param("roleName") ERole roleName);

    // 3) Count "Active" users with a certain role among a set of user IDs
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r " +
            "WHERE u.id IN :userIds " +
            "AND r.name = :roleName " +
            "AND u.status = :status")
    long countByIdsAndRoleAndStatus(@Param("userIds") Set<Long> userIds,
                                    @Param("roleName") ERole roleName,
                                    @Param("status") String status);

    Optional<User> findByUsername(String username);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

//  @Query("""
//       SELECT u.email AS email, u.mobile AS mobile
//         FROM User u
//        WHERE (   (:emails  IS NOT NULL AND u.email  IN :emails )
//               OR (:mobiles IS NOT NULL AND u.mobile IN :mobiles))
//       """)
//  List<EmailMobilePair> findExistingEmailsAndMobiles(
//          @Param("emails")  List<String> emails,
//          @Param("mobiles") List<String> mobiles);

    @Query("SELECT u.email , u.mobile FROM User u WHERE u.email IN :emails OR u.mobile IN :mobiles")
    List<Object[]> findExistingEmailsAndMobiles(@Param("emails") List<String> emails,
                                                @Param("mobiles") List<String> mobiles);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE :teacher MEMBER OF u.teacher AND r.name = 'ROLE_STUDENT' AND  u.status IN ('Active','Pending')")
    Long countStudentsByTeacher(@Param("teacher") User creatorId);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE u.creatorId = :creatorId AND r.name = 'ROLE_PARENT'")
    long countParentsByTeacher(@Param("creatorId") Long creatorId);

//  @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE u.creatorId = :creator AND r.name = 'ROLE_TEACHER'")
//  Long countTeachersByCreator(@Param("creator") User creator);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE u.creatorId = :creatorId AND r.name = 'ROLE_TEACHER'")
    Long countTeachersByCreator(@Param("creatorId") Long creatorId);


    // =================================================
// ROLE RESOLUTION (USED BY MessageServiceImpl)
// =================================================
    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :userId")
    List<ERole> getRolesByUserId(@Param("userId") Long userId);




//  @Modifying
//  @Transactional
//  void saveAll(List<User> users);

    Boolean existsByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.email IN :emails")
    List<String> findExistingEmails(@Param("emails") List<String> emails);

    @Query("SELECT u.mobile FROM User u WHERE u.mobile IN :mobiles")
    List<String> findExistingMobiles(@Param("mobiles") List<String> mobiles);

    @Query("select new com.bezkoder.springjwt.payload.response.UserDetails(u.id,u.firstName,u.lastName,u.mobile,u.email,u.status,u.parentStatus,u.address) from User as u ")
    List<UserDetails> getAll();

    @Query("SELECT new com.bezkoder.springjwt.payload.response.UserDetails(u.id, u.firstName, u.lastName, u.mobile, u.email,u.status,u.parentStatus,u.address) " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = :role")
    List<UserDetails> getRoleWiseList(@Param("role") ERole role);


    @Query("SELECT new com.bezkoder.springjwt.payload.response.TeacherResponse(u.id, u.firstName, u.lastName, u.mobile, u.email) " +
            "FROM User u " +
            "JOIN u.subjectMasters sm " +
            "JOIN u.roles r " +
            "WHERE sm.id = :subjectId " +
            "AND r.name = 'ROLE_TEACHER'")
    List<TeacherResponse> getSubjectWiseTeachers(@Param("subjectId") Integer subjectId);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.UserDetails(u.id, u.firstName, u.lastName, u.mobile, u.email,u.status,u.parentStatus,u.address) " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = :roleEnum AND u.status = 'Active'")
    List<UserDetails> getAllActiveRoleWiseList(@Param("roleEnum") ERole roleEnum);

//  @Query("select count(u) from User as u join u.roles r where r.name='ROLE_STUDENT' and u.status!='Deleted' and u.status!='Pending'")
//  Integer studentCount();

    @Query("select count(u) from User as u join u.roles r where r.name = 'ROLE_STUDENT' and u.status != 'Deleted' and u.status != 'Pending' and u.status != 'Rejected'")
    Integer studentCount();



    @Query("select count(u) from User as u join u.roles r where r.name='ROLE_TEACHER' and u.status!='Deleted' and u.status!='Pending'")
    Integer teacherCount();

    @Query("select u from User as u where u.email=:username or u.mobile=:username")
    Optional<User> findByMobileOrEmail(@Param("username") String username);

    boolean existsByMobile(String mobile);
    @Query("SELECT new com.bezkoder.springjwt.payload.response.ProfileResponse(u.id, u.firstName, u.lastName, u.mobile, u.email, u.status, u.profilePicture, u.editAccess, r.name) " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = 'ROLE_TEACHER' " +
            "ORDER BY u.id DESC")
    List<ProfileResponse> allTeachers();




    @Query("SELECT new com.bezkoder.springjwt.payload.response.UserDetails(u.id, u.firstName, u.lastName, u.mobile, u.email,u.status,u.parentStatus,u.address) " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = 'ROLE_STUDENT' order by u.id desc")
    List<UserDetails> allStudents();


    @Query("SELECT s FROM User u JOIN u.subjectMasters s WHERE u.id = :userId AND s.subjectId = :subjectId")
    List<SubjectMaster> findSubjectsByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Integer subjectId);


    @Query("SELECT u FROM User u JOIN u.subjectMasters s JOIN u.standardMasters sm WHERE u.id = :userId AND sm.standardId = :standardId")
    User getUserIdAndStandardIdWiseSubjects(@Param("userId") Long userId, @Param("standardId") Integer standardId);


    @Query("SELECT s FROM User u JOIN u.subjectMasters s WHERE u.id = :userId")
    List<SubjectMaster> getUserIdWiseSubjects(@Param("userId") Long id);

    @Query("SELECT s FROM User u JOIN u.subjectMasters s WHERE u.id = :userId")
    List<SubjectMaster> getUserIdWiseSubjects1(@Param("userId")Long id);

    @Query(value = "select usm.subject_id FROM user_subjects AS usm  WHERE usm.user_id=:id",nativeQuery = true)
    List<Integer> getSubjectListById(@Param("id") Long id);

    @Query(value = "select uee.entrance_exam_id FROM user_entrance_exams AS uee  WHERE uee.user_id=:id",nativeQuery = true)
    List<Integer> getEntraceExamByUserId(@Param("id") Long id);

    @Query(value = "select us.standard_id FROM user_standard AS us  WHERE us.user_id=:id",nativeQuery = true)
    List<Integer> getStandardByUserId(@Param("id") Long id);

    @Query("SELECT u.entranceExamMasters FROM User u WHERE u.id = :userId")
    Set<EntranceExamMaster> findEntranceExamMastersByUserId(@Param("userId") Long userId);


    @Query("select new com.bezkoder.springjwt.payload.response.PendingRolesList(u.id,u.firstName,u.lastName,u.email,u.status,u.creatorId,r.name,u.date)  from User as u join u.roles r where r.name='ROLE_TEACHER' and u.status='pending' order by u.date desc")
    List<PendingRolesList> pendingTeacherList();

    @Query("select new com.bezkoder.springjwt.payload.response.PendingRolesList(u.id,u.firstName,u.lastName,u.email,u.status,u.creatorId,r.name,u.date)  from User as u join u.roles r where r.name='ROLE_STUDENT' and u.status='pending' order by u.date desc")
    List<PendingRolesList> pendingStudentList();

    @Query("select new com.bezkoder.springjwt.payload.response.PendingRolesList(u.id,u.firstName,u.lastName,u.email,u.status,u.creatorId,r.name,u.date)  from User as u join u.roles r where r.name='ROLE_PARENT' and u.status='pending' order by u.date desc")
    List<PendingRolesList> pendingParentList();

    @Query("select new com.bezkoder.springjwt.payload.response.PendingRolesList(u.id,u.firstName,u.lastName,u.email,u.status,u.creatorId,r.name,u.date)  from User as u join u.roles r where r.name='ROLE_ADMIN' and u.status='pending' order by u.date desc")
    List<PendingRolesList> pendingAdminList();

    @Query("select count(u) from User u join u.roles r where r.name = :role and u.status = 'pending'")
    Integer roleWisePendingCount(@Param("role") ERole role);

//  @Query("select count(u) from User as u join u.roles r where r.name='ROLE_ADMIN'")
//  Integer adminCount();

    @Query("select count(u) from User as u join u.roles r where r.name = 'ROLE_ADMIN' and u.status != 'Deleted' and u.status != 'Pending'")
    Integer adminCount();


    @Query("select count(u) from User as u join u.roles r where r.name='ROLE_PARENT'")
    Integer parentCount();
    @Query("SELECT u.standardMasters FROM User u WHERE u.id = :id")
    Set<StandardMaster> findStandardMasterByUserId(@Param("id") Long id);

    @Query("SELECT u.subjectMasters FROM User u WHERE u.id = :id")
    Set<SubjectMaster> findSubjectMasterByUserId(@Param("id") Long id);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :role AND u.status = 'Active'")
    Integer totalActiveUserCount(@Param("role") ERole role);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :role AND u.status = 'InActive'")
    Integer totalInActiveUserCount(@Param("role") ERole role);

    @Query("select count(u) from User as u where u.creatorId=:id")
    Integer createdUsersCounts(@Param("id") Long id);

    @Query("select count(u) from User as u JOIN u.roles r where u.creatorId=:id and r.name = :role")
    Integer getRoleWiseCounts(@Param("id") Long id,@Param("role") ERole role);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_subjects WHERE subject_id = :subjectId ", nativeQuery = true)
    void deleteSubjectBySubjectId(@Param("subjectId") Integer subjectId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_subjects WHERE  user_id=:id AND subject_id=:subjectId ", nativeQuery = true)
    void deleteSubject(@Param("id") Long id,@Param("subjectId") Integer subjectId);

    @Query("SELECT DISTINCT u.creatorId FROM User u WHERE u.creatorId IS NOT NULL")
    List<Long> getAllCreatorIds();

    @Query("select new com.bezkoder.springjwt.payload.response.ParentDetails(u.studentId,u.id,u.firstName,u.lastName,u.mobile,u.email,u.colorTheme,u.creatorId,u.status,u.password,u.confirmPassword,u.address) from User as u where u.studentId=:id")
    ParentDetails studentWiseParent(@Param("id") Long id);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN :roles")
    List<User> findUsersByRoles(@Param("roles") List<ERole> roles);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.InstituteDetailsResponse(u.id, u.instituteName, u.teacherKeys, u.slogan, u.photo,u.date,u.expiryDate,u.address,u.creatorId,u.logoImage,u.watermarkImage,u.email,u.mobile,u.status) " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = :roleEnum AND u.status = 'Active'")
    List<InstituteDetailsResponse> getAllActiveInstitutes(@Param("roleEnum") ERole roleEnum);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.InstituteDetailsResponse(u.id, u.instituteName, u.teacherKeys, u.slogan, u.photo,u.date,u.expiryDate,u.address,u.creatorId,u.logoImage,u.watermarkImage,u.email,u.mobile,u.status) from User as u where u.id=:id")
    InstituteDetailsResponse getInstituteById(@Param("id") Long id);



    @Query("select count(u) from User as u join u.roles r where r.name='ROLE_INSTITUTE'")
    Integer totalInstituteCount();

    @Query("SELECT new com.bezkoder.springjwt.payload.response.InstituteDetailsResponse(u.id, u.instituteName, u.teacherKeys, u.slogan, u.photo,u.date,u.expiryDate,u.address,u.creatorId,u.logoImage,u.watermarkImage,u.email,u.mobile,u.status)" +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = :roleEnum AND u.status =:status")
    List<InstituteDetailsResponse> getAllInstitutesByStatus(@Param("roleEnum") ERole roleEnum,@Param("status") String status);

    @Query("select count(u) from User as u join u.roles r where r.name='ROLE_INSTITUTE' and u.status!='Deleted' and u.status!='Pending'")
    Integer instituteCount();

    @Query("select t from User as t where t.creatorId=:id")
    List<Long> totalTeachersOfTheInstitute(@Param("id") Long id);

    @Query("select new com.bezkoder.springjwt.payload.response.PendingRolesList(u.id,u.firstName,u.lastName,u.email,u.status,u.creatorId,r.name,u.date)  from User as u join u.roles r where r.name='ROLE_INSTITUTE' and u.status='pending' order by u.date desc")
    List<PendingRolesList> pendingInstituteList();

    @Query("select um from UserManagementMaster as um where um.id=:id and um.entranceExamId=:entranceExamId and um.standardId=:standardId")
    List<UserManagementMaster> userAndEntranceExamAndStandardIdWiseSubjects(@Param("id") Long id,@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId);


    @Query("select new com.bezkoder.springjwt.payload.response.UserDetails(u.id,u.firstName,u.lastName,u.mobile,u.email,u.status,u.parentStatus,u.address) from User as u inner join u.roles as r where r.name='ROLE_STUDENT' order by u.id desc ")
    List<UserDetails> getAllStudents();


//  @Query("SELECT new com.bezkoder.springjwt.payload.response.UserDetails(u.id,u.firstName,u.lastName,u.mobile,u.email,u.status,u.parentStatus,u.address) FROM User u WHERE u.teacherId = :id order by u.date desc")
//  List<UserDetails> getAllUserByCreatorId(@Param("id") Long id);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.UserDetails( "
            + "u.id, u.firstName, u.lastName, u.mobile, u.email, u.status, "
            + "u.parentStatus, u.address) "
            + "FROM User u JOIN u.teacher t WHERE t.id = :id ORDER BY u.date DESC")
    List<UserDetails> getAllUsersByTeacherId(@Param("id") Long teacherId);


    @Query("SELECT u.id FROM User u JOIN u.teacher t WHERE t.id = :id ORDER BY u.date DESC")
    List<Long> getAllUserIdByCreatorId(@Param("id") Long id);


    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :userId")
    String getRoleById(@Param("userId") Long userId);


    List<User> findByCreatorId(Long id);


    @Query("SELECT u.studentId FROM User as u where u.id = :id")
    Long getStudentIdByParentId(@Param("id") Long id);

    @Query("SELECT t.id FROM User u JOIN u.teacher t WHERE u.id = (SELECT u2.studentId FROM User u2 WHERE u2.id = :senderId)")
    Long findTeacherIdByParentId(@Param("senderId") Long senderId);


    @Query("SELECT t.id\n" +
            "FROM User u JOIN u.teacher t\n" +
            "WHERE u.id = :senderId")
    Long findTeacherIdByStudentId(@Param("senderId") Long senderId);

    @Query(
            value = "SELECT * FROM users u " +
                    "WHERE (u.email = :email OR u.mobile = :mobile) " +
                    "ORDER BY u.id ASC LIMIT 1",
            nativeQuery = true
    )
    Optional<User> findByEmailOrMobile(@Param("email") String email,@Param("mobile") String mobile);



//  @Query("SELECT u FROM User u JOIN u.roles r WHERE u.email = :email AND r.name = :roleName")
//  Optional<User> findByEmailAndRole(@Param("email") String email, @Param("roleName") ERole roleName);

    @Query("SELECT u FROM User u JOIN u.roles r "
            + "WHERE u.email = :email "
            + "AND r.name = :roleName "
            + "AND u.status NOT IN ('Deleted', 'Rejected')")
    Optional<User> findByEmailAndRole(@Param("email") String email, @Param("roleName") ERole roleName);


    @Query("SELECT s.id FROM User s JOIN s.teacher t WHERE t.id = :teacherId")
    List<Long> findStudentsByTeacherId(@Param("teacherId") Long teacherId);



    @Query("SELECT s.id as id, CONCAT(s.firstName, ' ', s.lastName) AS name FROM User s JOIN s.teacher t WHERE t.id = :teacherId")
    List<TeacherIdNameProjection> findStudentsIdNameByTeacherId(@Param("teacherId") Long teacherId);



    @Query("SELECT COUNT(s.id) FROM User s JOIN s.teacher t WHERE t.id = :teacherId")
    Long countStudentsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.UserDetails(u.id, u.firstName, u.lastName, u.mobile, u.email,u.status,u.parentStatus,u.address) " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = 'ROLE_STUDENT' AND u.creatorId=:id order by u.id desc")
    List<UserDetails> allStudentsByCreatorId(@Param("id") Long id);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.ProfileResponse(u.id, u.firstName, u.lastName, u.mobile, u.email, u.status, u.profilePicture, u.editAccess, r.name) " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = 'ROLE_TEACHER' AND u.creatorId=:id " +
            "ORDER BY u.id DESC")
    List<ProfileResponse> allTeachersByCreatorId(@Param("id") Long id);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.InstituteDetailsResponse(u.id, u.instituteName, u.teacherKeys, u.slogan, u.photo,u.date,u.expiryDate,u.address,u.creatorId,u.logoImage,u.watermarkImage,u.email,u.mobile,u.status) " +
            "FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = :roleEnum AND u.creatorId=:id")
    List<InstituteDetailsResponse> getAllActiveInstitutesById(@Param("roleEnum") ERole roleEnum,@Param("id") Long id);

    @Query("SELECT u "+
            "FROM User u "+
            "WHERE u.expiryDate BETWEEN :start AND :end "
    )
    List<User> findExpiringBetween(
            @Param("start") java.util.Date start,
            @Param("end")   java.util.Date end
    );

    @Query("SELECT u "+
            "FROM User u "+
            "WHERE u.expiryDate BETWEEN :start AND :end "+
            "AND u.creatorId=:id"
    )
    List<User> findExpiringBetweenAndBycreatorId(
            @Param("start") java.util.Date start,
            @Param("end")   java.util.Date end,
            @Param("id") Long id
    );

//  List<User> findStudentsByTeacherId(Long teacherId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM student_teacher_mapping WHERE student_id = :id OR teacher_id = :id", nativeQuery = true)
    void deleteStudentTeacherMapping(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = 'InActive' WHERE u.expiryDate < CURRENT_DATE AND u.status = 'Active'")
    void markExpiredUsersInactive();




    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_roles WHERE user_id = :id", nativeQuery = true)
    void deleteUserRoles(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_subjects WHERE user_id = :id", nativeQuery = true)
    void deleteUserSubjects(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_standard WHERE user_id = :id", nativeQuery = true)
    void deleteUserStandards(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_entrance_exams WHERE user_id = :id", nativeQuery = true)
    void deleteUserEntranceExams(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM students_teachers WHERE student_id = :id OR teacher_id = :id", nativeQuery = true)
    void deleteTeacherStudentMapping(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM teacher_patterns WHERE teacher_id = :id", nativeQuery = true)
    void deleteTeacherPatterns(@Param("id") Long id);

}