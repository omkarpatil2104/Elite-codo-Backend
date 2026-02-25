package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.PackageMaster;
import com.bezkoder.springjwt.payload.request.PackageMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.PackageMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/package")
@CrossOrigin(origins = "*")
public class PackageMasterController {
    @Autowired
    private PackageMasterService packageMasterService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody PackageMasterRequest packageMasterRequest){
        MainResponse mainResponse = this.packageMasterService.create(packageMasterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody PackageMasterRequest packageMasterRequest){
        MainResponse mainResponse = this.packageMasterService.update(packageMasterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{packageId}")
    public ResponseEntity getById(@PathVariable("packageId") Integer packageId){
        PackageMaster packageMaster = this.packageMasterService.getById(packageId);
        return new ResponseEntity(packageMaster, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<PackageMaster> packageMasters = this.packageMasterService.getAll();
        return new ResponseEntity(packageMasters, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive(){
        List<PackageMaster> packageMasters = this.packageMasterService.getAllActive();
        return new ResponseEntity(packageMasters, HttpStatus.OK);
    }
}
