package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.payload.request.StandardRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.StandardResponse;
import com.bezkoder.springjwt.repository.EntranceExamRepository;
import com.bezkoder.springjwt.repository.StandardRepository;
import com.bezkoder.springjwt.services.StandardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StandardServiceImpl implements StandardService {
    @Autowired
    private StandardRepository standardRepository;

    @Autowired
    private EntranceExamRepository entranceExamRepository;

    @Override
    public MainResponse create(StandardRequest standardRequest) {
        MainResponse mainResponse = new MainResponse();
        StandardMaster standardMaster = new StandardMaster();
//        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(this.entranceExamRepository.findById(standardRequest.getEntranceExamId()).orElseThrow(()->new RuntimeException("Entrance exam not found")));

        try {
            BeanUtils.copyProperties(standardRequest,standardMaster);
//            standardMaster.setEntranceExamMaster(entranceExamMaster.get());
            standardMaster.setDate(new Date());
            this.standardRepository.save(standardMaster);
            mainResponse.setMessage("Standard created");
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
    public MainResponse update(StandardRequest standardRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<StandardMaster> standardMaster = this.standardRepository.findById(standardRequest.getStandardId());
//        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(this.entranceExamRepository.findById(standardRequest.getEntranceExamId()).orElseThrow(()->new RuntimeException("Entrance exam not found")));
        if (standardMaster.isPresent()){
            BeanUtils.copyProperties(standardRequest,standardMaster.get());
            try {
//                standardMaster.get().setEntranceExamMaster(entranceExamMaster.get());
                standardMaster.get().setDate(new Date());
                this.standardRepository.save(standardMaster.get());
                mainResponse.setMessage("Standard updated successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }catch (Exception e){
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        }else {
            mainResponse.setMessage("Class not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public StandardMaster getStandardById(Integer standardId) {
        Optional<StandardMaster> standardMaster = this.standardRepository.findById(standardId);
        if (standardMaster.isPresent()){
            return standardMaster.get();
        }else {
            return null;
        }
    }

    @Override
    public List<StandardMaster> getAllStandard() {
        List<StandardMaster> standardMasters = this.standardRepository.findAll();
        return standardMasters;
    }

    @Override
    public List<StandardMaster> getAllActiveStandard() {
        List<StandardMaster> standardMasters = this.standardRepository.getAllActiveClasses();
        return standardMasters;
    }

    @Override
    public List<EntranceExamMaster> standardWiseEntranceExam(Integer standardId) {
        List<EntranceExamMaster> entranceExamMasters = this.entranceExamRepository.standardWiseEntranceExam(standardId);
        return entranceExamMasters;
    }

    @Override
    public MainResponse delete(Integer standardId) {
        MainResponse mainResponse = new MainResponse();
        Optional<StandardMaster> standardMaster = this.standardRepository.findById(standardId);
        if (standardMaster.isPresent()) {
            try {
                standardMaster.get().setStatus("Deleted");
                standardRepository.save(standardMaster.get());
                mainResponse.setMessage("Standard deleted successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
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

//    @Override
//    public List<StandardResponse> entranceExamWiseStandard(Integer entranceExamId) {
//            List<StandardResponse> standardResponses = this.standardRepository.entranceExamWiseStandard(entranceExamId);
//        return standardResponses;
//    }

}
