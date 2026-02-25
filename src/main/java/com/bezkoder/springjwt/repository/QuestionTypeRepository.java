package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionTypeRepository extends JpaRepository<QuestionType, Integer> {
    @Query("select qt from QuestionType as qt where qt.status='Active'")
    List<QuestionType> getAllActive();
}
