package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class NotificationMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    private Long senderId;

    private Long userId;

    private String subject;

    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String status;
}
