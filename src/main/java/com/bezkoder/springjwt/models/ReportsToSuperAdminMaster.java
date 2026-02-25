package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class ReportsToSuperAdminMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportsToSuperAdminId;

    private Integer replyReportsToSuperAdminId;

    private String title;

    private Long teacherId;

    private Long senderId;

    private Long receiverId;

    private String attachments;

    private String category;

    private String description;

    private String email;

    private String priority;

    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String status;
}
