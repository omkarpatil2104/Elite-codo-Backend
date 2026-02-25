package com.bezkoder.springjwt.models;


import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class ReportNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long teacherId;

    private String type;

    private String uploadedFile;

    private String title;

    @Column(length = 2000) // Large content support
    private String content;

    @ElementCollection
    private List<Long> students; // List of student IDs

    private String reportType; // "monthly"

    private String toRole;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date = new Date();
}