package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.TestType;
import com.bezkoder.springjwt.payload.request.TestTypeRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface TestTypeService {
    MainResponse create(TestTypeRequest testTypeRequest);

    MainResponse update(TestTypeRequest testTypeRequest);

    TestType getTestTypeById(Integer testTypeId);

    List<TestType> getAllTestTypes();

    List<TestType> getAllActiveTestTypes();
}
