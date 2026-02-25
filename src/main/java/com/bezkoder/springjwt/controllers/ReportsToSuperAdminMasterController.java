package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.ReportsToSuperAdminMaster;
import com.bezkoder.springjwt.payload.request.ReportsToSuperAdminMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ReplyReportsToSuperAdminResponses;
import com.bezkoder.springjwt.payload.response.ReportsToSuperAdminResponse;
import com.bezkoder.springjwt.services.ReportsToSuperAdminMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/reportstosuperadmin")
public class ReportsToSuperAdminMasterController {
    @Autowired
    private ReportsToSuperAdminMasterService reportsToSuperAdminMasterService;

    @PostMapping("/sendreports")
    public ResponseEntity sendReports(@RequestBody ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest){
        MainResponse mainResponse = this.reportsToSuperAdminMasterService.sendReports(reportsToSuperAdminMasterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }

    }

    // Sending student report to the assigned teacher
    @PostMapping("/sendreporttoteacher")
    public ResponseEntity sendReportToTeacher(@RequestBody ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest){
        MainResponse mainResponse = this.reportsToSuperAdminMasterService.sendReportToTeacher(reportsToSuperAdminMasterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Get all teacher reports
    @GetMapping("/getallteacherreports")
    public ResponseEntity getAllTeacherReports(){
        List<ReportsToSuperAdminResponse> reportsToSuperAdminResponses = this.reportsToSuperAdminMasterService.getAllTeacherReports();
        return new ResponseEntity(reportsToSuperAdminResponses, HttpStatus.OK);
    }

    @GetMapping("/getallteacherreports/{id}")
    public ResponseEntity getAllTeacherReports(@PathVariable("id") Long id){
        List<ReportsToSuperAdminResponse> reportsToSuperAdminResponses = this.reportsToSuperAdminMasterService.getAllTeacherReportsById(id);
        return new ResponseEntity(reportsToSuperAdminResponses, HttpStatus.OK);
    }

    // Teacher read the report of student
    @GetMapping("/readstudentreport/{reportsToSuperAdminId}")
    public ResponseEntity readStudentReport(@PathVariable("reportsToSuperAdminId") Integer reportsToSuperAdminId){
        ReportsToSuperAdminResponse reportsToSuperAdminResponse = this.reportsToSuperAdminMasterService.readStudentReport(reportsToSuperAdminId);
        return new ResponseEntity(reportsToSuperAdminResponse, HttpStatus.OK);
    }

    // send reply message to the student
    @PostMapping("/replytostudent")
    public ResponseEntity replyToStudent(@RequestBody ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest){
        MainResponse mainResponse = this.reportsToSuperAdminMasterService.replyToStudent(reportsToSuperAdminMasterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getallreports")
    public ResponseEntity getAllReports(){
        List<ReportsToSuperAdminResponse> reportsToSuperAdminMasters = this.reportsToSuperAdminMasterService.getAllReports();
        return new ResponseEntity(reportsToSuperAdminMasters, HttpStatus.OK);
    }

    @GetMapping("/senderidwisereports/{senderId}")
    public ResponseEntity senderIdWiseReports(@PathVariable("senderId") Long senderId){
        List<ReportsToSuperAdminMaster> reportsToSuperAdminMasters = this.reportsToSuperAdminMasterService.senderIdWiseReports(senderId);
        return new ResponseEntity(reportsToSuperAdminMasters, HttpStatus.OK);
    }

    @GetMapping("/getreportsfromreportid/{reportsToSuperAdminId}")
    public ResponseEntity getReportsFromReportId(@PathVariable("reportsToSuperAdminId") Integer reportsToSuperAdminId){
        ReportsToSuperAdminResponse response = this.reportsToSuperAdminMasterService.getReportsFromReportId(reportsToSuperAdminId);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/reply")
    public ResponseEntity reply(@RequestBody ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest){
        MainResponse mainResponse = this.reportsToSuperAdminMasterService.reply(reportsToSuperAdminMasterRequest);
        if (Boolean.TRUE.equals(mainResponse.getFlag())){
            return new ResponseEntity(mainResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/readreport/{reportsToSuperAdminId}")
    public ResponseEntity readReport(@PathVariable("reportsToSuperAdminId") Integer reportsToSuperAdminId){
        ReportsToSuperAdminResponse response = this.reportsToSuperAdminMasterService.readReport(reportsToSuperAdminId);
        return new ResponseEntity(response, HttpStatus.OK);
    }


    @GetMapping("/getallreply/{reportsToSuperAdminId}")
    public ResponseEntity getAllReply(@PathVariable("reportsToSuperAdminId") Integer reportsToSuperAdminId){
        ReplyReportsToSuperAdminResponses reportsToSuperAdminResponses = this.reportsToSuperAdminMasterService.getAllReply(reportsToSuperAdminId);
        return new ResponseEntity(reportsToSuperAdminResponses, HttpStatus.OK);
    }

    @GetMapping("/getallstudentreply/{reportsToSuperAdminId}")
    public ResponseEntity getAllStudentReply(@PathVariable("reportsToSuperAdminId") Integer reportsToSuperAdminId){
        ReplyReportsToSuperAdminResponses reportsToSuperAdminResponses = this.reportsToSuperAdminMasterService.getAllStudentReply(reportsToSuperAdminId);
        return new ResponseEntity(reportsToSuperAdminResponses, HttpStatus.OK);
    }

}
