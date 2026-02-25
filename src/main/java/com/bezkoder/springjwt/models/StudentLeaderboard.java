package com.bezkoder.springjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentLeaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

//    // If you need subject-wise ranking, store subject references as well:
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "subject_id")
//    private SubjectMaster subject; // optional if you do overall ranking

    private Double totalMarks;
    private Integer testAttempted;
//    private String academicYear;

    private LocalDateTime lastUpdated;
}
