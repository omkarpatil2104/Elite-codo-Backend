package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportOfflineTestResponse {
    private int id;
    private String name;
    private Date date;
    private String semester;
    private String mode;
    private Double totalMarks;
    private int obtainedMarks;
    private double percentage;
    private String grade;
    private String status;
    private List<ReportForStudentTestResponse.SubjectResult> subjectResults;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectResult {
        private String subject;
        private int totalMarks;
        private int obtainedMarks;
        private double percentage;
        private String grade;
        private String status;


        public void updateSubjectResults(String subject, int totalMarks, int obtainedMarks) {
            this.subject = subject;
            this.totalMarks += totalMarks;
            this.obtainedMarks += obtainedMarks;
            this.percentage = (this.totalMarks > 0) ? (this.obtainedMarks * 100.0 / this.totalMarks) : 0.0;
            this.grade = calculateGrade(this.percentage);
            this.status = (this.percentage >= 40.0) ? "Pass" : "Fail";
        }

        private String calculateGrade(double percentage) {
            if (percentage >= 90) {
                return "A+";
            } else if (percentage >= 80) {
                return "A";
            } else if (percentage >= 70) {
                return "B+";
            } else if (percentage >= 60) {
                return "B";
            } else if (percentage >= 50) {
                return "C";
            } else if (percentage >= 40) {
                return "D";
            } else {
                return "F";
            }
        }
    }
}
