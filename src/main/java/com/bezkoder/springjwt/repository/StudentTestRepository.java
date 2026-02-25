package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.StudentTestMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentTestRepository extends JpaRepository<StudentTestMaster,Long> {
}
