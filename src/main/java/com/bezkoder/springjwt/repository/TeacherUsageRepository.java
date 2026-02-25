package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.TeacherQuestionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherUsageRepository
        extends JpaRepository<TeacherQuestionUsage,TeacherQuestionUsage.PK> {

    /** all question IDs already used by this teacher */
    @Query("SELECT t.questionId FROM TeacherQuestionUsage t WHERE t.teacherId = :tid")
    List<Integer> idsUsedBy(@Param("tid") Long teacherId);

    /** bulk delete when pool is exhausted */
    @Modifying
    @Query("DELETE FROM TeacherQuestionUsage t WHERE t.teacherId = :tid")
    void resetFor(@Param("tid") Long teacherId);
}
