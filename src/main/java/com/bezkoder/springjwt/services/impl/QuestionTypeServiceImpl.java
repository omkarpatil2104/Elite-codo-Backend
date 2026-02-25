package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.QuestionType;
import com.bezkoder.springjwt.payload.request.QuestionTypeRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.repository.QuestionTypeRepository;
import com.bezkoder.springjwt.services.QuestionTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionTypeServiceImpl implements QuestionTypeService {
    @Autowired
    private QuestionTypeRepository questionTypeRepository;

    @Override
    public MainResponse create(QuestionTypeRequest questionTypeRequest) {
        MainResponse mainResponse = new MainResponse();
        QuestionType questionType = new QuestionType();
        try {
            BeanUtils.copyProperties(questionTypeRequest, questionType);
            questionType.setDate(new Date());
            this.questionTypeRepository.save(questionType);
            mainResponse.setMessage("Question type created successfully");
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
    public MainResponse update(QuestionTypeRequest questionTypeRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<QuestionType> questionType = Optional.ofNullable(this.questionTypeRepository.findById(questionTypeRequest.getQuestionTypeId()).orElseThrow(()->new RuntimeException("Question type not found")));

        try {
            BeanUtils.copyProperties(questionTypeRequest, questionType.get());
            questionType.get().setDate(new Date());
            this.questionTypeRepository.save(questionType.get());
            mainResponse.setMessage("Question type updated successfully");
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
    public QuestionType getById(Integer questionTypeId) {
        Optional<QuestionType> questionType = Optional.ofNullable(this.questionTypeRepository.findById(questionTypeId).orElseThrow(()->new RuntimeException("Question type not found")));
        if (questionType.isPresent()){
            return questionType.get();
        }else {
            return null;
        }
    }

    @Override
    public List<QuestionType> getAll() {
        List<QuestionType> questionTypes = this.questionTypeRepository.findAll();
        return questionTypes;
    }

    @Override
    public List<QuestionType> getAllActive() {
        List<QuestionType> questionTypes = this.questionTypeRepository.getAllActive();
        return questionTypes;
    }

    @Override
    public MainResponse delete(Integer questionTypeId) {
        MainResponse mainResponse = new MainResponse();
        QuestionType questionType = this.questionTypeRepository.findById(questionTypeId).orElseThrow(()->new RuntimeException("Question type not found"));
        try {
//            this.questionTypeRepository.deleteById(questionTypeId);
            questionType.setStatus("Deleted");
            this.questionTypeRepository.save(questionType);
            mainResponse.setMessage("Question type deleted successfully");
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
