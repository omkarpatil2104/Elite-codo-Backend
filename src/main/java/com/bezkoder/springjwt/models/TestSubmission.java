package com.bezkoder.springjwt.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "test_submissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {
        "testSubmissionDetails",
        "testSubmission"
        })
public class TestSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testSubmissionId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "test_id" , nullable = false)
    private TestMaster test;

    private LocalDateTime submittedAt = LocalDateTime.now();

    private Boolean isPass ;

    private double score;


    @OneToMany(mappedBy = "testSubmission",cascade =CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<TestSubmissionDetail> submissionDetails;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "important_questions",
            joinColumns = @JoinColumn(name = "test_submission_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<QuestionMaster> importantQuestions;
}
