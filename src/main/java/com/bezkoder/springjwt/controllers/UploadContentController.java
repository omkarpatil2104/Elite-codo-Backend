package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.UploadContentMaster;
import com.bezkoder.springjwt.payload.request.UploadContentRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.UploadContentMappedResponse;
import com.bezkoder.springjwt.payload.response.UploadContentMasterResponses;
import com.bezkoder.springjwt.payload.response.UploadContentQueRes;
import com.bezkoder.springjwt.services.UploadContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/uploadcontent")
public class UploadContentController {
    @Autowired
    private UploadContentService uploadContentService;

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestBody UploadContentRequest uploadContentRequest){
        MainResponse mainResponse = this.uploadContentService.upload(uploadContentRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody UploadContentRequest uploadContentRequest)
    {
        MainResponse mainResponse = this.uploadContentService.update(uploadContentRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{uploadContentId}")
    public ResponseEntity getById(@PathVariable("uploadContentId") Integer uploadContentId)
    {
        UploadContentMaster uploadContentMaster = this.uploadContentService.getById(uploadContentId);
        return new ResponseEntity(uploadContentMaster, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll()
    {
        List<UploadContentMasterResponses> uploadContentMasters = this.uploadContentService.getAll();
        return new ResponseEntity(uploadContentMasters, HttpStatus.OK);
    }

    @GetMapping("/getAllById/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long uploaderId){
        List<UploadContentMasterResponses> response = this.uploadContentService.getAllById(uploaderId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActive()
    {
        List<UploadContentMaster> uploadContentMasters = this.uploadContentService.getAllActive();
        return new ResponseEntity(uploadContentMasters, HttpStatus.OK);
    }

    @GetMapping("/allmappeduploadcontents/{entranceExamId}/{standardId}/{id}")
    public ResponseEntity allMappedUploadContents(@PathVariable("entranceExamId") Integer entranceExamId,@PathVariable("standardId") Integer standardId,@PathVariable("id") Long id)

    {
        UploadContentMappedResponse uploadContentMappedResponse = this.uploadContentService.allMappedUploadContents(entranceExamId,standardId,id);
        return new ResponseEntity(uploadContentMappedResponse, HttpStatus.OK);
    }


    @GetMapping("/getAllBystudentId/{id}")
    public ResponseEntity<?> getAllBystudentIdAndType(@PathVariable("id") Long studId){
        try{
            List<UploadContentQueRes> uploadContentQueRes = uploadContentService.getAllBystudentIdAndType(studId);
            return new ResponseEntity<>(uploadContentQueRes,HttpStatus.OK);
        }catch (Exception e){
            throw new RuntimeException("Somthing went wrong"+e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUploadContent(@PathVariable("id") Integer id){
        try {
            uploadContentService.deleteUploadContentById(id);
            MainResponse response = new MainResponse(
                    "UploadContent deleted successfully",
                    HttpStatus.OK.value(),
                    true
            );
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            MainResponse errorResponse = new MainResponse(
                    e.getMessage(),
                    HttpStatus.NOT_FOUND.value(),
                    false
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
