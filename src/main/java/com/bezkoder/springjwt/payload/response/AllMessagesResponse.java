package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllMessagesResponse {

    private Long replyToMessageId;
    private Long recipientIds;
    private Long id;
    private Long userId;
    private String userType;
    private String userName;
    private String message;
    private String title;
    private Date date;
    private String status;
    private String priority;
    private String attachments;
    private AllMessagesResponse reply;
}
