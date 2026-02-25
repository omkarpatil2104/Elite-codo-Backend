package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.payload.response.EntranceExamMasterResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntranceExamRepository extends JpaRepository<EntranceExamMaster, Integer> {
    @Query("select e from EntranceExamMaster as e where e.status='Active'")
    List<EntranceExamMaster> allActiveEntranceExam();

    @Query("select em from EntranceExamMaster em join em.standardMasters sm where sm.standardId = :standardId")
    List<EntranceExamMaster> standardWiseEntranceExam(@Param("standardId") Integer standardId);

    @Query("select new com.bezkoder.springjwt.payload.response.EntranceExamMasterResponse(e.entranceExamId,e.entranceExamName,e.date,e.status) from EntranceExamMaster as e where e.entranceExamId=:entranceExamId")
    Optional<EntranceExamMasterResponse> getEntranceExamResponse(@Param("entranceExamId") Integer entranceExamId);
}
