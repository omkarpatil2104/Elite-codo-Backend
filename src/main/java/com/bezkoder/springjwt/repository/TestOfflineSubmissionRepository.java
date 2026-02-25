package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.TestOfflineSubmission;
import com.bezkoder.springjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestOfflineSubmissionRepository extends JpaRepository< TestOfflineSubmission,Long> {

    @Query("SELECT toff FROM TestOfflineSubmission toff WHERE toff.test.testId IN :testIds")
    List<TestOfflineSubmission> findByTestIds(@Param("testIds") List<Integer> testIds);

    @Query("SELECT tos.student FROM TestOfflineSubmission tos WHERE tos.test.testId = :testId")
    List<User> findStudentsByTestId(@Param("testId") Integer testId);

        @Query("SELECT tos FROM TestOfflineSubmission tos WHERE tos.student.studentId = :studentId")
        List<TestOfflineSubmission> findAllByStudentId(Long studentId);

    List<TestOfflineSubmission> findByStudent_Id(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(tos) FROM TestOfflineSubmission tos WHERE tos.student.studentId = :studentId")
    Long countOfflineSubmittedTestsByStudent(@Param("studentId") Long studentId);

    @Query("select tos from TestOfflineSubmission tos where tos.test.testId= :testId")
    List<TestOfflineSubmission> findByTest_TestId(@Param("testId") Integer testId);

    @Query("SELECT tos FROM TestOfflineSubmission tos WHERE tos.test.testId IN :testIds")
    List<TestOfflineSubmission> findByTestIdIn(@Param("testIds") List<Integer> testIds);

    @Query("SELECT COUNT(tos) FROM TestOfflineSubmission tos WHERE  tos.test.testId= :testId ")
    Integer countOfflineSubmittedTestsByTestId(@Param("testId") Integer testId);

    @Query("SELECT tos FROM TestOfflineSubmission tos WHERE  tos.test.testId= :testId ")
    List<TestOfflineSubmission> findOfflineSubsByTestId(@Param("testId") Integer testId);

    List<TestOfflineSubmission> findByTest_CreatedBy_Id(Long id);

    @Query(
  "SELECT tos "+
    "FROM TestOfflineSubmission tos "+
"WHERE tos.student.id = :studentId"+
" AND tos.test.createdBy.id = :teacherId")
    List<TestOfflineSubmission> findByStudentAndTeacher(
            @Param("studentId") Long studentId,
            @Param("teacherId") Long teacherId
    );

    @Query("SELECT o FROM TestOfflineSubmission o WHERE o.student.id = :studentId ORDER BY o.test.testDate ASC")
    List<TestOfflineSubmission> findByStudentIdOrderByTestDateAsc(@Param("studentId") Long studentId);

    @Query("SELECT tos "+
             "FROM TestOfflineSubmission tos "+
           " WHERE tos.test.testId IN :testIds "+
              "AND tos.student.id  IN :studentIds"
           )
    List<TestOfflineSubmission> findByTestIdsAndStudentIds(@Param("testIds")   List<Integer> testIds,
                                                           @Param("studentIds") List<Long> studentIds);

    @Query(
            "SELECT COALESCE(SUM(tos.score),0) "+
                    "From TestOfflineSubmission tos "+
                    "WHERE tos.student.id = :studentId"
    )
    double sumAllOfflineScores(@Param("studentId") Long studentId);

//    @Query("SELECT COUNT(t) FROM TestMaster t WHERE t.testMode = 'Offline' AND t.createdBy.id = (SELECT s.teacher.id FROM Student s WHERE s.id = :studentId)")
//    long countOfflineTestsCreatedForStudentByTeacher(@Param("studentId") Long studentId);
}
