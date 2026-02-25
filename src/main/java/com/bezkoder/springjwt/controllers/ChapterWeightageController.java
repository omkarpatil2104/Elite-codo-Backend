package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.ChapterWeightageMaster;
import com.bezkoder.springjwt.payload.request.ChapterWiseWeightageRequest;
import com.bezkoder.springjwt.payload.response.ChapterWeightageResponse;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.ChapterWeightageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/chapterweightage")
public class ChapterWeightageController {
    @Autowired
    private ChapterWeightageService chapterWeightageService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody ChapterWiseWeightageRequest chapterWiseWeightageRequest){
        MainResponse mainResponse = this.chapterWeightageService.chapterWiseWeightage(chapterWiseWeightageRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody ChapterWiseWeightageRequest chapterWiseWeightageRequest){
        MainResponse mainResponse = this.chapterWeightageService.update(chapterWiseWeightageRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{chapterWeightageId}")
    public ResponseEntity getById(@PathVariable("chapterWeightageId") Integer chapterWeightageId){
        ChapterWeightageResponse chapterWeightageResponse = this.chapterWeightageService.getById(chapterWeightageId);
        return new ResponseEntity(chapterWeightageResponse, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<ChapterWeightageResponse> chapterWeightageResponses = this.chapterWeightageService.getAll();
        return new ResponseEntity(chapterWeightageResponses, HttpStatus.OK);
    }


}
