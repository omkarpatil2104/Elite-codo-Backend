package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.ChapterMaster;
import com.bezkoder.springjwt.models.ChapterWeightageMaster;
import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.payload.request.ChapterWiseWeightageRequest;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.repository.ChapterRepository;
import com.bezkoder.springjwt.repository.ChapterWeightageRepository;
import com.bezkoder.springjwt.repository.EntranceExamRepository;
import com.bezkoder.springjwt.repository.SubjectRepository;
import com.bezkoder.springjwt.services.ChapterWeightageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterWeightageServiceImpl implements ChapterWeightageService {
    @Autowired
    private ChapterWeightageRepository chapterWeightageRepository;

    @Autowired
    private EntranceExamRepository entranceExamRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Override
    public MainResponse chapterWiseWeightage(ChapterWiseWeightageRequest chapterWiseWeightageRequest) {
        MainResponse mainResponse = new MainResponse();
        ChapterWeightageMaster chapterWeightageMaster = new ChapterWeightageMaster();
        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(chapterWiseWeightageRequest.getEntranceExamId()).get();
        SubjectMaster subjectMaster = this.subjectRepository.findById(chapterWiseWeightageRequest.getSubjectId()).get();
        ChapterMaster chapterMaster = this.chapterRepository.findById(chapterWiseWeightageRequest.getChapterId()).get();

        BeanUtils.copyProperties(chapterWiseWeightageRequest,chapterWeightageMaster);
        try {
            chapterWeightageMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            chapterWeightageMaster.setSubjectId(subjectMaster.getSubjectId());
            chapterWeightageMaster.setChapterId(chapterMaster.getChapterId());
            chapterWeightageMaster.setDate(new Date());
            this.chapterWeightageRepository.save(chapterWeightageMaster);
            mainResponse.setMessage("Weightage added to the chapter");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }catch (Exception e){
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }
        return mainResponse;
    }

    @Override
    public MainResponse update(ChapterWiseWeightageRequest chapterWiseWeightageRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<ChapterWeightageMaster> chapterWeightageMaster = this.chapterWeightageRepository.findById(chapterWiseWeightageRequest.getChapterWeightageId());

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(chapterWiseWeightageRequest.getEntranceExamId()).get();
        SubjectMaster subjectMaster = this.subjectRepository.findById(chapterWiseWeightageRequest.getSubjectId()).get();
        ChapterMaster chapterMaster = this.chapterRepository.findById(chapterWiseWeightageRequest.getChapterId()).get();

        if (chapterWeightageMaster.isPresent()){
            BeanUtils.copyProperties(chapterWiseWeightageRequest,chapterWeightageMaster.get());
            try {
                chapterWeightageMaster.get().setDate(new Date());
                chapterWeightageMaster.get().setEntranceExamId(entranceExamMaster.getEntranceExamId());
                chapterWeightageMaster.get().setSubjectId(subjectMaster.getSubjectId());
                chapterWeightageMaster.get().setChapterId(chapterMaster.getChapterId());
                this.chapterWeightageRepository.save(chapterWeightageMaster.get());
                mainResponse.setMessage("Weightage of chapter is updated");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }catch (Exception e){
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }
        }else {
            mainResponse.setMessage("Chapter weightage master not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public ChapterWeightageResponse getById(Integer chapterWeightageId) {
        ChapterWeightageResponse chapterWeightageResponse = new ChapterWeightageResponse();
        Optional<ChapterWeightageMaster> chapterWeightageMaster = this.chapterWeightageRepository.findById(chapterWeightageId);
        if (chapterWeightageMaster.isPresent()){
            EntranceExamMaster entranceExamMaster =  this.entranceExamRepository.findById(chapterWeightageMaster.get().getEntranceExamId()).get();
            EntranceExamMasterResponse entranceExamMasterResponse = new EntranceExamMasterResponse();
            entranceExamMasterResponse.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            entranceExamMasterResponse.setEntranceExamName(entranceExamMaster.getEntranceExamName());
            entranceExamMasterResponse.setDate(entranceExamMaster.getDate());
            entranceExamMasterResponse.setStatus(entranceExamMaster.getStatus());

            SubjectMaster subjectMaster = this.subjectRepository.findById(chapterWeightageMaster.get().getSubjectId()).get();
            SubjectMastersResponse subjectMastersResponse = new SubjectMastersResponse();
            subjectMastersResponse.setSubjectId(subjectMaster.getSubjectId());
            subjectMastersResponse.setSubjectName(subjectMaster.getSubjectName());
            subjectMastersResponse.setStatus(subjectMaster.getStatus());
            subjectMastersResponse.setDate(subjectMaster.getDate());

            ChapterMaster chapterMaster = this.chapterRepository.findById(chapterWeightageMaster.get().getChapterId()).get();
            ChapterMasterResponse chapterMasterResponse = new ChapterMasterResponse();
            chapterMasterResponse.setChapterId(chapterMaster.getChapterId());
            chapterMasterResponse.setChapterName(chapterMaster.getChapterName());
            chapterMasterResponse.setStatus(chapterMaster.getStatus());
            chapterMasterResponse.setDate(chapterMaster.getDate());

            chapterWeightageResponse.setWeightage(chapterWeightageMaster.get().getWeightage());
            chapterWeightageResponse.setChapterMaster(chapterMasterResponse);
            chapterWeightageResponse.setChapterWeightageId(chapterWeightageMaster.get().getChapterWeightageId());
            chapterWeightageResponse.setEntranceExamMaster(entranceExamMasterResponse);
            chapterWeightageResponse.setSubjectMaster(subjectMastersResponse);
            chapterWeightageResponse.setStatus(chapterWeightageMaster.get().getStatus());
            chapterWeightageResponse.setDate(chapterWeightageMaster.get().getDate());

        }
        return chapterWeightageResponse;
    }

    @Override
    public List<ChapterWeightageResponse> getAll() {
        List<ChapterWeightageResponse> chapterWeightageResponses = new ArrayList<>();
        List<ChapterWeightageMaster> chapterWeightageMasters = this.chapterWeightageRepository.findAll();

        List<EntranceExamMasterResponse> entranceExamMasters = new ArrayList<>();
        List<ChapterMasterResponse> chapterMasters = new ArrayList<>();
        List<SubjectMastersResponse> subjectMasters = new ArrayList<>();


        for (ChapterWeightageMaster chapterWeightageMaster : chapterWeightageMasters) {

            ChapterWeightageResponse chapterWeightageResponse = new ChapterWeightageResponse();

            EntranceExamMaster entranceExamMaster =  this.entranceExamRepository.findById(chapterWeightageMaster.getEntranceExamId()).get();
            EntranceExamMasterResponse entranceExamMasterResponse = new EntranceExamMasterResponse();
            entranceExamMasterResponse.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            entranceExamMasterResponse.setEntranceExamName(entranceExamMaster.getEntranceExamName());
            entranceExamMasterResponse.setDate(entranceExamMaster.getDate());
            entranceExamMasterResponse.setStatus(entranceExamMaster.getStatus());
            entranceExamMasters.add(entranceExamMasterResponse);

            SubjectMaster subjectMaster = this.subjectRepository.findById(chapterWeightageMaster.getSubjectId()).get();
            SubjectMastersResponse subjectMastersResponse = new SubjectMastersResponse();
            subjectMastersResponse.setSubjectId(subjectMaster.getSubjectId());
            subjectMastersResponse.setSubjectName(subjectMaster.getSubjectName());
            subjectMastersResponse.setStatus(subjectMaster.getStatus());
            subjectMastersResponse.setDate(subjectMaster.getDate());
            subjectMasters.add(subjectMastersResponse);

            ChapterMaster chapterMaster = this.chapterRepository.findById(chapterWeightageMaster.getChapterId()).get();
            ChapterMasterResponse chapterMasterResponse = new ChapterMasterResponse();
            chapterMasterResponse.setChapterId(chapterMaster.getChapterId());
            chapterMasterResponse.setChapterName(chapterMaster.getChapterName());
            chapterMasterResponse.setStatus(chapterMaster.getStatus());
            chapterMasterResponse.setDate(chapterMaster.getDate());
            chapterMasters.add(chapterMasterResponse);

            chapterWeightageResponse.setEntranceExamMaster(entranceExamMasterResponse);
            chapterWeightageResponse.setSubjectMaster(subjectMastersResponse);
            chapterWeightageResponse.setChapterMaster(chapterMasterResponse);
            chapterWeightageResponse.setChapterWeightageId(chapterWeightageMaster.getChapterWeightageId());
            chapterWeightageResponse.setWeightage(chapterWeightageMaster.getWeightage());
            chapterWeightageResponse.setStatus(chapterWeightageMaster.getStatus());
            chapterWeightageResponse.setDate(chapterWeightageMaster.getDate());

            chapterWeightageResponses.add(chapterWeightageResponse);

        }
        return chapterWeightageResponses;
    }
}
