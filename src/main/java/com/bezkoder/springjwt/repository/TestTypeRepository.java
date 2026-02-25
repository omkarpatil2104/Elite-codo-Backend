package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.TestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestTypeRepository extends JpaRepository<TestType, Integer> {
    @Query("select t from TestType as t where t.status='Active'")
    List<TestType> getAllActiveTestTypes();
}
