package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.services.TestService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/teacherReport")
public class TeacherSideReportController {

    @Autowired
    private TestService testService;

    @GetMapping("/getTestReportByTeacher/{id}")
    public ResponseEntity<?> getTestReportByTeacher(@PathVariable("id") Long tid){
        try {
            List<AllTestReportByTeachId> report = testService.getTestReportByTeacher(tid);
            return new ResponseEntity<>(report,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/testReportByTeacherId/{testId}")
    public ResponseEntity<TestReportResponse> getTestReport(@PathVariable Integer testId) {
        TestReportResponse response = testService.getTestReport(testId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getTestDetailCount/{tid}")
    public ResponseEntity<?> getTestDetailCount(@PathVariable Long tid){
        try{
            TestDetailCountResponse response = testService.getTestDetailCount(tid);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (Apierrorr e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);

        }

    }

    @GetMapping("/monthly/{tId}")
    public ResponseEntity<List<MonthWiseReportDTO>> getMonthWiseReport(
            @PathVariable Long tId,
            @RequestParam String year) {

        List<MonthWiseReportDTO> report =
                testService.getMonthWiseReport(tId, year);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/studentPerformance/{id}")
    public ResponseEntity<?> getStudentPerformance(@PathVariable("id") Long studentId) {
        StudentPerformanceDTO response =
                testService.getStudentPerformance(studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-students")
    public ResponseEntity<List<StudentPerformanceDTO>> getAllStudents(
            @RequestParam Long teacherId) {

        List<StudentPerformanceDTO> result =
                testService.getAllStudentsPerformance(teacherId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getTeacherWiseTest/{id}")
    public ResponseEntity<?> getTeacherWiseTest(@PathVariable("id") Long teacherId){
        try{
            List<ExamResponse> responses = testService.getTeacherWiseTest(teacherId);
            return new ResponseEntity<>(responses,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }
}
