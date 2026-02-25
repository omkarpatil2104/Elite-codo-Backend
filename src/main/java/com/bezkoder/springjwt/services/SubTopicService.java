package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.SubTopicMaster;
import com.bezkoder.springjwt.payload.request.SubTopicRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.SubTopicResponse;

import java.util.List;

public interface SubTopicService {
    MainResponse create(SubTopicRequest subTopicRequest);

    MainResponse update(SubTopicRequest subTopicRequest);

    SubTopicMaster getById(Integer subTopicId);

    List<SubTopicMaster> getAll();

    List<SubTopicMaster> getAllActive();

    List<SubTopicResponse> topicWiseSubTopics(Integer topicId);

    List<SubTopicResponse> topicWiseActiveSubTopics(Integer topicId);

    MainResponse delete(Integer subTopicId);
}
