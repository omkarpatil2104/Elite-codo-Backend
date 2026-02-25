package com.bezkoder.springjwt.models;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.ToString;

import java.time.LocalTime;
import java.util.*;

@Entity
@Data
@ToString(exclude = {
        "testSubmissions",
        "chapterMasters",
        "subjectMasters"
        // any relationships that cause cycles
        })
public class TestMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testId;

    private String testName;

    private LocalTime startTime;

    private LocalTime endTime;

    private Double marks;

    @Pattern(regexp = "Online|Offline|Both", message = "testMode must be Online, Offline, or Both")
    private String testMode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date testDate;

    @ManyToOne
    private TestType testType;

    @ManyToOne
    private User createdBy;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @ManyToOne
    private YearOfAppearance yearOfAppearance;

    @ManyToOne
    private EntranceExamMaster entranceExamMaster;

    @ManyToMany
    @JoinTable(name = "test_standard",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "standard_id"))
    private List<StandardMaster> standardMaster;

    @ManyToMany
    @JoinTable(name = "test_subjects",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private List<SubjectMaster> subjectMaster;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "test_chapter",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "chapter_id"))
    private List<ChapterMaster> chapterMasters;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "test_topic",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<TopicMaster> topicMasters;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "test_question",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<QuestionMaster> questionMasters;

    private String status;

    private String typeOfTest;

    private String testGeneratedId;

    @OneToMany(mappedBy = "testMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamSet> examSets;

    private Integer testSet;


}
