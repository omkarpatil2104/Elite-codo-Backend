package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.TopicMaster;
import com.bezkoder.springjwt.payload.request.TopicRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.TopicResponse;
import com.bezkoder.springjwt.services.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topic")
@CrossOrigin(origins = "*")
public class TopicController {
    @Autowired
    private TopicService topicService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TopicRequest topicRequest){
        MainResponse mainResponse = this.topicService.create(topicRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody TopicRequest topicRequest){
        MainResponse mainResponse = this.topicService.update(topicRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{topicId}")
    public ResponseEntity getById(@PathVariable("topicId") Integer topicId){
        TopicResponse topicResponse = this.topicService.getById(topicId);
        return new ResponseEntity(topicResponse, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<TopicMaster> topicMasters = this.topicService.getAll();
        return new ResponseEntity(topicMasters, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<TopicResponse> topicResponses = this.topicService.getAllActive();
        return new ResponseEntity(topicResponses, HttpStatus.OK);
    }

    @GetMapping("/chapterwisetopics/{chapterId}")
    public ResponseEntity chapterWiseTopics(@PathVariable("chapterId") Integer chapterId){
        List<TopicResponse> topicResponses = this.topicService.chapterWiseTopics(chapterId);
        return new ResponseEntity(topicResponses, HttpStatus.OK);
    }

    @GetMapping("/chapterwiseActivetopics/{chapterId}")
    public ResponseEntity chapterWiseActiveTopics(@PathVariable("chapterId") Integer chapterId){
        List<TopicResponse> topicResponses = this.topicService.chapterWiseActiveTopics(chapterId);
        return new ResponseEntity(topicResponses, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{topicId}")
    public ResponseEntity delete(@PathVariable("topicId") Integer topicId){
        MainResponse mainResponse = this.topicService.delete(topicId);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
