package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.NotificationMaster;
import com.bezkoder.springjwt.payload.request.NotificationMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ReportNotificationResponse;

import java.util.List;

public interface NotificationMasterService {
    MainResponse BroadCastSend(NotificationMasterRequest notificationMasterRequest);

    MainResponse individualSend(NotificationMasterRequest notificationMasterRequest);

    List<NotificationMaster> useridWiseNotifications(Long id);

    NotificationMaster notificationIdWiseNotification(Integer notificationId);

}
