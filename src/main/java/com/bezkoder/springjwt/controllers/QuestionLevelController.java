package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.QuestionLevel;
import com.bezkoder.springjwt.payload.request.QuestionLevelRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.QuestionLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionlevel")
@CrossOrigin(origins = "*")
public class QuestionLevelController {
    @Autowired
    private QuestionLevelService questionLevelService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody QuestionLevelRequest questionLevelRequest){
        MainResponse mainResponse = this.questionLevelService.create(questionLevelRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody QuestionLevelRequest questionLevelRequest){
        MainResponse mainResponse = this.questionLevelService.update(questionLevelRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{questionLevelId}")
    public ResponseEntity getById(@PathVariable("questionLevelId") Integer questionLevelId){
        QuestionLevel questionLevel = this.questionLevelService.getById(questionLevelId);
        return new ResponseEntity(questionLevel, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<QuestionLevel> questionLevels = this.questionLevelService.getAll();
        return new ResponseEntity(questionLevels, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<QuestionLevel> questionLevels = this.questionLevelService.getAllActive();
        return new ResponseEntity(questionLevels, HttpStatus.OK);
    }
}
