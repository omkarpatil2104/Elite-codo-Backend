package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ChapterMaster;
import com.bezkoder.springjwt.models.QuestionMaster;
import com.bezkoder.springjwt.payload.response.ChapterMasterResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<ChapterMaster, Integer> {

    @Query("SELECT c FROM ChapterMaster c " +
            "WHERE c.subjectMaster.subjectId IN :ids AND c.status = 'Active'")
    List<ChapterMaster> findBySubjectIds(@Param("ids") List<Integer> ids);

    List<ChapterMaster> findBySubjectMaster_SubjectId(Integer subjectId);

    @Query("select ch from ChapterMaster as ch where ch.status='Active'")
    List<ChapterMaster> getAllActiveChapters();

    @Query("select ch from ChapterMaster as ch where ch.subjectMaster.subjectId=:subjectId AND ch.status='Active'")
    List<ChapterMaster> subjectWiseChapter(@Param("subjectId") Integer subjectId);

    @Query("select new com.bezkoder.springjwt.payload.response.ChapterMasterResponse(c.chapterId,c.chapterName,c.date,c.status) from ChapterMaster as c where c.chapterId=:chapterId")
    ChapterMasterResponse getChapterMasterResponse(@Param("chapterId") Integer chapterId);

    @Query("select ch.chapterId from ChapterMaster as ch where ch.entranceExamMaster.entranceExamId=:entranceExamId and ch.standardMaster.standardId=:standardId and ch.subjectMaster.subjectId=:subjectId")
    List<Integer> getChaptersByEntranceStandardSubject(@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId,@Param("subjectId") Integer subjectId);

    @Query("select ch from ChapterMaster as ch where ch.subjectMaster.subjectId=:subjectId AND ch.standardMaster.standardId=:standardId")
    List<ChapterMaster> subjectStandardWiseChapter(@Param("subjectId") Integer subjectId,@Param("standardId") Integer standardId);

    @Query("select ch from ChapterMaster as ch where ch.subjectMaster.subjectId=:subjectId AND ch.status='Active' AND ch.standardMaster.standardId=:standardId")
    List<ChapterMaster> subjectStandardWiseActiveChapter(@Param("subjectId") Integer subjectId,@Param("standardId") Integer standardId);

    @Query("SELECT c.chapterId AS chapterId, c.chapterName AS chapterName, COUNT(q) AS questionCount " +
            "FROM ChapterMaster c LEFT JOIN QuestionMaster q ON q.chapterMaster.chapterId = c.chapterId " +
            "WHERE c.subjectMaster.subjectId IN :ids AND c.status = 'Active' " +
            "GROUP BY c.chapterId, c.chapterName")
    List<Object[]> findChaptersWithQuestionCount(@Param("ids") List<Integer> ids);




}
