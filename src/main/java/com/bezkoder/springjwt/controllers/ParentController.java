package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.services.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parent")
@CrossOrigin("*")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @GetMapping("/percentage/{studentId}")
    public ResponseEntity<Double> getAttendancePercentage(@PathVariable Long studentId) {
        double attendancePercentage = parentService.calculateAttendancePercentage(studentId);
        return ResponseEntity.ok(attendancePercentage);
    }

    @GetMapping("/performance/{studentId}")
    public ResponseEntity<Double> getPerformancePercentage(@PathVariable Long studentId) {
        double performancePercentage = parentService.calculatePerformancePercentage(studentId);
        return ResponseEntity.ok(performancePercentage);
    }

    @GetMapping("/getSubjectWiseReport/{id}")
    public ResponseEntity<?> getSubjectWiseReport(@PathVariable("id") Long parentId){
        StudentSubReportResponse studentSubReportResponse = parentService.generateReport(parentId);
        return new ResponseEntity<>(studentSubReportResponse,HttpStatus.OK);
    }

    @GetMapping("/recent-tests")
    public ResponseEntity<List<RecentTestReportResponse>> getRecentTests(
            @RequestParam Long parentId) {

        List<RecentTestReportResponse> result =
                parentService.getRecentTestReports(parentId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getSubjectPerformance/{id}")
    public ResponseEntity<?> getSubjectPerformance(@PathVariable Long id){
        List<SubjectPerformanceResponse> response = parentService.getSubjectPerformance(id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/{parentId}/performance")
    public ResponseEntity<StudentOverallPerformanceResponse>
    getOverallPerformance(@PathVariable Long parentId) {

        StudentOverallPerformanceResponse resp = parentService.getStudentOverallPerformance(parentId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/mock-summary/{parentId}")
    public ResponseEntity<MockSummaryDTO> mockSummary(@PathVariable Long parentId) {
        MockSummaryDTO dto = parentService.getMockSummary(parentId);
        return ResponseEntity.ok(dto);
    }

}
