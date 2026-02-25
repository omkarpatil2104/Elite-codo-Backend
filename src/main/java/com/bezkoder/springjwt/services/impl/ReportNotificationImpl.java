package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import com.bezkoder.springjwt.models.NotificationMaster;
import com.bezkoder.springjwt.models.ReportNotification;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.NotificationGeneralRequest;
import com.bezkoder.springjwt.payload.response.ReportNotificationResponse;
import com.bezkoder.springjwt.payload.response.SimpleNotificationResponse;
import com.bezkoder.springjwt.repository.ReportNotificationRepository;
import com.bezkoder.springjwt.repository.StudentManagementRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.ReportNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportNotificationImpl implements ReportNotificationService {

    @Autowired
    private ReportNotificationRepository reportNotificationRepository;

    @Autowired
    private StudentManagementRepository studentManagementRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveReportNotification(NotificationGeneralRequest request) {
        ReportNotification notification = new ReportNotification();
        notification.setTeacherId(request.getId());
        notification.setType(request.getType());
        notification.setUploadedFile(request.getUploadedFile());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setContent(request.getContent());
        notification.setReportType(request.getReportType());
        notification.setToRole(request.getToRole());
        if(request.getType().equalsIgnoreCase("Report")){

            notification.setStudents(request.getStudents());
        }else{
            notification.setStudents(userRepository.getAllUserIdByCreatorId(request.getId()));
        }
         reportNotificationRepository.save(notification);
    }

    public List<ReportNotificationResponse> getNotificationsById(Long id, String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new Apierrorr("Role cannot be null or empty", "400");
        }
        Long studentId;
        if(role.equalsIgnoreCase("parent")){
             studentId = (Long) Optional.ofNullable(userRepository.getStudentIdByParentId(id)).orElseThrow(() -> new Apierrorr("No student linked to parent ID " + id, "400"));
        }else{

             studentId = id;
        }

        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new Apierrorr("User with ID " + studentId + " not found", "400"));

        // Validate role input


        // Get report notifications filtered by student ID and role
        List<ReportNotification> reportNotifications = Optional.ofNullable(
                reportNotificationRepository.findByStudentIdAndRole(studentId, role)
        ).orElse(Collections.emptyList());

        if (reportNotifications.isEmpty()) {
            throw new Apierrorr("No notifications found for student ID " + studentId + " and role " + role, "400");
        }

        return reportNotifications.stream()
                .map(notification -> new ReportNotificationResponse(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getContent(),
                        notification.getDate(),
                        notification.getType(),
                        notification.getUploadedFile(),
                        notification.getReportType()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<SimpleNotificationResponse> findAllByTeacherAndRole(Long teacherId,
                                                                    String roleName) {

        /* --- validation unchanged --- */

        List<ReportNotification> notifications =
                Optional.ofNullable(
                        reportNotificationRepository.findByTeacherIdAndToRoleOrderByDateDesc(teacherId, roleName)
                ).orElse(Collections.emptyList());

        if (notifications.isEmpty()) {
            throw new Apierrorr("No notifications found for teacher "
                    + teacherId + " and role " + roleName,
                    "NO_NOTIFICATIONS_FOUND");
        }

        /* build <studentId -> username> map only once */
        Set<Long> studentIds = notifications.stream()
                .flatMap(n -> n.getStudents().stream())
                .collect(Collectors.toSet());

        Map<Long,String> userNameMap = userRepository.findAllById(studentIds)
                .stream()
                .collect(Collectors.toMap(
                        User::getId,
                        User::getUsername));

        /* map every ReportNotification → SimpleNotificationResponse */
        return notifications.stream()
                .map(n -> new SimpleNotificationResponse(
                        n.getType() != null ? n.getType() : "Reminder",
                        n.getTitle(),
                        n.getContent(),
                        n.getStudents().stream()
                                .map(id -> userNameMap.getOrDefault(id, "unknown"))
                                .collect(Collectors.toList()),
                        n.getReportType() != null ? n.getReportType() : "Academic"
                ))
                .collect(Collectors.toList());
    }


}
