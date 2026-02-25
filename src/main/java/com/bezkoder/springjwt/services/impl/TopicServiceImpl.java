package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.TopicRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.TopicResponse;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.TopicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private EntranceExamRepository entranceExamRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    @Override
    public MainResponse create(TopicRequest topicRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(this.entranceExamRepository.findById(topicRequest.getEntranceExamId()).orElseThrow(()->new RuntimeException("Entrance Exam not found")));
        Optional<StandardMaster> standardMaster = Optional.ofNullable(this.standardRepository.findById(topicRequest.getStandardId()).orElseThrow(()->new RuntimeException("Standard not found exception")));
        Optional<SubjectMaster> subjectMaster = Optional.ofNullable(this.subjectRepository.findById(topicRequest.getSubjectId()).orElseThrow(()->new RuntimeException("Subject not found")));
        Optional<ChapterMaster> chapterMaster = Optional.ofNullable(this.chapterRepository.findById(topicRequest.getChapterId()).orElseThrow(()->new RuntimeException("Chapter not found")));

        TopicMaster topicMaster = new TopicMaster();
        try {
            BeanUtils.copyProperties(topicRequest,topicMaster);
            topicMaster.setEntranceExamMaster(entranceExamMaster.get());
            topicMaster.setStandardMaster(standardMaster.get());
            topicMaster.setSubjectMaster(subjectMaster.get());
            topicMaster.setChapterMaster(chapterMaster.get());
            topicMaster.setDate(new Date());
            this.topicRepository.save(topicMaster);
            mainResponse.setMessage("Topic created successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }catch (Exception e){
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public MainResponse update(TopicRequest topicRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(this.entranceExamRepository.findById(topicRequest.getEntranceExamId()).orElseThrow(()->new RuntimeException("Entrance Exam not found")));
        Optional<StandardMaster> standardMaster = Optional.ofNullable(this.standardRepository.findById(topicRequest.getStandardId()).orElseThrow(()->new RuntimeException("Standard not found exception")));
        Optional<SubjectMaster> subjectMaster = Optional.ofNullable(this.subjectRepository.findById(topicRequest.getSubjectId()).orElseThrow(()->new RuntimeException("Subject not found")));
        Optional<ChapterMaster> chapterMaster = Optional.ofNullable(this.chapterRepository.findById(topicRequest.getChapterId()).orElseThrow(()->new RuntimeException("Chapter not found")));
        Optional<TopicMaster> topicMaster = Optional.ofNullable(this.topicRepository.findById(topicRequest.getTopicId()).orElseThrow(()->new RuntimeException("Topic not found exception")));
        try {
            BeanUtils.copyProperties(topicRequest,topicMaster.get());
            topicMaster.get().setEntranceExamMaster(entranceExamMaster.get());
            topicMaster.get().setStandardMaster(standardMaster.get());
            topicMaster.get().setSubjectMaster(subjectMaster.get());
            topicMaster.get().setChapterMaster(chapterMaster.get());
            topicMaster.get().setDate(new Date());
            this.topicRepository.save(topicMaster.get());
            mainResponse.setMessage("Topic updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }catch (Exception e){
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public TopicResponse getById(Integer topicId) {
        TopicResponse topicResponse = this.topicRepository.getByTopicId(topicId);
        return topicResponse;
    }

    @Override
    public List<TopicMaster> getAll() {
        List<TopicMaster> topicMasters = this.topicRepository.findAll();
        return topicMasters;
    }

    @Override
    public List<TopicResponse> getAllActive() {
        List<TopicResponse> topicResponses = this.topicRepository.getAllActive();
        return topicResponses;
    }

    @Override
    public List<TopicResponse> chapterWiseTopics(Integer chapterId) {
        List<TopicResponse> topicResponses = this.topicRepository.chapterWiseTopics(chapterId);
        return topicResponses;
    }

    @Override
    public List<TopicResponse> chapterWiseActiveTopics(Integer chapterId) {
        List<TopicResponse> topicResponses = this.topicRepository.chapterWiseActiveTopics(chapterId);
        return topicResponses;
    }

    @Override
    public MainResponse delete(Integer topicId) {
        MainResponse mainResponse = new MainResponse();
        TopicMaster topicMaster = this.topicRepository.findById(topicId).orElseThrow(()->new RuntimeException("Topic not found"));
        try {
            topicMaster.setStatus("Deleted");
            this.topicRepository.save(topicMaster);
//            this.topicRepository.deleteById(topicId);
            mainResponse.setMessage("Topic deleted successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }catch (Exception e){
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }
}
