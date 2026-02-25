package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.InstituteMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstituteRepository extends JpaRepository<InstituteMaster, Integer> {
    @Query("select i from InstituteMaster as i where i.status='Active'")
    List<InstituteMaster> getAllActive();
    InstituteMaster findByInstituteNameIgnoreCase(String instituteName);


}
