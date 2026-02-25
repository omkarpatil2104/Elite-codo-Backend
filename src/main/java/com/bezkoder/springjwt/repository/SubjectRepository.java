package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.payload.response.SubjectMasterResponse;
import com.bezkoder.springjwt.payload.response.SubjectMastersResponse;
import com.bezkoder.springjwt.payload.response.SubjectResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectMaster, Integer> {

    @Query("SELECT s FROM SubjectMaster s JOIN s.standardMaster st " +
            "WHERE st.standardId IN :ids AND s.status = 'Active'")
    List<SubjectMaster> findByStandardIds(@Param("ids") List<Integer> ids);

    List<SubjectMaster> findByEntranceExamMaster_EntranceExamId(Integer entranceExamId);

    @Query("select s from SubjectMaster as s where s.status='Active'")
    List<SubjectMaster> getAllActive();

    @Query("select sm from SubjectMaster sm join sm.standardMaster st where st.standardId = :standardId")
    List<SubjectMaster> standardWiseSubjects(@Param("standardId") Integer standardId);

    @Query("select sm from SubjectMaster sm join sm.standardMaster st where st.standardId = :standardId AND sm.status ='Active'")
    List<SubjectMaster> standardWiseActiveSubjects(@Param("standardId") Integer standardId);

    @Query("select new com.bezkoder.springjwt.payload.response.SubjectMasterResponse(s.subjectId,s.subjectName,s.date,s.status,s.entranceExamMaster.entranceExamId,s.entranceExamMaster.entranceExamName) from SubjectMaster as s where s.entranceExamMaster.entranceExamId=:entranceExamId")
    List<SubjectMasterResponse> entranceExamIdWiseSubjects(@Param("entranceExamId") Integer entranceExamId);


    @Query("select new com.bezkoder.springjwt.payload.response.SubjectMastersResponse(s.subjectId,s.subjectName,s.date,s.status) from SubjectMaster as s where s.subjectId=:subjectId")
    Optional<SubjectMastersResponse> getSubjectResponse(@Param("subjectId") Integer subjectId);

    @Query("SELECT s.subjectName FROM TestMaster t " +
            "JOIN t.subjectMaster s WHERE t.testId = :testId")
    List<String> findSubjectsByTestId(@Param("testId") Integer testId);

    SubjectMaster findBySubjectName(@Param("subjectName") Integer subjectName);

    @Query(
       "SELECT DISTINCT sm "+
       "FROM SubjectMaster sm "+
       "JOIN FETCH sm.standardMaster st  "+
       "WHERE sm.entranceExamMaster.entranceExamId = :entranceExamId "+
         "AND sm.status =  'Active' "
       )
    List<SubjectMaster> findWithStandardsByEntranceExamId(@Param("entranceExamId") Integer entranceExamId);

//    @Query("SELECT s FROM User u JOIN SubjectMaster as sm WHERE u.id = :userId")
//    List<SubjectMaster> getUserIdWiseSubjects1(Long id);


//    @Query("select new com.bezkoder.springjwt.payload.response.SubjectResponse(s.subjectId,s.subjectName,s.date,s.status,s.standardMaster.standardId,s.standardMaster.standardName,s.entranceExamMaster.entranceExamId,s.entranceExamMaster.entranceExamName) from SubjectMaster as s where s.standardMaster.standardId=:standardId")
//    List<SubjectResponse> standardWiseSubjects(@Param("standardId") Integer standardId);
}
