package com.bezkoder.springjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message_master")
public class MessageMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long recipientIds;

    private Long replyToMessageId;

    private String title;

    private String attachments;

    private String category;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private String status;

    private String priority;
    private String type;  // e.g., "REPORT", "NOTIFICATION", etc.


}
