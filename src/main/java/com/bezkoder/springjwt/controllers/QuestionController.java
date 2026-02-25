package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.PatternMaster;
import com.bezkoder.springjwt.models.QuestionMaster;
import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.models.YearOfAppearance;
import com.bezkoder.springjwt.payload.request.DuplicateQuestionRequest;
import com.bezkoder.springjwt.payload.request.QuestionFilterDTO;
import com.bezkoder.springjwt.payload.request.QuestionRequest;
import com.bezkoder.springjwt.payload.request.ShuffleQuestionReq;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.QuestionResponse;
import com.bezkoder.springjwt.payload.response.QuestionResponse1;
import com.bezkoder.springjwt.payload.response.ShuffleQuestionsResponse;
import com.bezkoder.springjwt.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/question")
@CrossOrigin(origins = "*")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody QuestionRequest questionRequest) {
        MainResponse mainResponse = this.questionService.create(questionRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody QuestionRequest questionRequest) {
        MainResponse mainResponse = this.questionService.update(questionRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())) {
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/questionbyid1/{questionId}")
    public ResponseEntity questionbyid1(@PathVariable("questionId") Integer questionId) {
        QuestionResponse questionResponse = this.questionService.questionById1(questionId);
        return new ResponseEntity(questionResponse, HttpStatus.OK);
    }


    //    @GetMapping("/getall")
//    public ResponseEntity getAll(){
//        List<QuestionMaster> questionMasters = this.questionService.getAll();
//        return new ResponseEntity(questionMasters, HttpStatus.OK);
//    }
//
    @GetMapping("/getallactive")
    public ResponseEntity getAllActiveQuestions() {
        List<QuestionResponse> questionResponses = this.questionService.getAllActiveQuestions();
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    @GetMapping("/getAllByStatus/{status}")
    public ResponseEntity getAllByStatus(@PathVariable("status") String status) {
        List<QuestionResponse1> questionResponses = this.questionService.getAllByStatus(status);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }


    //
//
//    @GetMapping("/yearffappearancewisequestions/{yearOfAppearance}")
//    public ResponseEntity yearOfAppearanceWiseQuestions(@PathVariable("yearOfAppearance") String yearOfAppearance){
//        List<QuestionResponse> questionResponses = this.questionService.yearOfAppearanceWiseQuestions(yearOfAppearance);
//        return new ResponseEntity(questionResponses, HttpStatus.OK);
//    }
//
    @GetMapping("/allquestionscount")
    public ResponseEntity allQuestionsCount() {
        Integer count = this.questionService.allQuestionsCount();
        return new ResponseEntity(count, HttpStatus.OK);
    }
//
//    @GetMapping("/standardwiseallquestions/{standardId}")
//    public ResponseEntity standardWiseAllQuestions(@PathVariable("standardId") Integer standardId){
//        List<QuestionResponse> questionResponses = this.questionService.standardWiseAllQuestions(standardId);
//        return new ResponseEntity(questionResponses, HttpStatus.OK);
//    }
//
////    @GetMapping("/yearwisequestionbank/{year}/{entranceExamId}")
////    public ResponseEntity yearWiseQuestionBank(@PathVariable("year") String year,@PathVariable("entranceExamId") Integer entranceExamId){
////        List<QuestionResponse> questionResponses = this.questionService.yearWiseQuestionBank(year,entranceExamId);
////        return new ResponseEntity(questionResponses, HttpStatus.OK);
////    }

    @GetMapping("/getByUserId/{id}/{status}")
    public ResponseEntity getByUserIdAndStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        List<QuestionResponse1> questionResponse1s = this.questionService.getByUserIdAndStatus(id, status);
        return new ResponseEntity(questionResponse1s, HttpStatus.OK);
    }

    @GetMapping("/acceptorrejectquestion/{questionId}/{status}")
    public ResponseEntity acceptOrRejectQuestion(@PathVariable("questionId") Integer questionId, @PathVariable("status") String status) {
        MainResponse mainResponse = this.questionService.acceptOrRejectQuestion(questionId, status);
        if (Boolean.TRUE.equals(mainResponse.getFlag())) {
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{questionId}")
    public ResponseEntity delete(@PathVariable("questionId") Integer questionId) {
        MainResponse mainResponse = this.questionService.delete(questionId);
        if (Boolean.TRUE.equals(mainResponse.getFlag())) {
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // For delete all questions

    @DeleteMapping("/deleteAll")
    public ResponseEntity deleteAllQuestions() {
        MainResponse response = questionService.deleteAllQuestions();
        if (Boolean.TRUE.equals(response.getFlag())) {
            return new ResponseEntity(response, HttpStatus.OK);
        } else {
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }


    //    chapter wise question count
    @GetMapping("/chapterwisequestioncount/{chapterId}")
    public ResponseEntity chapterWiseQuestionCount(@PathVariable("chapterId") Integer chapterId) {
        Integer count = this.questionService.chapterWiseQuestionCount(chapterId);
        return new ResponseEntity(count, HttpStatus.OK);
    }

    //    subject wise question count
    @GetMapping("/subjectwisequestioncount/{subjectId}")
    public ResponseEntity subjectWiseQuestionCount(@PathVariable("subjectId") Integer subjectId) {
        Integer count = this.questionService.subjectWiseQuestionCount(subjectId);
        return new ResponseEntity(count, HttpStatus.OK);
    }

    //    entrance exam wise question count
    @GetMapping("/entranceexamwisequestioncount/{entranceExamId}")
    public ResponseEntity entranceExamWiseQuestionCount(@PathVariable("entranceExamId") Integer entranceExamId) {
        Integer count = this.questionService.entranceExamWiseQuestionCount(entranceExamId);
        return new ResponseEntity(count, HttpStatus.OK);
    }

    // Entrance Exam wise questions
    @GetMapping("/entranceexamwisequestions/{entranceExamId}")
    public ResponseEntity entranceExamWiseQuestions(@PathVariable("entranceExamId") Integer entranceExamId) {
        List<QuestionResponse> questionResponses = this.questionService.entranceExamWiseQuestions(entranceExamId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Chapter wise question paper
    @GetMapping("/chapterwisequestions/{chapterId}")
    public ResponseEntity chapterWiseQuestions(@PathVariable("chapterId") Integer chapterId) {
        List<QuestionResponse> questionResponses = this.questionService.chapterWiseQuestions(chapterId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Subject wise questions
    @GetMapping("/subjectwisequestions/{subjectId}")
    public ResponseEntity subjectWiseQuestions(@PathVariable("subjectId") Integer subjectId) {
        List<QuestionResponse> questionResponses = this.questionService.subjectWiseQuestions(subjectId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Topic wise questions
    @GetMapping("/topicwisequestions/{topicId}")
    public ResponseEntity topicWiseQuestions(@PathVariable("topicId") Integer topicId) {
        List<QuestionResponse> questionResponses = this.questionService.topicWiseQuestions(topicId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Sub topic wise questions
    @GetMapping("/subtopicwisequestions/{subTopicId}")
    public ResponseEntity subTopicWiseQuestions(@PathVariable("subTopicId") Integer subTopicId) {
        List<QuestionResponse> questionResponses = this.questionService.subTopicWiseQuestions(subTopicId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Question type wise questions
    @GetMapping("/questiontypewisequestion/{questionTypeId}")
    public ResponseEntity questionTypeWiseQuestions(@PathVariable("questionTypeId") Integer questionTypeId) {
        List<QuestionResponse> questionResponses = this.questionService.questionTypeWiseQuestions(questionTypeId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Question level wise questions
    @GetMapping("/questionlevelwisequestions/{questionLevelId}")
    public ResponseEntity questionLevelWiseQuestions(@PathVariable("questionLevelId") Integer questionLevelId) {
        List<QuestionResponse> questionResponses = this.questionService.questionLevelWiseQuestions(questionLevelId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Pattern wise questions
    @GetMapping("/patternwisequestions/{patternId}")
    public ResponseEntity patternWiseQuestion(@PathVariable("patternId") Integer patternId) {
        List<QuestionResponse> questionResponses = this.questionService.patternWiseQuestion(patternId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Year of appearance wise questions
    @GetMapping("/yearofappearancewisequestions/{yearOfAppearanceId}")
    public ResponseEntity yearOfAppearanceWiseQuestions(@PathVariable("yearOfAppearanceId") Integer yearOfAppearanceId) {
        List<QuestionResponse> questionResponses = this.questionService.yearOfAppearanceWiseQuestions(yearOfAppearanceId);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // Question category wise questions
    @GetMapping("/questioncategorywisequestions/{questionCategory}")
    public ResponseEntity questionCategoryWiseQuestions(@PathVariable("questionCategory") Integer questionCategory) {
        List<QuestionResponse> questionResponses = this.questionService.questionCategoryWiseQuestions(questionCategory);
        return new ResponseEntity(questionResponses, HttpStatus.OK);
    }

    // find duplicate questions
    @GetMapping("/duplicatequestions")
    public ResponseEntity findDuplicateQuestions(@RequestParam("question") String question){
        MainResponse mainResponse = this.questionService.findDuplicateQuestions(question);
        return new ResponseEntity(mainResponse, HttpStatus.OK);
    }

    @GetMapping("/ask")
    public ResponseEntity asked(){
        List list=questionService.asked();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    // Entrance and standard wise questions
    @GetMapping("/entranceandstandardwisequestion/{entranceExamId}/{standardId}")
    public ResponseEntity entranceAndStandardWiseQuestions(@PathVariable("entranceExamId") Integer entranceExamId,@PathVariable("standardId") Integer standardId){
        List<QuestionMaster> questionResponseList = this.questionService.entranceAndStandardWiseQuestions(entranceExamId,standardId);
        return new ResponseEntity(questionResponseList, HttpStatus.OK);
    }

    @PostMapping("/shuffleQuestions")
    public ResponseEntity<List<ShuffleQuestionsResponse>> sufflequestions(@RequestBody ShuffleQuestionReq shuffleQuestionReq)
    {
        List<ShuffleQuestionsResponse> response = questionService.shuffleQuestions(shuffleQuestionReq);
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/search")
//    public List<QuestionResponse1> searchQuestions(
//            QuestionFilterDTO filterDTO,
//            @RequestParam(defaultValue = "5") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return questionService.getQuestionsByFilter(filterDTO, page, size);
//    }
@GetMapping("/search")
public ResponseEntity<Map<String, Object>> searchQuestions(
        QuestionFilterDTO filterDTO,
        @RequestParam(defaultValue = "0") int page,  // Change default to 0 for zero-based pagination
        @RequestParam(defaultValue = "10") int size
) {
    Page<QuestionResponse1> questionPage = (Page<QuestionResponse1>) questionService.getQuestionsByFilter(filterDTO, page, size);

    Map<String, Object> response = new HashMap<>();
    response.put("questions", questionPage.getContent());
    response.put("currentPage", questionPage.getNumber());
    response.put("totalPages", questionPage.getTotalPages());
    response.put("totalItems", questionPage.getTotalElements());

    return ResponseEntity.ok(response);
}


//    @GetMapping("/unused/{teacherId}")
//    public ResponseEntity<List<QuestionMaster>> getUnusedQuestions(@PathVariable("teacherId") Long teacherId){
//        List<QuestionMaster> unusedQuestions = questionService.getUnusedQuestionsForTeacher(teacherId);
//        return new ResponseEntity<>(unusedQuestions,HttpStatus.OK);
//    }


}
