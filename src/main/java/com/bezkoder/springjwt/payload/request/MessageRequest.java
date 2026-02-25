package com.bezkoder.springjwt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    private Long senderId;

    private Long recipientIds;  // could be multiple or just one

    private Long replyToMessageId;

    private String attachments;

    private String title;

    private String category;

    private String description;

    private String priority;

    private String type;

    private String status;

    private String role;
}
