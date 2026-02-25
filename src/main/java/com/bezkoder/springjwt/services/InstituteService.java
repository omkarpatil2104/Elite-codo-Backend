package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.InstituteMaster;
import com.bezkoder.springjwt.payload.request.InstituteRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface InstituteService {
    MainResponse createInstitute(InstituteRequest instituteRequest);

    MainResponse updateInstitute(InstituteRequest instituteRequest);

    List<InstituteMaster> getAll();

    List<InstituteMaster> getAllActive();

    InstituteMaster getById(Integer instituteId);

    public MainResponse deleteInstitute(Integer id);
}
