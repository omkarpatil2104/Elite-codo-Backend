package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.TestSubmissionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSubmissionDetailRepository extends JpaRepository<TestSubmissionDetail,Long> {

    @Query("SELECT tsd FROM TestSubmissionDetail tsd " +
            "JOIN tsd.testSubmission ts " +
            "WHERE ts.user.id = :studentId " +
            "AND ts.test.testId = :testId")
    List<TestSubmissionDetail> findAllByUserAndTest(@Param("studentId") Long studentId,
                                                    @Param("testId") Integer testId);
}
