package com.bezkoder.springjwt.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message_recipient")
public class MessageRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the main message
    private Long messageId;

    // The user who is receiving this
    private Long recipientId;

    // Track read/unread, or "open", "reply", etc.
    private String status;  // e.g. "unread", "read", "reply"

    @Temporal(TemporalType.TIMESTAMP)
    private Date readAt;


}