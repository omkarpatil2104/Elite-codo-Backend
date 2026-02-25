package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ExamSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExamSetRepository extends JpaRepository<ExamSet,Long> {
    @Query(value = "SELECT * FROM exam_set WHERE test_id = :testId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<ExamSet> findRandomExamSetByTestId(@Param("testId") Integer testId);
}
