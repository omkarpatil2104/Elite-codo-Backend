package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.payload.request.NotificationGeneralRequest;
import com.bezkoder.springjwt.payload.response.ReportNotificationResponse;
import com.bezkoder.springjwt.payload.response.SimpleNotificationResponse;

import java.util.List;

public interface ReportNotificationService {
    void saveReportNotification(NotificationGeneralRequest request);

    List<ReportNotificationResponse> getNotificationsById(Long studentId,String role);

    List<SimpleNotificationResponse> findAllByTeacherAndRole(Long teacherId, String role);
}
