package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.TestType;
import com.bezkoder.springjwt.payload.request.TestTypeRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.repository.TestTypeRepository;
import com.bezkoder.springjwt.services.TestTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TestTypeServiceImpl implements TestTypeService {
    @Autowired
    private TestTypeRepository testTypeRepository;

    MainResponse mainResponse = new MainResponse();

    @Override
    public MainResponse create(TestTypeRequest testTypeRequest) {
        TestType testType = new TestType();
        try {
            BeanUtils.copyProperties(testTypeRequest,testType);
            testType.setDate(new Date());
            testType.setStatus("Active");
            this.testTypeRepository.save(testType);
            mainResponse.setMessage("Test type created successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }catch (Exception e){
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public MainResponse update(TestTypeRequest testTypeRequest) {
        Optional<TestType> testType = this.testTypeRepository.findById(testTypeRequest.getTestTypeId());
        if (testType.isPresent()){
            BeanUtils.copyProperties(testTypeRequest,testType.get());
            testType.get().setDate(new Date());
            try {
                this.testTypeRepository.save(testType.get());
                mainResponse.setMessage("Test type updated successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }catch (Exception e){
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        }else {
            mainResponse.setMessage("Test type not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public TestType getTestTypeById(Integer testTypeId) {
        Optional<TestType> testType = this.testTypeRepository.findById(testTypeId);
        if (testType.isPresent()){
            return testType.get();
        }else {
            return null;
        }
    }

    @Override
    public List<TestType> getAllTestTypes() {
        List<TestType> testTypes = this.testTypeRepository.findAll();
        return testTypes;
    }

    @Override
    public List<TestType> getAllActiveTestTypes() {
        List<TestType> testTypes = this.testTypeRepository.getAllActiveTestTypes();
        return testTypes;
    }
}
