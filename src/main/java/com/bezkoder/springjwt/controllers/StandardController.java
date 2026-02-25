package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.payload.request.StandardRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.StandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/standard")
public class StandardController {
    @Autowired
    private StandardService standardService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody StandardRequest standardRequest){
        MainResponse mainResponse = this.standardService.create(standardRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody StandardRequest standardRequest){
        MainResponse mainResponse = this.standardService.update(standardRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getstandardbyid/{standardId}")
    public ResponseEntity getStandardById(@PathVariable("standardId") Integer standardId){
        StandardMaster standardMaster = this.standardService.getStandardById(standardId);
        if (standardMaster!=null){
            return new ResponseEntity(standardMaster, HttpStatus.OK);
        }else {
            return new ResponseEntity(standardMaster, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getallstandards")
    public ResponseEntity getAllStandards(){
        List<StandardMaster> standardMasters = this.standardService.getAllStandard();
        return new ResponseEntity(standardMasters, HttpStatus.OK);
    }

    @GetMapping("/allactivestandards")
    public ResponseEntity getAllActiveStandards(){
        List<StandardMaster> standardMasters = this.standardService.getAllActiveStandard();
        return new ResponseEntity(standardMasters, HttpStatus.OK);
    }

    @GetMapping("/standardwiseentranceexam/{standardId}")
    public ResponseEntity standardWiseEntranceExam(@PathVariable("standardId") Integer standardId){
        List<EntranceExamMaster> entranceExamMasters = this.standardService.standardWiseEntranceExam(standardId);
        return new ResponseEntity(entranceExamMasters, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{standardId}")
    public ResponseEntity delete(@PathVariable("standardId") Integer standardId){
        MainResponse mainResponse = this.standardService.delete(standardId);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }


}
