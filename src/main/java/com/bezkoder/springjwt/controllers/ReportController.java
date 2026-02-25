package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.services.EntranceExamService;
import com.bezkoder.springjwt.services.ReportService;
import com.bezkoder.springjwt.services.TestService;
import com.bezkoder.springjwt.services.impl.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private EntranceExamService entranceExamService;

    @Autowired
    private TestService testService;

    @Autowired
    private ReportService reportService;

    @GetMapping("/exams-report")
    public ResponseEntity<?> getEntranceExamReport(){

        List<ReportResponse> reportResponse = entranceExamService.getEntranceExamReport();

        return new ResponseEntity<>(reportResponse, HttpStatus.OK);
    }

    @GetMapping("/subjects")
    public List<?> getSubjectsByExam(@RequestParam("examId") Integer examId) {
        return entranceExamService.getSubjectsReport(examId);
    }

    @GetMapping("/chapters")
    public List<ChapterReportResponse> getChaptersBySubject(@RequestParam("subjectId") Integer subjectId) {
        return entranceExamService.getChaptersReport(subjectId);
    }

    @GetMapping("/teachers/{id}")
    public List<TeacherReportResponse> getTeacherReport(@PathVariable("id") Long instituteId){
        List<TeacherReportResponse> reportResponses = reportService.getTeachersReportByIntitute(instituteId);
        return reportResponses;
    }

    @GetMapping("/teacher/{teacherId}/performance")
    public ResponseEntity<?>
    getTeacherPerformance(@PathVariable Long teacherId) {

        List<StudentPerfResponse> resp = reportService.getTeacherPerformance(teacherId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/teacher/{teacherId}/tests")
    public ResponseEntity<?> getTestsByMonth(
            @PathVariable Long teacherId) {

        List<TestGroupResponse> resp = reportService.getTeacherTests(teacherId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/tests")
    public ResponseEntity<List<TestSummaryResponse>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTestSummaries());
    }

    @GetMapping("/test/{testId}/performance")
    public ResponseEntity<?> getTestPerformance(@PathVariable Integer testId) {
        return ResponseEntity.ok(reportService.getTestPerformance(testId));
    }

    @GetMapping("/test/{testId}/score-distribution")
    public ResponseEntity<?> getScoreDistribution(
            @PathVariable Integer testId) {

        List<ScoreRangeDTO> resp = reportService.getScoreDistribution(testId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentSummaryResponse>> getAllStudents(
            @RequestParam Long instituteId) {

        List<StudentSummaryResponse> resp =
                reportService.getAllStudents(instituteId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/student/{studentId}/progress")
    public ResponseEntity<?> getProgress(
            @PathVariable Long studentId) {

        List<TestProgressDTO > resp = reportService.getStudentProgress(studentId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/student/{studentId}/subjects")
    public ResponseEntity<?> getStudentSubjects(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                reportService.getStudentSubjects(studentId)
        );
    }


}
