package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.TopicMaster;
import com.bezkoder.springjwt.payload.response.TopicMasterResponse;
import com.bezkoder.springjwt.payload.response.TopicResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<TopicMaster,Integer> {

    int countByChapterMaster_ChapterId(Integer chapterId);

    @Query("select new com.bezkoder.springjwt.payload.response.TopicResponse(tm.topicId,tm.topicName,tm.entranceExamMaster.entranceExamId,tm.standardMaster.standardId,tm.subjectMaster.subjectId,tm.chapterMaster.chapterId,tm.date,tm.status) from TopicMaster as tm where tm.topicId=:topicId")
    TopicResponse getByTopicId(@Param("topicId") Integer topicId);

    @Query("select new com.bezkoder.springjwt.payload.response.TopicResponse(tm.topicId,tm.topicName,tm.entranceExamMaster.entranceExamId,tm.standardMaster.standardId,tm.subjectMaster.subjectId,tm.chapterMaster.chapterId,tm.date,tm.status) from TopicMaster as tm")
    List<TopicResponse> getAll();
    @Query("select new com.bezkoder.springjwt.payload.response.TopicResponse(tm.topicId,tm.topicName,tm.entranceExamMaster.entranceExamId,tm.standardMaster.standardId,tm.subjectMaster.subjectId,tm.chapterMaster.chapterId,tm.date,tm.status) from TopicMaster as tm where tm.status='Active'")
    List<TopicResponse> getAllActive();
    @Query("select new com.bezkoder.springjwt.payload.response.TopicResponse(tm.topicId,tm.topicName,tm.entranceExamMaster.entranceExamId,tm.standardMaster.standardId,tm.subjectMaster.subjectId,tm.chapterMaster.chapterId,tm.date,tm.status) from TopicMaster as tm where tm.chapterMaster.chapterId=:chapterId ")
    List<TopicResponse> chapterWiseTopics(@Param("chapterId") Integer chapterId);

    @Query("select new com.bezkoder.springjwt.payload.response.TopicResponse(tm.topicId,tm.topicName,tm.entranceExamMaster.entranceExamId,tm.standardMaster.standardId,tm.subjectMaster.subjectId,tm.chapterMaster.chapterId,tm.date,tm.status) from TopicMaster as tm where tm.chapterMaster.chapterId=:chapterId AND tm.status='Active' ")
    List<TopicResponse> chapterWiseActiveTopics(@Param("chapterId") Integer chapterId);

    @Query("select new com.bezkoder.springjwt.payload.response.TopicMasterResponse(t.topicId,t.topicName,t.date,t.status) from TopicMaster as t where t.topicId=:topicId")
    TopicMasterResponse getTopiMasterResponse(@Param("topicId") Integer topicId);

    @Query("select t.topicId from TopicMaster as t where t.entranceExamMaster.entranceExamId=:entranceExamId and t.standardMaster.standardId=:standardId and t.subjectMaster.subjectId=:subjectId and t.chapterMaster.chapterId=:chapterId")
    List<Integer> getTopicsByEntranceStandardSubjectChapterWise(@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId,@Param("subjectId") Integer subjectId, @Param("chapterId") Integer chapterId);

    @Query("select t.topicId from TopicMaster as t where t.entranceExamMaster.entranceExamId=:entranceExamId and t.standardMaster.standardId=:standardId and t.subjectMaster.subjectId=:subjectId and t.chapterMaster.chapterId=:chapterId")
    List<Integer> getTopicCountByEntranceStandardSubjectChapter(@Param("entranceExamId") Integer entranceExamId,@Param("standardId") Integer standardId,@Param("subjectId") Integer subjectId,@Param("chapterId") Integer chapterId);

//    @Query("select tm from TopicMaster as tm. where tm.chapterMaster.chapterId=:chapterId")
//    List<TopicMaster> getAllTopicByChapterId(@Param("chapterId") Integer chapterId);
}
