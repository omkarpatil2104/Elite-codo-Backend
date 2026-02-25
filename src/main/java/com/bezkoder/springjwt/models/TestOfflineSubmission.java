package com.bezkoder.springjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "test_offline_submission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestOfflineSubmission {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "test_id", nullable = false)
        private TestMaster test;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "student_id", nullable = false)
        private User student;

        private Boolean isPass ;

        private double score;

        @ElementCollection
        @CollectionTable(name = "test_grades", joinColumns = @JoinColumn(name = "test_offlineSub_id"))
        @MapKeyColumn(name = "subject_name")
        @Column(name = "grade")
        private Map<String, Integer> grades = new HashMap<>();


    }
