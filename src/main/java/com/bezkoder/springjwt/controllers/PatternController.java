package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.PatternMaster;
import com.bezkoder.springjwt.payload.request.PatternRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pattern")
public class PatternController {
    @Autowired
    private PatternService patternService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody PatternRequest patternRequest){
        MainResponse mainResponse = this.patternService.create(patternRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody PatternRequest patternRequest){
        MainResponse mainResponse = this.patternService.update(patternRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{patternId}")
    public ResponseEntity getById(@PathVariable("patternId")Integer patternId){
        PatternMaster patternMaster = this.patternService.getById(patternId);
        return new ResponseEntity(patternMaster, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<PatternMaster> patternMasters = this.patternService.getAllActive();
        return new ResponseEntity(patternMasters, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<PatternMaster> patternMasters = this.patternService.getAll();
        return new ResponseEntity(patternMasters, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{patternId}")
    public ResponseEntity delete(@PathVariable("patternId") Integer patternId){
        MainResponse mainResponse = this.patternService.delete(patternId);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
