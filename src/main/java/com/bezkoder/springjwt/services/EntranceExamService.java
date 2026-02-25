package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.payload.request.EntranceExamRequest;
import com.bezkoder.springjwt.payload.response.ChapterReportResponse;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ReportResponse;
import com.bezkoder.springjwt.payload.response.StandardResponse;

import java.util.List;

public interface EntranceExamService {
    MainResponse create(EntranceExamRequest entranceExamRequest);

    MainResponse update(EntranceExamRequest entranceExamRequest);

    EntranceExamMaster entranceExamById(Integer entranceExamId);

    List<EntranceExamMaster> getAll();

    List<EntranceExamMaster> allActiveEntranceExam();

    StandardResponse entranceExamWiseStandard(Integer entranceExamId);

    MainResponse delete(Integer entranceExamId);

    List<ReportResponse> getEntranceExamReport();

    List<?> getSubjectsReport(Integer examId);

    List<ChapterReportResponse> getChaptersReport(Integer subjectId);

    StandardResponse entranceexamwiseActiveStandard(Integer entranceExamId);
}
