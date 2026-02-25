package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.StudentManagementMaster;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.models.UserManagementMaster;
import com.bezkoder.springjwt.payload.response.StandardMasterResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentManagementRepository extends JpaRepository<StudentManagementMaster, Integer> {
    @Query("select s from StudentManagementMaster as s where s.studentId=:studentId")
    Optional<StudentManagementMaster> getStudentManagementByStudentId(@Param("studentId") Long studentId);

    @Query("select s from StudentManagementMaster as s where s.studentId=:id")
    List<StudentManagementMaster> getStudentManagementsByStudentId(@Param("id") Long id);
    void deleteByStudentId(Long id);

    @Query("SELECT s FROM StudentManagementMaster s WHERE s.studentId = :studentId")
    List<StudentManagementMaster> getAllByStudentId(@Param("studentId") Long studentId);
    @Query("select sm from StudentManagementMaster as sm where sm.subjectId=:subjectId")
    List<StudentManagementMaster> getStudentManagementsBySubject(@Param("subjectId") Integer subjectId);

//    List<StudentManagementMaster> findAllByUserIdAndEntranceExamIdAndStandardId(Long id, Integer entranceExamId, Integer standardId);

    @Query("select u from StudentManagementMaster as u where u.studentId=:id and u.entranceExamId=:entranceExamId and u.standardId=:standardId")
    List<StudentManagementMaster> findAllByUserIdAndEntranceExamIdAndStandardId(@Param("id") Long id, @Param("entranceExamId") Integer entranceExamId, @Param("standardId") Integer standardId);

    @Query("select u from StudentManagementMaster as u where u.studentId=:id and u.entranceExamId=:entranceExamId and u.standardId=:standardId and u.subjectId=:subjectId")
    StudentManagementMaster findByUserIdAndEntranceExamIdAndStandardIdAndSubjectId(@Param("id") Long id,@Param("entranceExamId")  Integer entranceExamId,@Param("standardId") Integer standardId,@Param("subjectId") Integer subjectId);

    boolean existsByStudentIdAndEntranceExamIdAndStandardIdAndSubjectId(Long studentId, Integer entranceExamId, Integer standardId, Integer subjectId);

    @Query("select sm from StudentManagementMaster as sm where sm.studentId=:id and sm.entranceExamId=:entranceExamId1")
    List<StudentManagementMaster> getAllByStudentIdAndEntranceExamIdWiseData(@Param("id") Long id,@Param("entranceExamId1") Integer entranceExamId1);

    @Query("select s from StudentManagementMaster as s where s.entranceExamId=:entranceExamId and s.standardId=:standardId and s.status='Active'")
    List<StudentManagementMaster> getStudentsByEntranceAndStandardIdWise(@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId);

    @Query("select sm from StudentManagementMaster as sm where sm.studentId=:id")
    Set<StudentManagementMaster> studentEntranceExams(@Param("id") Long id);

    @Query("select sm.standardId from StudentManagementMaster as sm where sm.studentId=:id and sm.entranceExamId=:entranceExamId")
    Set<Integer> getStudentIdAndEntranceExamIdWiseStandard(@Param("id") Long id, @Param("entranceExamId") Integer entranceExamId);

    @Query("select sm.subjectId from StudentManagementMaster as sm where sm.studentId=:id and sm.entranceExamId=:entranceExamId and sm.standardId=:standardId")
    Set<Integer> getSubjectsByStudentIdEntranceExamIdStandardId(@Param("id") Long id,@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId);

    @Query("select sm.entranceExamId from StudentManagementMaster as sm where sm.studentId=:id")
    List<Integer> getStudentEntranceExamById(@Param("id") Long id);

//    @Query("Select sm.teacherId from StudentManagementMaster as sm where sm.studentId=:id")
//    Long getStudentIdByParentId(Long id);


//    @Query("select u from UserManagementMaster as u where u.id=:id and u.entranceExamId=:entranceExamId and u.standardId=:standardId and u.subjectId=:subjectId")
//    UserManagementMaster findByUserIdAndEntranceExamIdAndStandardIdAndSubjectId(@Param("id") Long id,@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId,@Param("subjectId") Integer subjectId);
}
