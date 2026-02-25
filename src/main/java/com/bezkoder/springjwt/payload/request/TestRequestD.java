package com.bezkoder.springjwt.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestRequestD {

    private Integer testId;

    private String testName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date testDate;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalTime startTime;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalTime endTime;

    private Double marks;

    private Integer entranceExamId;

    public Integer standardId;

    private Integer subjectId;

//    private Set<ChapterMaster> chapterMasters;
//
//    private Set<TopicMaster> topicMasters;
//
//    private Set<QuestionMaster> questionMasters;

      private List<QuestionReq> questionReqs;

//    private List<Integer> questionsId;

//    private Integer testTypeId;

//    private Integer yearOfAppearanceId;

    private Long createdBy;

    private Date createdDate;

    private String status;

    private String typeOfTest;
}
