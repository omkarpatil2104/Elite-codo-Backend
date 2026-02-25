package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.TestMaster;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.ModeWiseTestProjection;
import com.bezkoder.springjwt.payload.response.*;
import org.hibernate.sql.Select;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface TestRepository extends JpaRepository<TestMaster, Integer> {


//    @Query("select count(t) from TestMaster t join t.createdBy u join u.roles r " +
//            "where r.name = :role")
//    long countByRole(@Param("role") ERole role);
//
//    @Query("select count(t) from TestMaster t join t.createdBy u join u.roles r " +
//            "where r.name = :role and u.id in :creatorIds")
//    long countByRoleAndCreatorIds(@Param("role") ERole role,
//                                  @Param("creatorIds") Collection<Long> creatorIds);

    /* ───── global ───── */
    long countByCreatedBy_Roles_Name(ERole name);             // ✅

    /* ───── limited to a set of creators ───── */
    long countByCreatedBy_IdInAndCreatedBy_Roles_Name(        // ✅
                                                              Collection<Long> creatorIds,
                                                              ERole name
    );

//    @Query(" SELECT COUNT(tm) " +
//           " FROM TestMaster tm " +
//          " WHERE tm.createdBy IN ( " +
//            "    SELECT teacher " +
//              "  FROM User s JOIN s.teacher teacher "+
//                " WHERE s.id = :studentId )"
//           )
//    Long countTestsCreatedForStudentByTeacher(Long studentId);
    @Query("SELECT COUNT(tm) FROM TestMaster tm WHERE tm.createdBy.id IN ("+
            "SELECT t.id FROM User s JOIN s.teacher t WHERE s.id = :studentId)")
    Long countTestsCreatedForStudentByTeacher(@Param("studentId") Long studentId);

//    @Query("SELECT COUNT(t) FROM TestMaster t WHERE t.testMode = 'Offline' AND t.createdBy.id = (SELECT s.teacher.id FROM Student s WHERE s.id = :studentId)")
//    Long countOfflineTestsCreatedForStudentByTeacher(@Param("studentId") Long studentId);

    List<TestMaster> findAllByCreatedBy(User createdBy);

    @Query("SELECT t.testId AS testId, t.testName AS testName, t.testDate AS testDate, " +
            "t.marks AS marks, t.status AS status " +
            "FROM TestMaster t " +
            "WHERE t.testMode = :testMode " +
            "AND t.createdBy.id = :createdById")
    Page<ModeWiseTestProjection> findByTestModeAndCreatedBy(
            @Param("testMode") String testMode,
            @Param("createdById") Long createdById,
            Pageable pageable);

    @Query("select tm from TestMaster as tm where tm.status=:status order by tm.testDate desc")
    List<TestMaster> statusWiseTests(@Param("status") String status);

    @Query("select count(t) from TestMaster as t where t.status = 'Upcoming' or t.status = 'Active' or t.status='InActive'")
    Integer totalTests();
    @Query("select count(t) from TestMaster as t where t.status='Active'")
    Integer totalActiveTests();

    @Query("select count(t) from TestMaster as t where t.status='InActive'")
    Integer totalInActiveTests();

    @Query("SELECT new com.bezkoder.springjwt.payload.response.TestMasterResponse(t.testId, t.testName, t.status,t.startTime,t.endTime,t.testDate) FROM TestMaster t JOIN t.standardMaster s WHERE t.entranceExamMaster.entranceExamId = :entranceExamId AND s.standardId = :standardId AND t.status = 'Upcoming' order by t.testDate desc")
    List<TestMasterResponse> entranceAndStandardIdWiseUpComingTest(@Param("entranceExamId") Integer entranceExamId, @Param("standardId") Integer standardId);

    List<TestMaster> findAllByCreatedBy_Id(@Param("id") Long id);

//    @Query("SELECT new com.bezkoder.springjwt.payload.response.TestOfflineResponse(t.testId,t.testName) FROM TestMaster t" +
//            " where t.createdBy.id = :userId AND t.testMode = 'Offline' ")
//    List<TestOfflineResponse> findAllOfflineTestsByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.TestOfflineResponse(t.testId, t.testName)" +
            "FROM TestMaster t " +
            "WHERE t.createdBy.id = :userId AND t.testMode = 'Offline'")
    List<TestOfflineResponse> findAllOfflineTestsByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.bezkoder.springjwt.payload.response.OfflineTestQueryResponse(" +
            "t.testId, t.testName, t.testDate, t.marks, t.status, t.endTime) " +
            "FROM TestMaster t " +
            "WHERE t.createdBy.id = :userId AND t.testMode = 'Offline'")
    List<OfflineTestQueryResponse> findAllOfflineTByUserId(@Param("userId") Long userId);

    long countByCreatedById(@Param("teacherId") Long teacherId);

    @Query("Select Count(t) from TestMaster t where t.createdBy.id = :tid AND t.status = 'Active'")
    int countTotalActiveTest(@Param("tid") Long tid);

    @Query("select Count(t) from TestMaster t where t.createdBy.id= :tid AND t.status= 'Upcoming'")
    int countTotalUpcomingTest(@Param("tid") Long tid);

    @Query("select Count(t) from TestMaster t where t.createdBy.id= :tid AND t.status= 'Ended'")
    int countEndedTest(@Param("tid") Long tid);

//    @Query("SELECT COUNT(t) FROM TestMaster t JOIN t.students s WHERE s.id = :studentId")
//    long countTestsAssignedToStudent(Long studentId);

    @Query("SELECT t.testId FROM TestMaster t WHERE t.createdBy.id = :teacherId")
    List<Integer> findIdsByTeacher(@Param("teacherId") Long teacherId);

    @Query(
            value = "SELECT DISTINCT tm.* " +
                    "FROM   test_master      tm " +
                    "JOIN   test_standard    ts ON tm.test_id   = ts.test_id " +
                    "JOIN   user_standard    us ON ts.standard_id = us.standard_id " +
                    "WHERE  us.user_id = :studentId " +
                    "  AND  tm.test_date >= CURDATE() " +
                    "ORDER  BY tm.test_date, tm.start_time",
            nativeQuery = true)
    List<TestMaster> findUpcomingTestsForStudent(@Param("studentId") Long studentId);


    @Query(
            value = "SELECT tm.test_id, tm.test_name, tm.test_date, " +
                    "       sm.subject_name,                        " +
                    "       tm.status                               " +
                    "FROM   test_master      tm                     " +
                    "JOIN   test_subjects    ts ON tm.test_id   = ts.test_id " +
                    "JOIN   subject_master   sm ON ts.subject_id = sm.subject_id " +
                    "JOIN   test_standard    tstd ON tm.test_id = tstd.test_id " +
                    "JOIN   user_standard    ustd ON tstd.standard_id = ustd.standard_id " +
                    "WHERE  ustd.user_id = :studentId              " +
                    "  AND  tm.test_date <  CURDATE()              " +
                    "ORDER  BY tm.test_date DESC, tm.start_time DESC " +
                    "LIMIT  50",  // tweak or remove if you need every record
            nativeQuery = true )
    List<Object[]> findPastTestsForStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(t) " +
            "FROM   TestMaster t " +
            "WHERE  t.createdBy.id = :studentId " )
    long countPracticeMocks(@Param("studentId") Long studentId);


//    @Query("SELECT new com.bezkoder.springjwt.dto.TestMasterResponseDTO(" +
//            "tm.testId, tm.testName, tm.startTime, tm.endTime, tm.marks, tm.id, " +
//            "COALESCE(SIZE(tm.subjectMaster), 0), " +
//            "tm.status, tm.typeOfTest, " +
//            "COALESCE(SIZE(tm.chapterMasters), 0), " +
//            "tm.testDate, " +
//            "COALESCE(SIZE(tm.standardMaster), 0), " +
//            "eam.examName, yao.yearOfAppearance, " +
//            "tm.createdDate, " +
//            "COALESCE(SIZE(tm.questionMasters), 0)) " +
//            "FROM TestMaster tm " +
//            "JOIN tm.testType tt " +
//            "JOIN tm.createdBy u " +
//            "JOIN tm.yearOfAppearance yao " +
//            "JOIN tm.entranceExamMaster eam " +
//            "LEFT JOIN tm.subjectMaster su " +
//            "LEFT JOIN tm.chapterMasters ch " +
//            "LEFT JOIN tm.standardMaster sm " +
//            "LEFT JOIN tm.questionMasters qm " +
//            "WHERE u.id IN :userIds")
//    List<TestResponse> getAllTestsByUsers(Set<User> teachers);



//    @Query("select new com.bezkoder.springjwt.payload.response.TestResponse(ts.testId,ts.testName,ts.startTime,ts.endTime,ts.marks,ts.createdBy.id) from TestMaster as ts where ts.createdBy.id=:id")
//    List<TestResponse> getAllByCreatedById(@Param("id") Long id);
}
