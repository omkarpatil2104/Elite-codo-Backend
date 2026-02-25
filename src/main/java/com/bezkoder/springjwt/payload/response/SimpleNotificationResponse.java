package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SimpleNotificationResponse {

    private String type;            // e.g. "Reminder"
    private String title;           // e.g. "Assignment Due"
    private String content;         // e.g. "Your assignment is due tomorrow"
    private List<String> students;  // list of usernames / full-names
    private String reportType;      // e.g. "Academic"
}