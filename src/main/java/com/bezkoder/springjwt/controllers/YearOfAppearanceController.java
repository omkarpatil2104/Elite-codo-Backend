package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.YearOfAppearance;
import com.bezkoder.springjwt.payload.request.YearOfAppearanceRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.YearOfAppearanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/yearofappearance")
@CrossOrigin(origins = "*")
public class YearOfAppearanceController {
    @Autowired
    private YearOfAppearanceService yearOfAppearanceService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody YearOfAppearanceRequest yearOfAppearanceRequest){
        MainResponse mainResponse = this.yearOfAppearanceService.create(yearOfAppearanceRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody YearOfAppearanceRequest yearOfAppearanceRequest){
        MainResponse mainResponse = this.yearOfAppearanceService.update(yearOfAppearanceRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{yearOfAppearanceId}")
    public ResponseEntity getById(@PathVariable("yearOfAppearanceId") Integer yearOfAppearanceId){
        YearOfAppearance yearOfAppearance = this.yearOfAppearanceService.getById(yearOfAppearanceId);
        return new ResponseEntity(yearOfAppearance, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<YearOfAppearance> yearOfAppearances = this.yearOfAppearanceService.getAll();
        return new ResponseEntity(yearOfAppearances, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<YearOfAppearance> yearOfAppearances = this.yearOfAppearanceService.getAllActive();
        return new ResponseEntity(yearOfAppearances, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{yearOfAppearanceId}")
    public ResponseEntity delete(@PathVariable("yearOfAppearanceId") Integer yearOfAppearanceId){
        MainResponse mainResponse = this.yearOfAppearanceService.delete(yearOfAppearanceId);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
