package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.PackageMaster;
import com.bezkoder.springjwt.payload.request.PackageMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface PackageMasterService {
    MainResponse create(PackageMasterRequest packageMasterRequest);

    MainResponse update(PackageMasterRequest packageMasterRequest);

    PackageMaster getById(Integer packageId);

    List<PackageMaster> getAll();

    List<PackageMaster> getAllActive();
}
