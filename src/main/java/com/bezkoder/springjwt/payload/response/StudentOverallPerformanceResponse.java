package com.bezkoder.springjwt.payload.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentOverallPerformanceResponse {
    private StudentInfo student;
    private double overallGrade;
    private int attendance;
    private ClassRank classRank;
    private double improvement;
    private List<SubjectPerformance> subjects;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfo {
        private String name;
        private String grade;
        private String rollNo;
        private String avatar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassRank {
        private int rank;
        private String percentile;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectPerformance {
        private String subject;
        private double score;
        private double average;
        private double improvement;
        private String trend; // "up", "down", "stable"
    }
}