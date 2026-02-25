package com.bezkoder.springjwt.payload.request;

import lombok.Data;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
public class CreateTestFromStudent {

    /*  -------  IDs / meta  ------- */
    private Long   id;          // student-id (who is creating the test)
    private String testName;
    private String testType;    // e.g. “Create Weightage Wise Test”
    private Date   testDate;

    /*  timings  */
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer   duration; // minutes (nullable)

    /*  question-filters  */
    private Integer level;      // question-level id (nullable)
    private Integer type;       // question-type id  (nullable)
    private Boolean used;       // true ⇒ omit questions already used by the student
    private Boolean asked;      // true ⇒ only PYQ
    private StudentTestLevelRequest levelWise;

    /*  syllabus scope  */
    private Integer  standard;        // new field  – id of standard / class (nullable)
    private Integer  subjectId;
    private Integer  chapterId;       // optional single-chapter tests
    private List<StudentTestChaptersRequest> chapters;
    private List<StudentTestTopicRequest>    topics;
    private List<Integer> selectedChapterIds;   // for Level-Wise branch
    private List<StudentTestSubjectsRequest>  groupSubjects;

    /*  weightage-wise specific  */
    private Integer totalQuestions;   // new field  – grand total to be generated
    private Integer remainingCount;   // new field  – for the UI to show how many still un-allocated

    /*  marks per question (nullable) */
    private Double mark;
}
