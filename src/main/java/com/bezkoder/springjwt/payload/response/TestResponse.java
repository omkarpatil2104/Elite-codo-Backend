package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.models.*;
import lombok.*;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestResponse {

    private Integer testId;
    private String testName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double marks;
    private Long id;
    private List<String> subjectName;
    private String status;
    private String typeOfTest;
    private List<String> chapterName;
    private Date testDate;
    private List<String> standardName;
    private String entranceExamName;
    private String yearOfAppearance;
    private Date createdDate;
    private Integer questionCount;

    public TestResponse(Integer testId, String testName, LocalTime startTime, LocalTime endTime, Double marks, Long id, List<String> subjectName, String status, String typeOfTest, List<String> chapterName, Date testDate, List<String> standardName, String entranceExamName, String yearOfAppearance, Date createdDate) {
        this.testId = testId;
        this.testName = testName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.marks = marks;
        this.id = id;
        this.subjectName = subjectName;
        this.status = status;
        this.typeOfTest = typeOfTest;
        this.chapterName = chapterName;
        this.testDate = testDate;
        this.standardName = standardName;
        this.entranceExamName = entranceExamName;
        this.yearOfAppearance = yearOfAppearance;
        this.createdDate = createdDate;
    }


}
