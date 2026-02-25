package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.QuestionType;
import com.bezkoder.springjwt.payload.request.QuestionTypeRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.QuestionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questiontype")
@CrossOrigin(origins = "*")
public class QuestionTypeController {
    @Autowired
    private QuestionTypeService questionTypeService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody QuestionTypeRequest questionTypeRequest){
        MainResponse mainResponse = this.questionTypeService.create(questionTypeRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody QuestionTypeRequest questionTypeRequest){
        MainResponse mainResponse = this.questionTypeService.update(questionTypeRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{questionTypeId}")
    public ResponseEntity getById(@PathVariable("questionTypeId") Integer questionTypeId){
        QuestionType questionType = this.questionTypeService.getById(questionTypeId);
        return new ResponseEntity(questionType, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<QuestionType> questionTypes = this.questionTypeService.getAll();
        return new ResponseEntity(questionTypes, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<QuestionType> questionTypes = this.questionTypeService.getAllActive();
        return new ResponseEntity(questionTypes, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{questionTypeId}")
    public ResponseEntity delete(@PathVariable("questionTypeId") Integer questionTypeId){
        MainResponse mainResponse = this.questionTypeService.delete(questionTypeId);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
