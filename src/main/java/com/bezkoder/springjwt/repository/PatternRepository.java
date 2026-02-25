package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.PatternMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatternRepository extends JpaRepository<PatternMaster,Integer> {
    @Query("select p from PatternMaster as p where p.status='Active'")
    List<PatternMaster> getAllActive();
}
