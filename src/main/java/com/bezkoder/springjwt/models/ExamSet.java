package com.bezkoder.springjwt.models;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examSetName;

    @ElementCollection
    @CollectionTable(name = "exam_set_question_ids", joinColumns = @JoinColumn(name = "exam_set_id"))
    @Column(name = "question_ids")
    private List<Integer> questionIds;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private TestMaster testMaster;
}
