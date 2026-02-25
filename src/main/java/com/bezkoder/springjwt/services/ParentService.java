package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.payload.response.*;

import java.util.List;

public interface ParentService {

     double calculateAttendancePercentage(Long studentId);

     double calculateOfflineAttendancePercentage(Long studentId);

     double calculatePerformancePercentage(Long studentId);

      StudentSubReportResponse generateReport(Long parentId);

    List<RecentTestReportResponse> getRecentTestReports(Long studentId);

    List<SubjectPerformanceResponse> getSubjectPerformance(Long studentId);

    StudentOverallPerformanceResponse getStudentOverallPerformance(Long parentId);

    MockSummaryDTO getMockSummary(Long parentId);
}
