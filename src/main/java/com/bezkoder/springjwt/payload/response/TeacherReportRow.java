package com.bezkoder.springjwt.payload.response;

public interface TeacherReportRow {

    Long   getId();
    String getName();
    Long   getStudentCount();
    Long   getTestCount();
    Double getAvgScore();
    Double getCompletionRate();
}
