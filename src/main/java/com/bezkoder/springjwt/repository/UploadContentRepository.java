package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.UploadContentMaster;
import com.bezkoder.springjwt.payload.request.UploadContentRequest;
import com.bezkoder.springjwt.payload.response.UploadContentMasterResponses;
import com.bezkoder.springjwt.payload.response.UploadContents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface UploadContentRepository extends JpaRepository<UploadContentMaster, Integer> {
    @Query("select u from UploadContentMaster as u where u.status='Active'")
    List<UploadContentMaster> getAllActive();

    @Query("select new com.bezkoder.springjwt.payload.response.UploadContents(u.uploadContentId,u.url,u.type,u.title,u.description) from UploadContentMaster as u where u.topicId=:topicId")
    List<UploadContents> findByTopicId(@Param("topicId") Integer topicId);

    @Query("select new com.bezkoder.springjwt.payload.response.UploadContents(u.uploadContentId, u.url, u.type, u.title,u.description) from UploadContentMaster as u where u.chapterId=:chapterId and u.topicId is null")
    List<UploadContents> findByChapterId(@Param("chapterId") Integer chapterId);


    @Query("select u from UploadContentMaster as u where u.uploaderId= :uploaderId")
    List<UploadContentMaster> getAllByUploaderId(@Param("uploaderId") Long uploaderId);

    @Query("SELECT u FROM UploadContentMaster u WHERE u.uploaderId = :uploaderId AND u.contentType = 'question'")
    List<UploadContentMaster> getAllByUploaderIdAndContentTypeIsQuestion(@Param("uploaderId") Long uploaderId);

}
