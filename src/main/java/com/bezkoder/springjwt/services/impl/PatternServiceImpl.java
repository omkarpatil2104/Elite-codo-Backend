package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.PatternMaster;
import com.bezkoder.springjwt.payload.request.PatternRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.repository.PatternRepository;
import com.bezkoder.springjwt.services.PatternService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PatternServiceImpl implements PatternService {
    @Autowired
    private PatternRepository patternRepository;

    @Override
    public MainResponse create(PatternRequest patternRequest) {
        MainResponse mainResponse = new MainResponse();
        PatternMaster patternMaster = new PatternMaster();
        BeanUtils.copyProperties(patternRequest,patternMaster);
        try {
            patternMaster.setDate(new Date());
            this.patternRepository.save(patternMaster);
            mainResponse.setMessage("Pattern created successfully");
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
    public MainResponse update(PatternRequest patternRequest) {
        MainResponse mainResponse = new MainResponse();
        PatternMaster patternMaster = this.patternRepository.findById(patternRequest.getPatternId()).get();
        BeanUtils.copyProperties(patternRequest,patternMaster);
        try {
            patternMaster.setDate(new Date());
            this.patternRepository.save(patternMaster);
            mainResponse.setMessage("Pattern updated successfully");
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
    public PatternMaster getById(Integer patternId) {
        PatternMaster patternMaster = this.patternRepository.findById(patternId).get();
        return patternMaster;
    }

    @Override
    public List<PatternMaster> getAllActive() {
        List<PatternMaster> patternMasters = this.patternRepository.getAllActive();
        return patternMasters;
    }

    @Override
    public List<PatternMaster> getAll() {
        List<PatternMaster> patternMasters = this.patternRepository.findAll();
        return patternMasters;
    }

    @Override
    public MainResponse delete(Integer patternId) {
        MainResponse mainResponse = new MainResponse();
        try {
            this.patternRepository.deleteById(patternId);
            mainResponse.setMessage("Pattern deleted successfully");
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
