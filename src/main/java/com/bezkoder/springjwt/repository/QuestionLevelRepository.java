package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.QuestionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionLevelRepository extends JpaRepository<QuestionLevel,Integer> {
    @Query("select q from QuestionLevel as q where q.status='Active'")
    List<QuestionLevel> getAllActive();

    QuestionLevel findByQuestionLevel(String easy);
}
