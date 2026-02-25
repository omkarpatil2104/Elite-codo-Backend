package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.QuestionLevel;
import com.bezkoder.springjwt.payload.request.QuestionLevelRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.repository.QuestionLevelRepository;
import com.bezkoder.springjwt.services.QuestionLevelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionLevelServiceImpl implements QuestionLevelService {
    @Autowired
    private QuestionLevelRepository questionLevelRepository;

    @Override
    public MainResponse create(QuestionLevelRequest questionLevelRequest) {
        MainResponse mainResponse = new MainResponse();
        QuestionLevel questionLevel = new QuestionLevel();
        try {
            BeanUtils.copyProperties(questionLevelRequest,questionLevel);
            questionLevel.setDate(new Date());
            this.questionLevelRepository.save(questionLevel);
            mainResponse.setMessage("Question level created successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }catch (Exception exception){
            exception.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public MainResponse update(QuestionLevelRequest questionLevelRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<QuestionLevel> questionLevel = Optional.ofNullable(this.questionLevelRepository.findById(questionLevelRequest.getQuestionLevelId()).orElseThrow(()->new RuntimeException("Question level not found")));
        try {
            BeanUtils.copyProperties(questionLevelRequest,questionLevel.get());
            questionLevel.get().setDate(new Date());
            this.questionLevelRepository.save(questionLevel.get());
            mainResponse.setMessage("Question level updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }catch (Exception exception){
            exception.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public QuestionLevel getById(Integer questionLevelId) {
        Optional<QuestionLevel> questionLevel = Optional.ofNullable(this.questionLevelRepository.findById(questionLevelId).orElseThrow(()->new RuntimeException("Question level not found")));
        if (questionLevel.isPresent()){
            return questionLevel.get();
        }else {
            return null;
        }
    }

    @Override
    public List<QuestionLevel> getAll() {
        List<QuestionLevel> questionLevels = this.questionLevelRepository.findAll();
        return questionLevels;
    }

    @Override
    public List<QuestionLevel> getAllActive() {
        List<QuestionLevel> questionLevels = this.questionLevelRepository.getAllActive();
        return questionLevels;
    }
}
