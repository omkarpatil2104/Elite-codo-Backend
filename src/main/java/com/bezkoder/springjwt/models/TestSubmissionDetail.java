package com.bezkoder.springjwt.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "test_submission_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {
        "testSubmission"
        })
public class TestSubmissionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testSubmissionDetailId;

    @ManyToOne
    @JoinColumn(name = "test_submission_id" , nullable = false)
    @JsonBackReference
    private TestSubmission testSubmission;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionMaster question;

    @ElementCollection
    @CollectionTable(name = "selected_answers", joinColumns = @JoinColumn(name = "test_submission_detail_id"))
    @Column(name = "answer")
    private List<String> selectedAnswers;


    private int timeSpentSeconds;
    private int visits;

}
