package com.bezkoder.springjwt.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "pdf_sets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PDFSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long  creatorId;

    private String examName;
    private String date;

    private String examSetName;

    @Lob
    @Column(name = "question_sequence", nullable = false, columnDefinition = "TEXT")
    private String questionSequenceJson;

    public PDFSet(Long creatorId, String examName, String date, String examSetName, String csv) {
    }
}
