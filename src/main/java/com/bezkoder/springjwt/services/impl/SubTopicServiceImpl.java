package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.SubTopicRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.SubTopicResponse;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.SubTopicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubTopicServiceImpl implements SubTopicService {
    @Autowired
    private SubTopicRepository subTopicRepository;
    @Autowired
    private EntranceExamRepository entranceExamRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private TopicRepository topicRepository;

    @Override
    public MainResponse create(SubTopicRequest subTopicRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(this.entranceExamRepository.findById(subTopicRequest.getEntranceExamId()).orElseThrow(()->new RuntimeException("Entrance Exam not found")));
        Optional<StandardMaster> standardMaster = Optional.ofNullable(this.standardRepository.findById(subTopicRequest.getStandardId()).orElseThrow(()->new RuntimeException("Standard not found exception")));
        Optional<SubjectMaster> subjectMaster = Optional.ofNullable(this.subjectRepository.findById(subTopicRequest.getSubjectId()).orElseThrow(()->new RuntimeException("Subject not found")));
        Optional<ChapterMaster> chapterMaster = Optional.ofNullable(this.chapterRepository.findById(subTopicRequest.getChapterId()).orElseThrow(()->new RuntimeException("Chapter not found")));
        Optional<TopicMaster> topicMaster = Optional.ofNullable(this.topicRepository.findById(subTopicRequest.getTopicId()).orElseThrow(()->new RuntimeException("Topic not found")));

        SubTopicMaster subTopicMaster = new SubTopicMaster();
        try {
            BeanUtils.copyProperties(subTopicRequest,subTopicMaster);
            subTopicMaster.setEntranceExamMaster(entranceExamMaster.get());
            subTopicMaster.setStandardMaster(standardMaster.get());
            subTopicMaster.setSubjectMaster(subjectMaster.get());
            subTopicMaster.setChapterMaster(chapterMaster.get());
            subTopicMaster.setDate(new Date());
            subTopicMaster.setTopicMaster(topicMaster.get());
            this.subTopicRepository.save(subTopicMaster);
            mainResponse.setMessage("Subtopic created successfully");
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
    public MainResponse update(SubTopicRequest subTopicRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(this.entranceExamRepository.findById(subTopicRequest.getEntranceExamId()).orElseThrow(()->new RuntimeException("Entrance Exam not found")));
        Optional<StandardMaster> standardMaster = Optional.ofNullable(this.standardRepository.findById(subTopicRequest.getStandardId()).orElseThrow(()->new RuntimeException("Standard not found exception")));
        Optional<SubjectMaster> subjectMaster = Optional.ofNullable(this.subjectRepository.findById(subTopicRequest.getSubjectId()).orElseThrow(()->new RuntimeException("Subject not found")));
        Optional<ChapterMaster> chapterMaster = Optional.ofNullable(this.chapterRepository.findById(subTopicRequest.getChapterId()).orElseThrow(()->new RuntimeException("Chapter not found")));
        Optional<TopicMaster> topicMaster = Optional.ofNullable(this.topicRepository.findById(subTopicRequest.getTopicId()).orElseThrow(()->new RuntimeException("Topic not found")));

        Optional<SubTopicMaster> subTopicMaster = Optional.ofNullable(this.subTopicRepository.findById(subTopicRequest.getSubTopicId()).orElseThrow(()->new RuntimeException("Sub topic not found")));

        try {
            BeanUtils.copyProperties(subTopicRequest,subTopicMaster.get());
            subTopicMaster.get().setEntranceExamMaster(entranceExamMaster.get());
            subTopicMaster.get().setStandardMaster(standardMaster.get());
            subTopicMaster.get().setSubjectMaster(subjectMaster.get());
            subTopicMaster.get().setChapterMaster(chapterMaster.get());
            subTopicMaster.get().setDate(new Date());
            subTopicMaster.get().setTopicMaster(topicMaster.get());
            this.subTopicRepository.save(subTopicMaster.get());
            mainResponse.setMessage("Subtopic updated successfully");
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
    public SubTopicMaster getById(Integer subTopicId) {
        Optional<SubTopicMaster> subTopicMaster = Optional.ofNullable(this.subTopicRepository.findById(subTopicId).orElseThrow(()->new RuntimeException("Sub topic not found")));
        return subTopicMaster.get();
    }

    @Override
    public List<SubTopicMaster> getAll() {
        List<SubTopicMaster> subTopicMasters = this.subTopicRepository.findAll();
        return subTopicMasters;
    }

    @Override
    public List<SubTopicMaster> getAllActive() {
        List<SubTopicMaster> subTopicMasters = this.subTopicRepository.getAllActive();
        return subTopicMasters;
    }

    @Override
    public List<SubTopicResponse> topicWiseSubTopics(Integer topicId) {
        List<SubTopicResponse> subTopicResponses = this.subTopicRepository.topicWiseSubTopics(topicId);
        return subTopicResponses;
    }

    @Override
    public List<SubTopicResponse> topicWiseActiveSubTopics(Integer topicId) {
        List<SubTopicResponse> subTopicResponses = this.subTopicRepository.topicWiseActiveSubTopics(topicId);
        return subTopicResponses;
    }

    @Override
    public MainResponse delete(Integer subTopicId) {
        MainResponse mainResponse = new MainResponse();
        try {
            SubTopicMaster subTopicMaster = this.subTopicRepository.findById(subTopicId).orElseThrow(()->new RuntimeException("Sub topic not found"));
            subTopicMaster.setStatus("Deleted");
            this.subTopicRepository.save(subTopicMaster);
//            this.subTopicRepository.deleteById(subTopicId);
            mainResponse.setMessage("Sub topic deleted successfully");
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
