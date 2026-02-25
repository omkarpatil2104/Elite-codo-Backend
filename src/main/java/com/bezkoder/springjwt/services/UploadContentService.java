package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.UploadContentMaster;
import com.bezkoder.springjwt.payload.request.UploadContentRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.UploadContentMappedResponse;
import com.bezkoder.springjwt.payload.response.UploadContentMasterResponses;
import com.bezkoder.springjwt.payload.response.UploadContentQueRes;

import java.util.List;

public interface UploadContentService {
    MainResponse upload(UploadContentRequest uploadContentRequest);

    MainResponse update(UploadContentRequest uploadContentRequest);

    UploadContentMaster getById(Integer uploadContentId);

    List<UploadContentMasterResponses> getAll();

    List<UploadContentMaster> getAllActive();

    UploadContentMappedResponse allMappedUploadContents(Integer entranceExamId, Integer standardId, Long id);

    List<UploadContentMasterResponses> getAllById(Long uploaderId);

    List<UploadContentQueRes> getAllBystudentIdAndType(Long studId);

    void deleteUploadContentById(Integer id);
}
