package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.dto.PDFCrunchReq;
import com.bezkoder.springjwt.dto.PDFCrunchRes;
import com.bezkoder.springjwt.dto.PdfRequestDTO;
import com.bezkoder.springjwt.dto.QuestionDTO;
import com.bezkoder.springjwt.models.TestMaster;
import com.bezkoder.springjwt.payload.request.*;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.services.TestService;
import com.bezkoder.springjwt.services.pdfGenerated.PdfGenerationService;
import com.bezkoder.springjwt.utils.PdfClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private TestService testService;




    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TestRequest testRequest){
        MainResponse mainResponse = this.testService.create(testRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }


    ///  craete Test with Muiltiple Questions
    @PostMapping("/createD")
    public ResponseEntity createD(@RequestBody TestRequestD testRequestD){
        MainResponse mainResponse = this.testService.createD(testRequestD);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    //test creation api for teacher
    @PostMapping("/generateTest")
    public ResponseEntity<PaginatedResponse<TestQuestionsResponse>> generate(@RequestBody @Valid QuestionFilterRequest r){
        return ResponseEntity.ok(testService.createTestFromTeacherSide(r));
    }

    @PostMapping("/generateTest/weightageWiseTest")
    public ResponseEntity<PaginatedResponse<TestQuestionsResponse>> generateWeightageWiseTest(@RequestBody @Valid QuestionFilterRequest r){
        return ResponseEntity.ok(testService.createWeightageWiseTestFromTeacherSide(r));
    }


    @PostMapping("/createSets")
    public ResponseEntity createSets(@RequestBody TestRequestSetWise testRequestSetWise){
        MainResponse mainResponse = this.testService.createQuestionSets(testRequestSetWise);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}/used-questions")
    public ResponseEntity<Map<String, String>> reset(@PathVariable("id") Long teacherId) {
        testService.resetUsed(teacherId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Reset unused questions successfully");

        return ResponseEntity.ok(response);
    }


    @PutMapping("/update")
    public ResponseEntity update(@RequestBody TestRequest testRequest){
        MainResponse mainResponse = this.testService.update(testRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getbyid/{testId}")
    public ResponseEntity getById(@PathVariable("testId") Integer testId){
        TestMaster testMaster = this.testService.getById(testId);
        return new ResponseEntity(testMaster, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity getAll(){
        List<TestMaster> testMasters = this.testService.getAll();
        return new ResponseEntity(testMasters, HttpStatus.OK);
    }

    @GetMapping("/getofflinetests/{id}")
    public ResponseEntity<List<TestOfflineResponse>> getAllOfflineTests(@PathVariable("id") Long teacherId){
             List<TestOfflineResponse> response= testService.getAllOfflineTests(teacherId);
              return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/getOfflineTestById/{id}")
    public ResponseEntity<List<TestOfflineResponse>> getOfflineTestById(@PathVariable("id") Integer testId){
        List<TestOfflineResponse> response= testService.getOfflineTestById(testId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/getOfflineById/{id}")
    public ResponseEntity<List<AllOfflineTestByIdResponse>> getOfflineTById(@PathVariable("id") Long teacherId){
        List<AllOfflineTestByIdResponse> response= testService.getOfflineTById(teacherId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

        @PostMapping("/saveOfflineTest")
        public ResponseEntity<?> saveTestResult(@RequestBody List<TestOfflineSubRequest> request) {
            testService.saveTestResult(request);
            return new ResponseEntity<>(new MessageResponse("Test result saved successfully"), HttpStatus.CREATED);
        }

    @GetMapping("/getallactivetests")
    public ResponseEntity getAllUpcomingTests(){
        List<TestMaster> testMasters = this.testService.getAllUpcomingTests();
        return new ResponseEntity(testMasters, HttpStatus.OK);
    }

    @GetMapping("/statuswisetests/{status}")
    public ResponseEntity statusWiseTests(@PathVariable("status") String status){
        List<TestMaster> testMasters = this.testService.statusWiseTests(status);
        return new ResponseEntity(testMasters, HttpStatus.OK);
    }

    //  Get questions by entranceExamId,standardId,subjectId,chapterId,topicId,subTopicId,yearOfAppearanceId,questionLevelId,questionTypeId,patternId
    @PostMapping("/getquestions")
    public ResponseEntity getQuestions(@RequestBody TestQuestionRequest testQuestionRequest){
        List<TestQuestionsResponse> testQuestionsResponse = this.testService.getQuestions(testQuestionRequest);
        return new ResponseEntity(testQuestionsResponse, HttpStatus.OK);
    }

    @PostMapping("/getrandomquestions")
    public ResponseEntity getRandomQuestions(@RequestBody TestQuestionRequest testQuestionRequest){
        List<TestQuestionsResponse> testQuestionsResponse = this.testService.getRandomQuestions(testQuestionRequest);
        return new ResponseEntity(testQuestionsResponse, HttpStatus.OK);
    }

    //  Filter wise questions count
    @PostMapping("/filterwisequestionscount")
    public ResponseEntity filterWiseQuestionsCount(@RequestBody TestQuestionRequest testQuestionRequest){
        Integer count = this.testService.filterWiseQuestionsCount(testQuestionRequest);
        return new ResponseEntity(count, HttpStatus.OK);
    }

    @GetMapping("/testidwisequestions/{testId}")
    public ResponseEntity testIdWiseQuestions(@PathVariable("testId") Integer testId){
        TestQuestionResponse testQuestionResponse = this.testService.testIdWiseQuestions(testId);
        return new ResponseEntity(testQuestionResponse,HttpStatus.OK);
    }

    //   Entrance exam and standard id wise test
    @GetMapping("/entranceandstandardidwiseupcomingtest/{entranceExamId}/{standardId}")
    public ResponseEntity entranceAndStandardIdWiseUpComingTest(@PathVariable("entranceExamId")Integer entranceExamId,@PathVariable("standardId")Integer standardId){
        List<TestMasterResponse> testMasterResponse = this.testService.entranceAndStandardIdWiseUpComingTest(entranceExamId,standardId);
        return new ResponseEntity(testMasterResponse, HttpStatus.OK);
    }

    @PostMapping("/weightagewisetestcreation")
    public ResponseEntity weightageWiseTestCreation(@RequestBody TestQuestionRequest testQuestionRequest){
        MainResponse mainResponse = this.testService.weightageWiseTestCreation(testQuestionRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }
    // Student id wise their entrance, standard, subject wise chapter weightage

    @GetMapping("/chapterweightagebystudent/{id}/{entranceExamId}")
    public ResponseEntity chapterWeightageByStudent(@PathVariable("id") Long id,@PathVariable("entranceExamId") Integer entranceExamId){
        List<StudentChapterWeightageResponse> studentChapterWeightageResponses = this.testService.chapterWeightageByStudent(id,entranceExamId);
        return new ResponseEntity(studentChapterWeightageResponses, HttpStatus.OK);
    }

    // Student questions filter for test creation
    @PostMapping("/studentfilter")
    public ResponseEntity studentFilter(@RequestBody TestQuestionRequest testQuestionRequest){

        List<TestQuestionsResponse> testQuestionsResponses = this.testService.studentFilter(testQuestionRequest);
        return new ResponseEntity(testQuestionsResponses, HttpStatus.OK);
    }

    // Create Test from student side
    @PostMapping("/createstudenttest")
    public ResponseEntity createTestFromStudentSide(@RequestBody CreateTestFromStudent createTestFromStudent)
    {
        MainResponse mainResponse = this.testService.createTestFromStudentSide(createTestFromStudent);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }




    //test created by student now get test question to attempt test
    @GetMapping("/getTestQuestionsForStudent")
    public ResponseEntity<?> getTestQuestionsForStudent(@RequestParam("userId") Long userId,
                                                        @RequestParam("testId") Integer testId) {
        try {
            TestQuestionsStudentResponse distinctQuestions = testService.getTestQuestionsForStudent(userId, testId);
            return ResponseEntity.ok(distinctQuestions);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while fetching the questions.");
        }
    }

    //test created by teacher now get test question to attempt test
    @GetMapping("/getTestQuestionsFromTeacher")
    public ResponseEntity<?> getTestQuestionsFromTeacher(@RequestParam("testId") Integer testId) {
        try {
            TestQuestionsStudentResponse distinctQuestions = testService.getTestQuestionsFromTeacher( testId);
            return ResponseEntity.ok(distinctQuestions);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while fetching the questions.");
        }
    }

    @GetMapping("/getUserIdWiseTests/{id}")
    private ResponseEntity<TestMaster> getUserIdWiseTests(@PathVariable("id") Long id )
    {
        List<TestResponse> testResponseList=new ArrayList<>();
        testResponseList=testService.getUserIdWiseTests(id);
        return new ResponseEntity(testResponseList, HttpStatus.OK);
    }

    @GetMapping("/getStudentIdWiseTests/{id}")
    private ResponseEntity<TestMaster> getStudentIdWiseTests(@PathVariable("id") Long id)
    {
        List<TestResponse> testResponseList=new ArrayList<>();
        testResponseList=testService.getStudentIdWiseTests(id);
        return new ResponseEntity(testResponseList,HttpStatus.OK);
    }

    @GetMapping("/getTestCreatedByStudent/{id}")
    private ResponseEntity<List<TestResponse>> getTestCreatedByStudent(@PathVariable("id") Long id) {
        List<TestResponse> testResponseList = testService.getTestCreatedByStudent(id);
        return new ResponseEntity<>(testResponseList, HttpStatus.OK);
    }


    @PostMapping("/submit")
    public ResponseEntity<?> submitTest(@RequestBody TestSubmissionRequest request) {
        TestSubmitResponse response = testService.saveTestSubmission(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getSubmittedTest")
    public ResponseEntity<TestResultResponse> getSubmittedTest(@RequestParam("id") Long studentId,@RequestParam("testId") Integer testId ) {
        TestResultResponse savedSubmission = testService.getSubmittedTest(studentId,testId);
        return ResponseEntity.ok(savedSubmission);
    }

    @GetMapping("/getAllTestResult/{id}")
    public ResponseEntity<List<ReportForStudentTestResponse>> getAllTestResult(@PathVariable("id") Long studentId){
        List<ReportForStudentTestResponse> response = testService.getResultForAllTest(studentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getAllOfflineTestResult/{id}")
    public ResponseEntity<List<ReportOfflineTestResponse>> getAllOfflineTestResult(@PathVariable("id") Long studentId){
        List<ReportOfflineTestResponse> response = testService.getAllOfflineTestResult(studentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/mode/{testMode}/{id}")
    public ResponseEntity<Page<ModeWiseTestResponse>> getTestsByMode(
            @PathVariable("id") Long createdById,
            @PathVariable String testMode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ModeWiseTestResponse> response = testService.getModeWiseTests(createdById,testMode, page, size);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody List<QuestionDTO> questionList) {
        byte[] pdfBytes = testService.generatePdf(questionList);
        if (pdfBytes == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=questions.pdf");
        headers.add("Content-Type", "application/pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @Autowired
    PdfGenerationService pdfGenerationService;
//    @PostMapping("/pdfGenerateTwoColumn")
//    public ResponseEntity<byte[]> pdfGenerateTwoColumn(@RequestBody List<QuestionDTO> questionList) {
//        byte[] pdfBytes = pdfGenerationService.pdfGenerateTwoColumn(questionList);
//        if (pdfBytes == null) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "attachment; filename=questionsTwoColumn.pdf");
//        headers.add("Content-Type", "application/pdf");
//        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//    }

    @PostMapping("/pdf/generate")
    public ResponseEntity<byte[]> generatePDF(@RequestBody PdfRequestDTO dto) {
        byte[] pdf = pdfGenerationService.buildPaper(dto);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=paper.pdf")
                .body(pdf);
    }





    @GetMapping("/getImportantQuestions/{tid}/{sid}")
    public ResponseEntity<?> getImportantQuestions(@PathVariable("tid") Integer tid,
                                                   @PathVariable("sid") Long sid) {
        try {
            TestQuestionResponse response = testService.testIdWiseImportantQuestions(tid, sid);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/important-questions/{studentId}")
    public ResponseEntity<?> getImportantQuestionsByStudent(
            @PathVariable("studentId") Long studentId) {

        try {
            List<ImportantQuestionDTO> list =
                    testService.findImportantQuestionsByStudent(studentId);

            return ResponseEntity.ok(list);      // 200 + JSON array
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/getTeacherReport/{tid}")
    public ResponseEntity<?> getTeacherReport(@PathVariable("tid") Long tid){
//        try {
//            StudentPassRetReport response = testService.getTeacherReport(tid);
//
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred: " + e.getMessage());
//        }
        StudentPassRetReport response = testService.getTeacherReport(tid);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming-events/{id}")
    public ResponseEntity<List<UpcomingEventDTO>> upcomingEvents(
            @PathVariable("id") Long parentId) {

        List<UpcomingEventDTO> events = testService.getUpcomingEvents(parentId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/recent-activities/{parentId}")
    public ResponseEntity<List<RecentActivityDTO>> recentActivities(
            @PathVariable Long parentId) {

        List<RecentActivityDTO> list = testService.getRecentActivities(parentId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/count/{requesterId}")
    public ResponseEntity<TestCountResponse> getCounts(@PathVariable Long requesterId) {
        return ResponseEntity.ok(testService.getCounts(requesterId));
    }





//      ---------------------------------------------------------
    // TEST CREATION FOR STUDENTS
    // CHAPTER WISE TEST
//    public ResponseEntity studentChapterWiseTest(@RequestBody )

    // Get all test details like questions, entrance, standard, subject, chapters, topics, subtopics
//    @GetMapping("/testallinformation/{testI}")d
//    public ResponseEntity testAllInformation(@PathVariable("testId") Integer testId){
//      TestAllInformationResponse testAllInformationResponse = this.
//    }
}
