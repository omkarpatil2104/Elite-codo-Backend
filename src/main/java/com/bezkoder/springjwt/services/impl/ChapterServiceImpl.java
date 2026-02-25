package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.ChapterRequest;
import com.bezkoder.springjwt.payload.request.ChapterWiseWeightageRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.TopicResponse;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.ChapterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private EntranceExamRepository entranceExamRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubTopicRepository subTopicRepository;

    @Autowired
    private TestRepository testRepository;
    
    @Autowired
    private TopicRepository topicRepository;

    @Override
    public MainResponse create(ChapterRequest chapterRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(chapterRequest.getEntranceExamId());
            Optional<StandardMaster> standardMaster = this.standardRepository.findById(chapterRequest.getStandardId());
            if (standardMaster.isPresent()){
                Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(chapterRequest.getSubjectId());
                if (subjectMaster.isPresent()){
                    ChapterMaster chapterMaster = new ChapterMaster();
                    BeanUtils.copyProperties(chapterRequest,chapterMaster);
                    try {
                        String chapterName = chapterRequest.getChapterName().toUpperCase();
                        chapterMaster.setChapterName(chapterName);
                        chapterMaster.setEntranceExamMaster(entranceExamMaster.get());
                        chapterMaster.setStandardMaster(standardMaster.get());
                        chapterMaster.setSubjectMaster(subjectMaster.get());
                        chapterMaster.setStatus("Active");
                        chapterMaster.setDate(new Date());
                        this.chapterRepository.save(chapterMaster);
                        mainResponse.setMessage("Chapter created successfully");
                        mainResponse.setResponseCode(HttpStatus.OK.value());
                        mainResponse.setFlag(true);
                    }catch (Exception e){
                        e.printStackTrace();
                        mainResponse.setMessage("Something went wrong");
                        mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                        mainResponse.setFlag(false);
                    }
                }else {
                    mainResponse.setMessage("Subject not found");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            }else {
                mainResponse.setMessage("Standard not found");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        return mainResponse;
    }

    @Override
    public MainResponse update(ChapterRequest chapterRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(chapterRequest.getEntranceExamId());
        Optional<ChapterMaster> chapterMaster = this.chapterRepository.findById(chapterRequest.getChapterId());
        Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(chapterRequest.getSubjectId());
        Optional<StandardMaster> standardMaster = this.standardRepository.findById(chapterRequest.getStandardId());
        BeanUtils.copyProperties(chapterRequest,chapterMaster.get());
        chapterMaster.get().setDate(new Date());
        chapterMaster.get().setSubjectMaster(subjectMaster.get());
        chapterMaster.get().setStandardMaster(standardMaster.get());
        chapterMaster.get().setEntranceExamMaster(entranceExamMaster.get());
        try {
            this.chapterRepository.save(chapterMaster.get());
            mainResponse.setMessage("Chapter updated successfully");
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
    public ChapterMaster getChapterById(Integer chapterId) {
        Optional<ChapterMaster> chapterMaster = this.chapterRepository.findById(chapterId);
        return chapterMaster.get();
    }

    @Override
    public List<ChapterMaster> getAll() {
        List<ChapterMaster> chapterMasters = this.chapterRepository.findAll();
        Collections.reverse(chapterMasters);
        return chapterMasters;
    }

    @Override
    public List<ChapterMaster> getAllActiveChapters() {
        List<ChapterMaster> chapterMasters = this.chapterRepository.getAllActiveChapters();
        Collections.reverse(chapterMasters);
        return chapterMasters;
    }

    @Override
    public List<ChapterMaster> subjectWiseChapter(Integer subjectId) {
        List<ChapterMaster> chapterMasters = this.chapterRepository.subjectWiseChapter(subjectId);
        System.out.println("CH MAster = "+chapterMasters);
        Collections.reverse(chapterMasters);
        return chapterMasters;
    }

    @Override
    public MainResponse delete(Integer chapterId) {
        MainResponse mainResponse = new MainResponse();
        ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId).orElseThrow(()->new RuntimeException("Chapter not found"));
        try {
//            this.chapterRepository.deleteById(chapterId);
            chapterMaster.setStatus("Deleted");
            this.chapterRepository.save(chapterMaster);
            mainResponse.setMessage("Chapter deleted successfully");
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
    public MainResponse chapterWiseWeightage(ChapterWiseWeightageRequest chapterWiseWeightageRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(chapterWiseWeightageRequest.getEntranceExamId());
        Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(chapterWiseWeightageRequest.getSubjectId());
        Optional<ChapterMaster> chapterMaster = this.chapterRepository.findById(chapterWiseWeightageRequest.getChapterId());

        try {
            chapterMaster.get().setWeightage(chapterWiseWeightageRequest.getWeightage());
            this.chapterRepository.save(chapterMaster.get());
            mainResponse.setMessage("Weightage added to the chapter");
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
    public List<ChapterMaster> subjectStandWiseChapter(Integer subjectId, String isactive, Integer standardId) {
        List<ChapterMaster> chapterMasters ;
        if(isactive.equalsIgnoreCase("Active")){
            chapterMasters = chapterRepository.subjectStandardWiseActiveChapter(subjectId,standardId);

        }else{
             chapterMasters = chapterRepository.subjectStandardWiseChapter(subjectId,standardId);

        }

        return chapterMasters;
    }
}
