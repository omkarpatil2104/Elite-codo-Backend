package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.models.StudentManagementMaster;
import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.payload.request.SubjectRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.StandardResponse1;
import com.bezkoder.springjwt.payload.response.SubjectMasterResponse;
import com.bezkoder.springjwt.payload.response.SubjectResponse;
import com.bezkoder.springjwt.repository.EntranceExamRepository;
import com.bezkoder.springjwt.repository.StandardRepository;
import com.bezkoder.springjwt.repository.StudentManagementRepository;
import com.bezkoder.springjwt.repository.SubjectRepository;
import com.bezkoder.springjwt.services.SubjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private EntranceExamRepository entranceExamRepository;

    @Autowired
    private StandardRepository standardRepository;

    @Autowired
    private StudentManagementRepository studentManagementRepository;

    MainResponse mainResponse = new MainResponse();

    @Override
    public MainResponse create(SubjectRequest subjectRequest) {
        SubjectMaster subjectMaster = new SubjectMaster();
        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(this.entranceExamRepository.findById(subjectRequest.getEntranceExamId()).orElseThrow(()-> new RuntimeException("Entrance exam not found")));
        try {
            Set<StandardMaster> standardMasters = new HashSet<>();
            for (Integer standardId : subjectRequest.getStandardIds()) {
                System.out.println("Sta Id = "+standardId);
                StandardMaster standardMaster = new StandardMaster();
                standardMaster = this.standardRepository.findById(standardId).get();
                System.out.println("Stanard Master = "+standardMaster);
                standardMasters.add(standardMaster);
            }
            subjectMaster.setStandardMaster(standardMasters);
            subjectMaster.setEntranceExamMaster(entranceExamMaster.get());
            BeanUtils.copyProperties(subjectRequest,subjectMaster);
            subjectMaster.setDate(new Date());
            subjectMaster.setStatus("Active");
//            subjectMaster.setStandardMaster(standardMaster.get());
            this.subjectRepository.save(subjectMaster);
            mainResponse.setMessage("Subject created successfully");
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
    public MainResponse update(SubjectRequest subjectRequest) {
        Optional<SubjectMaster> subjectMasterOpt = this.subjectRepository.findById(subjectRequest.getSubjectId());
        Optional<EntranceExamMaster> entranceExamMasterOpt = this.entranceExamRepository.findById(subjectRequest.getEntranceExamId());

        if (subjectMasterOpt.isPresent() && entranceExamMasterOpt.isPresent()) {
            SubjectMaster subjectMaster = subjectMasterOpt.get();
            EntranceExamMaster entranceExamMaster = entranceExamMasterOpt.get();

            BeanUtils.copyProperties(subjectRequest, subjectMaster);

            try {
                Set<StandardMaster> standardMasters = new HashSet<>();
                for (Integer standardId : subjectRequest.getStandardIds()) {
                    StandardMaster standardMaster = this.standardRepository.findById(standardId)
                            .orElseThrow(() -> new RuntimeException("Standard not found"));
                    standardMasters.add(standardMaster);
                }
                subjectMaster.setStandardMaster(standardMasters);
                subjectMaster.setEntranceExamMaster(entranceExamMaster);

                this.subjectRepository.save(subjectMaster);

                mainResponse.setMessage("Subject updated successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        } else {
            mainResponse.setMessage("Subject not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }



    @Override
    public SubjectMaster getSubjectById(Integer subjectId) {
        Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(subjectId);
        return subjectMaster.get();
    }

    @Override
    public List<SubjectMaster> getAll() {
        return this.subjectRepository.findAll();
    }

    @Override
    public List<SubjectMaster> getAllActive() {
        return this.subjectRepository.getAllActive();
    }

    @Override
    public SubjectResponse standardWiseSubjects(Integer standardId) {
//        System.out.println("SID = "+standardId);
        SubjectResponse subjectResponse = new SubjectResponse();
        StandardMaster standardMaster = standardRepository.findById(standardId).orElseThrow(()->new Apierrorr("standard not found","404"));
        subjectResponse.setStandardId(standardMaster.getStandardId());
        subjectResponse.setStandardName(standardMaster.getStandardName());
        List<SubjectMaster> subjectMasters = this.subjectRepository.standardWiseSubjects(standardId);
//        List<SubjectMaster> subjects = new ArrayList<>();
//        subjectResponse.setEntranceExamId(subjectMasters.stream().findFirst().get().getEntranceExamMaster().getEntranceExamId());
//        subjectResponse.setEntranceExamName(subjectMasters.stream().findFirst().get().getEntranceExamMaster().getEntranceExamName());
//        for (SubjectMaster subjectMaster : subjectMasters) {
//            subjectResponse.setStandardId(subjectMaster.getStandardMaster().g);
//            subjectResponse.setStandardName(subjectMaster.getSubjectName());
//            subjects.add(subjectMaster);
//        }
        subjectResponse.setSubjectMasters(subjectMasters);
        return subjectResponse;
    }

    @Override
    public SubjectResponse standardWiseActiveSubjects(Integer standardId) {
        System.out.println("SID = "+standardId);
        SubjectResponse subjectResponse = new SubjectResponse();
        List<SubjectMaster> subjectMasters = this.subjectRepository.standardWiseSubjects(standardId);
        List<SubjectMaster> subjects = new ArrayList<>();
//        subjectResponse.setEntranceExamId(subjectMasters.stream().findFirst().get().getEntranceExamMaster().getEntranceExamId());
//        subjectResponse.setEntranceExamName(subjectMasters.stream().findFirst().get().getEntranceExamMaster().getEntranceExamName());
        for (SubjectMaster subjectMaster : subjectMasters) {
            subjectResponse.setStandardId(subjectMaster.getSubjectId());
            subjectResponse.setStandardName(subjectMaster.getSubjectName());
            subjects.add(subjectMaster);
        }
        subjectResponse.setSubjectMasters(subjects);
        return subjectResponse;
    }

    @Override
    public MainResponse delete(Integer subjectId) {
        Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(subjectId);
        if (subjectMaster.isPresent()) {
            try {
                this.subjectRepository.deleteById(subjectId);
                mainResponse.setMessage("Subject deleted successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        } else {
            mainResponse.setMessage("Subject not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public List<SubjectMasterResponse> entranceExamIdWiseSubjects(Integer entranceExamId) {

        List<SubjectMaster> subjects =
                subjectRepository.findWithStandardsByEntranceExamId(entranceExamId);

        return subjects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts SubjectMaster → SubjectMasterResponse,
     * including the list of linked standards.
     */
    private SubjectMasterResponse mapToResponse(SubjectMaster sm) {

        List<StandardResponse1> standardResponses = sm.getStandardMaster()      // Set<StandardMaster>
                .stream()
                .filter(st -> "Active".equalsIgnoreCase(st.getStatus()))        // keep only active standards
                .map(st -> new StandardResponse1(st.getStandardId(),
                        st.getStandardName()))
                .collect(Collectors.toList());

        return new SubjectMasterResponse(
                sm.getSubjectId(),
                sm.getSubjectName(),
                sm.getDate(),
                sm.getStatus(),
                sm.getEntranceExamMaster().getEntranceExamId(),
                sm.getEntranceExamMaster().getEntranceExamName(),
                standardResponses);
    }



}
