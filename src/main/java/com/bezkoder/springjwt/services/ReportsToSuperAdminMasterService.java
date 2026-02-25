package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.ReportsToSuperAdminMaster;
import com.bezkoder.springjwt.payload.request.ReportsToSuperAdminMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ReplyReportsToSuperAdminResponses;
import com.bezkoder.springjwt.payload.response.ReportsToSuperAdminResponse;

import java.util.List;

public interface ReportsToSuperAdminMasterService {
    MainResponse sendReports(ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest);

    List<ReportsToSuperAdminMaster> senderIdWiseReports(Long senderId);

    List<ReportsToSuperAdminResponse> getAllReports();

    ReportsToSuperAdminResponse getReportsFromReportId(Integer reportsToSuperAdminId);

    MainResponse reply(ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest);

    ReportsToSuperAdminResponse readReport(Integer reportsToSuperAdminId);

    ReplyReportsToSuperAdminResponses getAllReply(Integer reportsToSuperAdminId);

    MainResponse sendReportToTeacher(ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest);

    List<ReportsToSuperAdminResponse> getAllTeacherReports();

    ReportsToSuperAdminResponse readStudentReport(Integer reportsToSuperAdminId);

    MainResponse replyToStudent(ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest);

    ReplyReportsToSuperAdminResponses getAllStudentReply(Integer reportsToSuperAdminId);

    List<ReportsToSuperAdminResponse> getAllTeacherReportsById(Long id);
}
