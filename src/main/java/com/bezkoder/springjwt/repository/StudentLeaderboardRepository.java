package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.StudentLeaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentLeaderboardRepository extends JpaRepository<StudentLeaderboard,Long> {
    @Query("SELECT COUNT(s) + 1 FROM StudentLeaderboard s, StudentLeaderboard sl " +
            "WHERE sl.student.id = :studentId " +
            "AND s.totalMarks > sl.totalMarks")
    Integer findRankByStudentId(@Param("studentId") Long studentId);

    StudentLeaderboard findByStudentId(Long id);

    /** All leaderboard rows for students that belong to one teacher’s class */
    @Query(
           "SELECT sl "+
             "FROM StudentLeaderboard sl "+
             "JOIN sl.student s "+
              "JOIN s.teacher  t "+
            "WHERE t.id = :teacherId "
           )
    List<StudentLeaderboard> findByTeacher(@Param("teacherId") Long teacherId);

    /** Same idea, but by class-name if you prefer */
    @Query(
            "SELECT sl "+
             "FROM StudentLeaderboard sl "+
             "JOIN sl.student s "+
            "WHERE s.className = :className "
           )
    List<StudentLeaderboard> findByClassName(@Param("className") String className);
}
