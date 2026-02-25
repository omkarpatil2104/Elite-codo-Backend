package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.payload.request.StandardRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.StandardResponse;

import java.util.List;

public interface StandardService {
    MainResponse create(StandardRequest standardRequest);

    MainResponse update(StandardRequest standardRequest);

    StandardMaster getStandardById(Integer standardId);

    List<StandardMaster> getAllStandard();

    List<StandardMaster> getAllActiveStandard();

    List<EntranceExamMaster> standardWiseEntranceExam(Integer standardId);

    MainResponse delete(Integer standardId);

//    List<StandardResponse> entranceExamWiseStandard(Integer entranceExamId);
}
