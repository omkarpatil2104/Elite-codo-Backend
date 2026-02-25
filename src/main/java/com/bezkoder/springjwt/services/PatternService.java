package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.PatternMaster;
import com.bezkoder.springjwt.payload.request.PatternRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface PatternService {
    MainResponse create(PatternRequest patternRequest);

    MainResponse update(PatternRequest patternRequest);

    PatternMaster getById(Integer patternId);

    List<PatternMaster> getAllActive();

    List<PatternMaster> getAll();

    MainResponse delete(Integer patternId);
}
