package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.InstituteMaster;
import com.bezkoder.springjwt.payload.request.InstituteRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/institute")
public class InstituteController {
    @Autowired
    private InstituteService instituteService;

    @PostMapping("/create")
    public ResponseEntity createInstitute(@RequestBody InstituteRequest instituteRequest){
        MainResponse mainResponse = this.instituteService.createInstitute(instituteRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateInstitute(@RequestBody InstituteRequest instituteRequest){
        MainResponse mainResponse = this.instituteService.updateInstitute(instituteRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{instituteId}")
    public ResponseEntity getById(@PathVariable("instituteId") Integer instituteId){
        InstituteMaster instituteMaster = this.instituteService.getById(instituteId);
        return new ResponseEntity(instituteMaster, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<InstituteMaster> instituteMasters = this.instituteService.getAll();
        return new ResponseEntity(instituteMasters, HttpStatus.OK);
    }

    @GetMapping("/getallactives")
    public ResponseEntity getAllActive(){
        List<InstituteMaster> instituteMasters = this.instituteService.getAllActive();
        return new ResponseEntity(instituteMasters, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MainResponse> deleteInstitute(@PathVariable Integer id) {
        MainResponse mainResponse = instituteService.deleteInstitute(id);
        if (Boolean.TRUE.equals(mainResponse.getFlag())) {
            return new ResponseEntity<>(mainResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }


}
