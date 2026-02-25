package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.ThemeManagement;
import com.bezkoder.springjwt.payload.request.ThemeManagementRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ThemeManagementResponse;
import com.bezkoder.springjwt.services.ThemeManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theme")
@CrossOrigin(origins = "*")
public class ThemeManagementController {
    @Autowired
    private ThemeManagementService themeManagementService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody ThemeManagementRequest themeManagementRequest){
        MainResponse mainResponse = this.themeManagementService.create(themeManagementRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody ThemeManagementRequest themeManagementRequest){
        MainResponse mainResponse = this.themeManagementService.update(themeManagementRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<ThemeManagement> themeManagements = this.themeManagementService.getAll();
        return new ResponseEntity(themeManagements, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<ThemeManagement> themeManagements = this.themeManagementService.getAllActive();
        return new ResponseEntity(themeManagements, HttpStatus.OK);
    }

    @GetMapping("/getbyid/{themeId}")
    public ResponseEntity getById(@PathVariable("themeId") Integer themeId){
        ThemeManagement themeManagement = this.themeManagementService.getById(themeId);
        return new ResponseEntity(themeManagement, HttpStatus.OK);
    }

    @GetMapping("/useridwisetheme/{id}")
    public ResponseEntity userIdWiseTheme(@PathVariable("id") Long id){
        List<ThemeManagementResponse> themeManagementResponses = this.themeManagementService.userIdWiseTheme(id);
        return new ResponseEntity(themeManagementResponses, HttpStatus.OK);
    }
}
