package com.bezkoder.springjwt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionFilterRequest {
    private List<Integer> standardIds;
    private List<Integer> subjectIds;
    private List<Integer> chapterIds;
    private List<Integer> topicIds;

    private String  questionLevel;       // "easy" | "medium" | "hard"
    private String  questionCategory;    // "THEORETICAL" | "NUMERICAL"
    private Boolean pyq;

    /* 🆕  teacher-specific “used / unused / all” filter */
    private Long   teacherId;             // pass teacher-id from UI
    private String usedStatus = "ALL";    // ALL | USED | UNUSED

    private Integer currentPage = 1;    // default to page 1
    private Integer pageSize = 10; // default size

}
