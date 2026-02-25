package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.QuestionMaster;
import com.bezkoder.springjwt.models.TestSubmission;
import com.bezkoder.springjwt.models.TestSubmissionDetail;
import com.bezkoder.springjwt.payload.request.StudentTestResultDTO;
import com.bezkoder.springjwt.payload.response.TestResultResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSubmissionRepository extends JpaRepository<TestSubmission,Long> {

    List<TestSubmission> findByTest_CreatedBy_Id(Long teacherId);

    @Query("SELECT ts FROM TestSubmission ts WHERE ts.test.testId IN :testIds")
    List<TestSubmission> findByTestIds(@Param("testIds") List<Integer> testIds);


    @Query("SELECT COUNT(ts) FROM TestSubmission ts WHERE ts.user.id = :studentId AND ts.test.createdBy.id IN (" +
            "SELECT t.id FROM User s JOIN s.teacher t WHERE s.id = :studentId)")
    Long countSubmittedTestsByStudent(@Param("studentId") Long studentId);


//    @Query("SELECT SUM(ts.obtainedMarks) FROM TestSubmission ts WHERE ts.user.id = :studentId AND ts.test.createdBy.id IN ( " +
//            "SELECT t.id FROM User s JOIN s.teacher t WHERE s.id = :studentId)")
//    Long sumObtainedMarksByStudent(@Param("studentId") Long studentId);
//
//    @Query("SELECT SUM(ts.totalMarks) FROM TestSubmission ts WHERE ts.user.id = :studentId AND ts.test.createdBy.id IN ( " +
//            "SELECT t.id FROM User s JOIN s.teacher t WHERE s.id = :studentId)")
//    Long sumTotalMarksByStudent(@Param("studentId") Long studentId);

     @Query("SELECT ts FROM TestSubmission ts WHERE ts.user.id = :studentId")
     List<TestSubmission> findByUserId(@Param("studentId") Long studentId);

//    @Query("SELECT t FROM TestSubmission t WHERE t.test.testId = :testId AND t.user.id = :studentId")
//    Optional<TestSubmission> findByTestIdAndUserId(@Param("testId") Integer testId,
//                                                   @Param("studentId") Long studentId);

     Optional<TestSubmission> findByTest_TestIdAndUser_Id(Integer testId, Long studentId);

    @Query("SELECT DISTINCT ts "+
             "FROM TestSubmission ts "+
             "JOIN  FETCH ts.importantQuestions iq "+
            "WHERE ts.user.id = :studentId "
           )
    List<TestSubmission> findWithImportantQuestionsByStudent(
            @Param("studentId") Long studentId);


    @Query("SELECT ts FROM TestSubmission ts " +
            "WHERE ts.user.id = :studentId AND ts.test.testId = :testId")
    List<TestSubmission> findByUserIdAndTestId(@Param("studentId") Long studentId,
                                               @Param("testId") Integer testId);


    List<TestSubmission> findByUser_Id(Long userId);

    List<TestSubmission> findByUser_IdAndTest_CreatedBy_Id(@Param("userId") Long userId,@Param("teacherId") Long teacherId);

    List<TestSubmission> findByUser_IdAndTest_CreatedBy_IdOrderBySubmittedAtDesc(Long studentId, Long creatorId);

    @Query("select ts from TestSubmission ts where ts.test.testId= :testId")
    List<TestSubmission> findByTest_TestId( @Param("testId") Integer testId);

    @Query("SELECT ts FROM TestSubmission ts WHERE ts.test.testId IN :testIds")
    List<TestSubmission> findByTestIdIn(@Param("testIds") List<Integer> testIds);

    @Query("select count(ts) from TestSubmission ts where ts.test.testId = :testId")
    Integer countSubmittedTestsByTestId(@Param("testId") Integer testId);

    @Query("select ts from TestSubmission ts where ts.test.testId = :testId")
    List<TestSubmission> findOnlineSubsByTestId(@Param("testId") Integer testId);

    @Query(
 "SELECT ts " +
"FROM TestSubmission ts " +
   "WHERE ts.user.id = :studentId " +
  " AND ts.test.createdBy.id = :teacherId")
    List<TestSubmission> findByStudentAndTeacher(
            @Param("studentId") Long studentId,
            @Param("teacherId") Long teacherId
    );

    @Query("SELECT d FROM TestSubmissionDetail d WHERE d.testSubmission.test.testId = :testId")
    List<TestSubmissionDetail> findByTestId(@Param("testId") Integer testId);

    List<TestSubmission> findByUser_IdOrderBySubmittedAtAsc(Long studentId);

    @Query("SELECT ts "+
             "FROM TestSubmission ts " +
            "WHERE ts.test.testId IN :testIds "+
              "AND ts.user.id      IN :studentIds "
           )
    List<TestSubmission> findByTestIdsAndStudentIds(@Param("testIds")   List<Integer> testIds,
                                                    @Param("studentIds") List<Long> studentIds);

    /** mock-tests already submitted by the student **/
    @Query("SELECT COUNT(ts)                                                " +
            "FROM   TestSubmission ts                                        " +
            "WHERE  ts.user.id = :studentId                                  "
            )
    long countCompletedMocks(
            @Param("studentId") Long studentId);

    @Query("SELECT COALESCE(SUM(ts.score),0)                                " +
            "FROM   TestSubmission ts                                        " +
            "WHERE  ts.user.id = :studentId                                  " )
    double sumALLScore(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(ts)                                                " +
            "FROM   TestSubmission ts                                        " +
            "WHERE  ts.user.id = :studentId                                  " +
            " AND ts.test.createdBy.id = :studentId")
    long countAllCompletedOnline(@Param("studentId") Long studentId);
}
