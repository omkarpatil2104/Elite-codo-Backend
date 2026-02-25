package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.TestType;
import com.bezkoder.springjwt.payload.request.TestTypeRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.TestTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testtype")
@CrossOrigin(origins = "*")
public class TestTypeController {
    @Autowired
    private TestTypeService testTypeService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TestTypeRequest testTypeRequest){
        MainResponse mainResponse = this.testTypeService.create(testTypeRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody TestTypeRequest testTypeRequest){
        MainResponse mainResponse = this.testTypeService.update(testTypeRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{testTypeId}")
    public ResponseEntity getTestTypeById(@PathVariable("testTypeId") Integer testTypeId){
        TestType testType = this.testTypeService.getTestTypeById(testTypeId);
        return new ResponseEntity(testType, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAllTestTypes(){
        List<TestType> testTypes = this.testTypeService.getAllTestTypes();
        return new ResponseEntity(testTypes, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActiveTestTypes(){
        List<TestType> testTypes = this.testTypeService.getAllActiveTestTypes();
        return new ResponseEntity(testTypes, HttpStatus.OK);
    }
}
