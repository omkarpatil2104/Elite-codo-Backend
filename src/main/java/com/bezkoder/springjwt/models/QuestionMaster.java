package com.bezkoder.springjwt.models;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class QuestionMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;

    @ManyToOne
    private User user;

    @ManyToOne
    private StandardMaster standardMaster;

    @ManyToOne
    private SubjectMaster subjectMaster;

    @ManyToOne
    private ChapterMaster chapterMaster;

    @ManyToOne
    private EntranceExamMaster entranceExamMaster;

    @ManyToOne
    private TopicMaster topicMaster;

    @ManyToOne
    private SubTopicMaster subTopicMaster;

    @ManyToOne
    private YearOfAppearance yearOfAppearance;

    @ManyToOne
    private QuestionType questionType;

    @ManyToOne
    private QuestionLevel questionLevel;

    private Integer marks;

    @Lob
    @Column(name = "question", length = 3000)
    private String question;

    @Lob
    @Column(name = "option1", length = 3000)
    private String option1;

    @Lob
    @Column(name = "option2", length = 3000)
    private String option2;

    @Lob
    @Column(name = "option3", length = 3000)
    private String option3;

    @Lob
    @Column(name = "option4", length = 3000)
    private String option4;

    @Lob
    @Column(name = "answer")
    private String answer;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "questions_answers",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "answer")
    private Set<String> multiAnswers = new HashSet<>();

    @Lob
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

    @ManyToOne
    private PatternMaster patternMaster;


    public QuestionMaster(Integer questionId, User user, StandardMaster standardMaster, SubjectMaster subjectMaster, ChapterMaster chapterMaster, EntranceExamMaster entranceExamMaster, TopicMaster topicMaster, SubTopicMaster subTopicMaster, Integer marks, Set<String> multiAnswers, String status) {
        this.questionId = questionId;
        this.user = user;
        this.standardMaster = standardMaster;
        this.subjectMaster = subjectMaster;
        this.chapterMaster = chapterMaster;
        this.entranceExamMaster = entranceExamMaster;
        this.topicMaster = topicMaster;
        this.subTopicMaster = subTopicMaster;
        this.marks = marks;
        this.multiAnswers = multiAnswers;
        this.status = status;
    }

    @Lob
    @Column(name = "solution", columnDefinition = "TEXT")
    private String solution;

    private String questionCategory;

    private Boolean asked;

    private String askedStatus;

}
