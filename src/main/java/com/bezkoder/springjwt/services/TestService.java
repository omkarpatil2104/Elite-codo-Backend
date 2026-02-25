package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.dto.*;
import com.bezkoder.springjwt.models.TestMaster;
import com.bezkoder.springjwt.payload.request.*;
import com.bezkoder.springjwt.payload.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TestService {
    MainResponse create(TestRequest testRequest);

    MainResponse update(TestRequest testRequest);

    TestMaster getById(Integer testId);

    List<TestMaster> getAll();

    List<TestMaster> getAllUpcomingTests();

    List<TestMaster> statusWiseTests(String status);

    List<TestQuestionsResponse> getQuestions(TestQuestionRequest testQuestionRequest);

    TestQuestionResponse testIdWiseQuestions(Integer testId);

    TestQuestionResponse testIdWiseImportantQuestions(Integer testId,Long studentId);

    List<TestQuestionsResponse> getRandomQuestions(TestQuestionRequest testQuestionRequest);

    List<TestMasterResponse> entranceAndStandardIdWiseUpComingTest(Integer entranceExamId, Integer standardId);

    Integer filterWiseQuestionsCount(TestQuestionRequest testQuestionRequest);

    MainResponse weightageWiseTestCreation(TestQuestionRequest testQuestionRequest);

    List<StudentChapterWeightageResponse> chapterWeightageByStudent(Long id, Integer entranceExamId);

    List<TestQuestionsResponse> studentFilter(TestQuestionRequest testQuestionRequest);

    MainResponse createTestFromStudentSide(CreateTestFromStudent createTestFromStudent);

    List<TestResponse> getUserIdWiseTests(Long id);

    List<TestResponse> getStudentIdWiseTests(Long id);

    List<TestResponse> getTestCreatedByStudent(Long id);

    MainResponse createD(TestRequestD testRequestD);

    byte[] generatePdf(List<QuestionDTO> jsonInput);

    MainResponse createQuestionSets(TestRequestSetWise testRequestSetWise);


    TestQuestionsStudentResponse getTestQuestionsForStudent(Long userId, Integer testId);

    TestSubmitResponse saveTestSubmission(TestSubmissionRequest request);

    List<ReportForStudentTestResponse> getResultForAllTest(Long studentId);

    TestQuestionsStudentResponse getTestQuestionsFromTeacher(Integer testId);

    TestResultResponse getSubmittedTest(Long studentId, Integer testId);

    public Page<ModeWiseTestResponse> getModeWiseTests(Long createdById, String testMode, int page, int size);

    List<TestOfflineResponse> getAllOfflineTests(Long teacherId);

    List<TestOfflineResponse> getOfflineTestById(Integer testId);

    void saveTestResult(List<TestOfflineSubRequest> request);

    List<AllOfflineTestByIdResponse> getOfflineTById(Long testId);

    List<ReportOfflineTestResponse> getAllOfflineTestResult(Long studentId);

    StudentPassRetReport getTeacherReport(Long tid);

    List<AllTestReportByTeachId> getTestReportByTeacher(Long tid);

    TestReportResponse getTestReport(Integer testId);

    TestDetailCountResponse getTestDetailCount(Long tid);

    List<MonthWiseReportDTO> getMonthWiseReport(Long teacherId, String year);

    StudentPerformanceDTO getStudentPerformance(Long studentId);

    List<StudentPerformanceDTO> getAllStudentsPerformance(Long teacherId);

    List<ExamResponse> getTeacherWiseTest(Long teacherId);

    List<TestSummaryResponse> getAllTestSummaries();

    List<UpcomingEventDTO> getUpcomingEvents(Long parentId);

    List<RecentActivityDTO> getRecentActivities(Long parentId);

    TestCountResponse getCounts(Long requesterId);

     PaginatedResponse<TestQuestionsResponse> createTestFromTeacherSide(QuestionFilterRequest req);

    void resetUsed(Long teacherId);

    PaginatedResponse<TestQuestionsResponse> createWeightageWiseTestFromTeacherSide(QuestionFilterRequest r);

    ExamResponseDTO createExam(ExamRequestDTO request);

    PDFCrunchRes createPDF(PDFCrunchReq request);

    AnswerKeyRes createAnswerKey(AnswerKeyReq answerKeyReq) throws JsonProcessingException;

    PDFChrunchAnsKeyRes createAnswerKeyCh(PDFChrunchAnsKeyReq answerKeyReq) throws JsonProcessingException;

    List<ImportantQuestionDTO> findImportantQuestionsByStudent(Long studentId);
}
