package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.SubTopicMaster;
import com.bezkoder.springjwt.payload.response.SubTopicResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopicMaster, Integer> {
    @Query("select st from SubTopicMaster as st where st.status='Active'")
    List<SubTopicMaster> getAllActive();

    @Query("select new com.bezkoder.springjwt.payload.response.SubTopicResponse(st.subTopicId,st.subTopicName,st.entranceExamMaster.entranceExamId,st.entranceExamMaster.entranceExamName,st.standardMaster.standardId,st.standardMaster.standardName,st.subjectMaster.subjectId,st.subjectMaster.subjectName,st.chapterMaster.chapterId,st.chapterMaster.chapterName,st.topicMaster.topicId,st.topicMaster.topicName,st.date,st.status) from SubTopicMaster as st where st.topicMaster.topicId=:topicId")
    List<SubTopicResponse> topicWiseSubTopics(@Param("topicId") Integer topicId);

    @Query("select new com.bezkoder.springjwt.payload.response.SubTopicResponse(st.subTopicId,st.subTopicName,st.entranceExamMaster.entranceExamId,st.entranceExamMaster.entranceExamName,st.standardMaster.standardId,st.standardMaster.standardName,st.subjectMaster.subjectId,st.subjectMaster.subjectName,st.chapterMaster.chapterId,st.chapterMaster.chapterName,st.topicMaster.topicId,st.topicMaster.topicName,st.date,st.status) from SubTopicMaster as st where st.topicMaster.topicId=:topicId AND st.status='Active'")
    List<SubTopicResponse> topicWiseActiveSubTopics(@Param("topicId") Integer topicId);

//    @Query("select st from SubTopicMaster as st where st.chapterMaster.chapterId=:chapterId")
//    List<SubTopicMaster> getAllSubTopicByChapterId(@Param("chapterId") Integer chapterId);
}
