package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.PackageMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageMasterRepository extends JpaRepository<PackageMaster, Integer> {
    @Query("select pm from PackageMaster as pm where pm.status='Active'")
    List<PackageMaster> getAllActive();
}
