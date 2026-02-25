package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.ChapterMaster;
import com.bezkoder.springjwt.payload.request.ChapterRequest;
import com.bezkoder.springjwt.payload.request.ChapterWiseWeightageRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapter")
@CrossOrigin(origins = "*")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody ChapterRequest chapterRequest){
        MainResponse mainResponse = this.chapterService.create(chapterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody ChapterRequest chapterRequest){
        MainResponse mainResponse = this.chapterService.update(chapterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getchapterbyid/{chapterId}")
    public ResponseEntity getChapterById(@PathVariable("chapterId") Integer chapterId){
        ChapterMaster chapterMaster = this.chapterService.getChapterById(chapterId);
        return new ResponseEntity(chapterMaster, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<ChapterMaster> chapterMasters = this.chapterService.getAll();
        return new ResponseEntity(chapterMasters, HttpStatus.OK);
    }

    @GetMapping("/getallactive")
    public ResponseEntity getAllActiveChapters(){
        List<ChapterMaster> chapterMasters = this.chapterService.getAllActiveChapters();
        return new ResponseEntity(chapterMasters, HttpStatus.OK);
    }

    @GetMapping("/subjectwisechapter/{subjectId}")
    public ResponseEntity subjectWiseChapter(@PathVariable("subjectId") Integer subjectId){
        System.out.println("Sub wise chapte = r"+subjectId);
        List<ChapterMaster> chapterMasters = this.chapterService.subjectWiseChapter(subjectId);
        return new ResponseEntity(chapterMasters, HttpStatus.OK);
    }
    //standard and subject id wise chapter
    @GetMapping("/subjectwisechapter/{isactive}/{subjectId}/{standardId}")
    public ResponseEntity subjectWiseNewChapter(@PathVariable("subjectId") Integer subjectId,
                                             @PathVariable("isactive") String isactive ,
                                             @PathVariable("standardId") Integer standardId){

        System.out.println("Sub wise chapte = r"+subjectId);
        List<ChapterMaster> chapterMasters = this.chapterService.subjectStandWiseChapter(subjectId,isactive,standardId);
        return new ResponseEntity(chapterMasters, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{chapterId}")
    public ResponseEntity delete(@PathVariable("chapterId") Integer chapterId){
        MainResponse mainResponse = this.chapterService.delete(chapterId);
        return new ResponseEntity(mainResponse, HttpStatus.OK);
    }

    @PostMapping("/chapterwiseweightage")
    public ResponseEntity chapterWiseWeightage(@RequestBody ChapterWiseWeightageRequest chapterWiseWeightageRequest){
        MainResponse mainResponse = this.chapterService.chapterWiseWeightage(chapterWiseWeightageRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
