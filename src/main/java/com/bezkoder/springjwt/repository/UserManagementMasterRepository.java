package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.StudentManagementMaster;
import com.bezkoder.springjwt.models.UserManagementMaster;
import com.bezkoder.springjwt.payload.response.UserManagementResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserManagementMasterRepository
        extends JpaRepository<UserManagementMaster, Integer> {

    /* ---------- simple fetch ---------- */

    List<UserManagementMaster> findByTeacherIdAndEntranceExamId(
            Long teacherId, Integer entranceExamId);

    /* ---------- subject lists ---------- */

    @Query("SELECT um.subjectId                                         " +
            "FROM   UserManagementMaster um                              " +
            "WHERE  um.teacherId = :teacherId                            " +
            "  AND  um.standardId = :standardId")
    List<Integer> findSubjectsByTeacherIdAndStandardId(
            @Param("teacherId") Long teacherId,
            @Param("standardId") Integer standardId);

    /* ---------- individual row ---------- */

    Optional<UserManagementMaster> findByTeacherIdAndStandardIdAndSubjectId(
            Long teacherId, Integer standardId, Integer subjectId);

    /* native sample (kept) ------------------------------------------- */
    @Query(value = "SELECT * FROM user_management_master               " +
            "WHERE  teacher_id = :teacherId                      " +
            "  AND  standard_id = :standardId", nativeQuery = true)
    List<UserManagementMaster> findByTeacherIdAndStandardIdNative(
            @Param("teacherId") Long teacherId,
            @Param("standardId") Integer standardId);

    /* ---------- convenience fetch ---------- */

    Optional<UserManagementMaster> getByTeacherId(Long teacherId);

    List<UserManagementMaster> findByTeacherIdAndEntranceExamIdAndStandardId(
            Long teacherId, Integer entranceExamId, Integer standardId);

    @Query("SELECT um.entranceExamId                                  " +
            "FROM   UserManagementMaster um                             " +
            "WHERE  um.teacherId = :teacherId")
    List<Integer> getEntranceExamIdsByTeacherId(@Param("teacherId") Long teacherId);

    List<UserManagementMaster> getEntranceExamsByTeacherId(Long teacherId);

    /* ---------- exists / duplicate checks ---------- */

    boolean existsByTeacherIdAndEntranceExamId(
            Long teacherId, Integer entranceExamId);

    boolean existsByTeacherIdAndEntranceExamIdAndStandardId(
            Long teacherId, Integer entranceExamId, Integer standardId);

    boolean existsByTeacherIdAndEntranceExamIdAndStandardIdAndSubjectId(
            Long teacherId, Integer entranceExamId,
            Integer standardId, Integer subjectId);

    /* ---------- delete-helpers ---------- */

    @Query("SELECT sm                                                " +
            "FROM   StudentManagementMaster sm                           " +
            "WHERE  sm.studentId      = :studentId                      " +
            "  AND  sm.entranceExamId = :entranceExamId                 " +
            "  AND  sm.standardId     = :standardId")
    List<StudentManagementMaster> deleteMappedDataOfStudent(
            @Param("studentId") Long studentId,
            @Param("entranceExamId") Integer entranceExamId,
            @Param("standardId") Integer standardId);

    @Query("SELECT um                                                " +
            "FROM   UserManagementMaster um                              " +
            "WHERE  um.teacherId      = :teacherId                      " +
            "  AND  um.entranceExamId = :entranceExamId                 " +
            "  AND  um.standardId     = :standardId")
    List<UserManagementMaster> deleteMappedDataOfTeacher(
            @Param("teacherId") Long teacherId,
            @Param("entranceExamId") Integer entranceExamId,
            @Param("standardId") Integer standardId);

    /* ---------- reporting / projection ---------- */

    @Query("SELECT u                                                " +
            "FROM   UserManagementMaster u                               " +
            "WHERE  u.teacherId = :teacherId")
    UserManagementResponse teacherIdWiseAccessManagements(
            @Param("teacherId") Long teacherId);

    UserManagementMaster findByTeacherIdAndEntranceExamIdAndStandardIdAndSubjectId(
            Long teacherId, Integer entranceExamId,
            Integer standardId, Integer subjectId);

    /* ---------- convenience lists / sets ---------- */

    List<UserManagementMaster> getAllByTeacherId(Long teacherId);

    @Query("SELECT um                                             " +
            "FROM   UserManagementMaster um                              " +
            "WHERE  um.teacherId      = :teacherId                     " +
            "  AND  um.entranceExamId = :entranceExamId")
    List<UserManagementMaster> getAllByTeacherIdAndEntranceExamId(
            @Param("teacherId") Long teacherId,
            @Param("entranceExamId") Integer entranceExamId);

    @Query("SELECT sm FROM StudentManagementMaster sm WHERE sm.studentId = :studentId")
    List<StudentManagementMaster> getAllByStudentId(@Param("studentId") Long studentId);

    /* ---------- look-ups for UI ---------- */

    @Query("SELECT u.standardId                                     " +
            "FROM   UserManagementMaster u                               " +
            "WHERE  u.teacherId      = :teacherId                      " +
            "  AND  u.entranceExamId = :entranceExamId")
    List<Integer> teacherAndEntranceExamIdWiseStandards(
            @Param("teacherId") Long teacherId,
            @Param("entranceExamId") Integer entranceExamId);

    @Query("SELECT u.subjectId                                      " +
            "FROM   UserManagementMaster u                               " +
            "WHERE  u.teacherId      = :teacherId                      " +
            "  AND  u.entranceExamId = :entranceExamId                 " +
            "  AND  u.standardId     = :standardId")
    List<Integer> teacherAndEntranceAndStandardIdWiseSubjects(
            @Param("teacherId") Long teacherId,
            @Param("entranceExamId") Integer entranceExamId,
            @Param("standardId") Integer standardId);

    @Query("SELECT um                                             " +
            "FROM   UserManagementMaster um                              " +
            "WHERE  um.teacherId      = :teacherId                     " +
            "  AND  um.entranceExamId = :entranceExamId")
    Set<UserManagementMaster> teacherAndEntranceExamIdWiseStandardsSet(
            @Param("teacherId") Long teacherId,
            @Param("entranceExamId") Integer entranceExamId);

    @Query("SELECT um FROM UserManagementMaster um WHERE um.teacherId = :teacherId")
    Set<UserManagementMaster> teacherWiseEntranceExams(@Param("teacherId") Long teacherId);
}
