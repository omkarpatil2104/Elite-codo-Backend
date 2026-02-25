package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.NotificationMaster;
import com.bezkoder.springjwt.payload.request.NotificationGeneralRequest;
import com.bezkoder.springjwt.payload.request.NotificationMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.payload.response.ReportNotificationResponse;
import com.bezkoder.springjwt.payload.response.SimpleNotificationResponse;
import com.bezkoder.springjwt.services.NotificationMasterService;
import com.bezkoder.springjwt.services.ReportNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/notification")
public class NotificationMasterController {

    @Autowired
    private NotificationMasterService notificationMasterService;

    @Autowired
    private ReportNotificationService reportNotificationService;


    @GetMapping("/all/{role}/{teacherId}")
    public ResponseEntity<List<SimpleNotificationResponse>> getAllByTeacherAndRole(
            @PathVariable String role,
            @PathVariable Long teacherId) {

        List<SimpleNotificationResponse> responses =
                reportNotificationService.findAllByTeacherAndRole(teacherId, role);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/broadcastsend")
    public ResponseEntity BroadCastSend(@RequestBody NotificationMasterRequest notificationMasterRequest){
        MainResponse mainResponse = this.notificationMasterService.BroadCastSend(notificationMasterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/individualsend")
    public ResponseEntity individualSend(@RequestBody NotificationMasterRequest notificationMasterRequest){
        MainResponse mainResponse = this.notificationMasterService.individualSend(notificationMasterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    //  User id wise notifications
    @GetMapping("/useridwisenotifications/{id}")
    public ResponseEntity useridWiseNotifications(@PathVariable("id") Long id){
        List<NotificationMaster> notificationMaster = this.notificationMasterService.useridWiseNotifications(id);
        return new ResponseEntity(notificationMaster, HttpStatus.OK);
    }

    @GetMapping("/getNotifications/{id}")
    public ResponseEntity<List<ReportNotificationResponse>> getNotificationsById(@PathVariable("id") Long studentId,@RequestParam(required = true) String role){
        List<ReportNotificationResponse> reportNotificationResponse =
                reportNotificationService.getNotificationsById(studentId, role);

        if (reportNotificationResponse == null || reportNotificationResponse.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(reportNotificationResponse, HttpStatus.OK);

    }

    // Notification id wise notification
    @GetMapping("/notificationidwise/{notificationId}")
    public ResponseEntity notificationIdWiseNotification(@PathVariable("notificationId") Integer notificationId){
        NotificationMaster notificationMaster = this.notificationMasterService.notificationIdWiseNotification(notificationId);
        return new ResponseEntity(notificationMaster, HttpStatus.OK);
    }

    @PostMapping("/addNotification")
    public ResponseEntity<?> saveNotificationForStudAndParent(@RequestBody NotificationGeneralRequest request){

        try {
            // Validate request before processing
            if (request == null) {

                return new ResponseEntity<>(new MessageResponse("Request body is missing"), HttpStatus.BAD_REQUEST);
            }

            reportNotificationService.saveReportNotification(request);
            return new ResponseEntity<>(new MessageResponse("Added notification successfully!"), HttpStatus.OK);
        } catch (HttpMessageNotReadableException e) {
            return new ResponseEntity<>(new MessageResponse("Invalid JSON format: ")+ e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new MessageResponse("Invalid input: ") + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("An unexpected error occurred: ") + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
