package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.SubjectRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.SubjectMasterResponse;
import com.bezkoder.springjwt.payload.response.SubjectResponse;
import com.bezkoder.springjwt.payload.response.TeacherResponse;
import com.bezkoder.springjwt.services.SubjectService;
import com.bezkoder.springjwt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/subject")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody SubjectRequest subjectRequest){
        System.out.println("hi");
        MainResponse mainResponse = this.subjectService.create(subjectRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody SubjectRequest subjectRequest){
        MainResponse mainResponse = this.subjectService.update(subjectRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getsubjectbyid/{subjectId}")
    public ResponseEntity getSubjectById(@PathVariable("subjectId") Integer subjectId){
        SubjectMaster subjectMaster = this.subjectService.getSubjectById(subjectId);
        return new ResponseEntity<>(subjectMaster, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<SubjectMaster> subjectMasters = this.subjectService.getAll();
        return new ResponseEntity<>(subjectMasters, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<SubjectMaster> subjectMasters = this.subjectService.getAllActive();
        return new ResponseEntity(subjectMasters, HttpStatus.OK);
    }

    @GetMapping("/subjectwiseteachers/{subjectId}")
    public ResponseEntity getSubjectWiseTeachers(@PathVariable("subjectId") Integer subjectId){
        List<TeacherResponse> teachers = this.userService.getSubjectWiseTeachers(subjectId);
        return new ResponseEntity(teachers, HttpStatus.OK);
    }

    @GetMapping("/standardwisesubject/{standardId}")
    public ResponseEntity standardWiseSubjects(@PathVariable("standardId") Integer standardId){
        SubjectResponse subjectResponses = this.subjectService.standardWiseSubjects(standardId);
        return new ResponseEntity(subjectResponses, HttpStatus.OK);
    }

    @GetMapping("/standardwiseActivesubject/{standardId}")
    public ResponseEntity standardWiseActiveSubjects(@PathVariable("standardId") Integer standardId){
        SubjectResponse subjectResponses = this.subjectService.standardWiseActiveSubjects(standardId);
        return new ResponseEntity(subjectResponses, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{subjectId}")
    public ResponseEntity delete(@PathVariable("subjectId") Integer subjectId){
        MainResponse mainResponse = this.subjectService.delete(subjectId);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

//    Entrance exam id wise subjects
    @GetMapping("/entranceexamwisesubjects/{entranceExamId}")
    public ResponseEntity entranceExamIdWiseSubjects(@PathVariable("entranceExamId") Integer entranceExamId){
        List<SubjectMasterResponse> subjectMasterResponses = this.subjectService.entranceExamIdWiseSubjects(entranceExamId);
        return new ResponseEntity(subjectMasterResponses, HttpStatus.OK);
    }
}
