package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.payload.response.*;

import java.util.List;

    public interface ReportService {
    List<TeacherReportResponse> getTeachersReportByIntitute(Long instituteId);

    List<StudentPerfResponse> getTeacherPerformance(Long teacherId);

    List<TestGroupResponse> getTeacherTests(Long teacherId);

    List<QuestionPerformanceResponse> getTestPerformance(Integer testId);

    List<ScoreRangeDTO> getScoreDistribution(Integer testId);

    List<StudentSummaryResponse> getAllStudents(Long instituteId);

    List<TestProgressDTO> getStudentProgress(Long studentId);

    List<SubjectDataDTO> getStudentSubjects(Long studentId);
}
