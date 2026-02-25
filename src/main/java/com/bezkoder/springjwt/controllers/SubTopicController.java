package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.SubTopicMaster;
import com.bezkoder.springjwt.payload.request.SubTopicRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.SubTopicResponse;
import com.bezkoder.springjwt.services.SubTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subtopic")
@CrossOrigin(origins = "*")
public class SubTopicController {
    @Autowired
    private SubTopicService subTopicService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody SubTopicRequest subTopicRequest){
        MainResponse mainResponse = this.subTopicService.create(subTopicRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/update")
    public ResponseEntity update(@RequestBody SubTopicRequest subTopicRequest){
        MainResponse mainResponse = this.subTopicService.update(subTopicRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{subTopicId}")
    public ResponseEntity getById(@PathVariable("subTopicId") Integer subTopicId){
        SubTopicMaster subTopicMaster = this.subTopicService.getById(subTopicId);
        return new ResponseEntity(subTopicMaster, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<SubTopicMaster> subTopicMasters = this.subTopicService.getAll();
        return new ResponseEntity(subTopicMasters, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<SubTopicMaster> subTopicMasters = this.subTopicService.getAllActive();
        return new ResponseEntity(subTopicMasters, HttpStatus.OK);
    }

    // Topic wise sub topics
    @GetMapping("/topicwisesubtopics/{topicId}")
    public ResponseEntity topicWiseSubTopics(@PathVariable("topicId") Integer topicId){
        List<SubTopicResponse> subTopicResponses = this.subTopicService.topicWiseSubTopics(topicId);
        return new ResponseEntity(subTopicResponses, HttpStatus.OK);
    }

    @GetMapping("/topicwiseActivesubtopics/{topicId}")
    public ResponseEntity topicWiseActiveSubTopics(@PathVariable("topicId") Integer topicId){
        List<SubTopicResponse> subTopicResponses = this.subTopicService.topicWiseActiveSubTopics(topicId);
        return new ResponseEntity(subTopicResponses, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{subTopicId}")
    public ResponseEntity delete(@PathVariable("subTopicId") Integer subTopicId){
        MainResponse mainResponse = this.subTopicService.delete(subTopicId);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
