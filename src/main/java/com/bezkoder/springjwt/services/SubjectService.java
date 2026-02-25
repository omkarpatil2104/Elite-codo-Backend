package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.payload.request.SubjectRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.SubjectMasterResponse;
import com.bezkoder.springjwt.payload.response.SubjectResponse;

import java.util.List;

public interface SubjectService {
    MainResponse create(SubjectRequest subjectRequest);

    MainResponse update(SubjectRequest subjectRequest);

    SubjectMaster getSubjectById(Integer subjectId);

    List<SubjectMaster> getAll();

    List<SubjectMaster> getAllActive();

    SubjectResponse standardWiseSubjects(Integer standardId);

    SubjectResponse standardWiseActiveSubjects(Integer standardId);

    MainResponse delete(Integer subjectId);

    List<SubjectMasterResponse> entranceExamIdWiseSubjects(Integer entranceExamId);
}
