package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.payload.response.StandardResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardRepository extends JpaRepository<StandardMaster,Integer> {
    @Query("select s from StandardMaster as s where s.status='Active'")
    List<StandardMaster> getAllActiveClasses();

//    @Query("select new com.bezkoder.springjwt.payload.response.StandardResponse(s.standardId,s.standardName,s.entranceExamMaster.entranceExamId,s.entranceExamMaster.entranceExamName,s.date,s.status) from StandardMaster as s where s.entranceExamMaster.entranceExamId=:entranceExamId")
//    List<StandardResponse> entranceExamWiseStandard(@Param("entranceExamId") Integer entranceExamId);
}
