package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.TopicMaster;
import com.bezkoder.springjwt.payload.request.TopicRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.TopicResponse;

import java.util.List;

public interface TopicService {
    MainResponse create(TopicRequest topicRequest);

    MainResponse update(TopicRequest topicRequest);

    TopicResponse getById(Integer topicId);

    List<TopicMaster> getAll();

    List<TopicResponse> getAllActive();

    List<TopicResponse> chapterWiseTopics(Integer chapterId);

    List<TopicResponse> chapterWiseActiveTopics(Integer chapterId);

    MainResponse delete(Integer topicId);
}
