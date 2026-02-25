package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.InstituteMaster;
import com.bezkoder.springjwt.payload.request.InstituteRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.repository.InstituteRepository;
import com.bezkoder.springjwt.services.InstituteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InstituteServiceImpl implements InstituteService {
    @Autowired
    private InstituteRepository instituteRepository;

    @Override
    public MainResponse createInstitute(InstituteRequest instituteRequest) {
        MainResponse mainResponse = new MainResponse();
        InstituteMaster instituteMaster = new InstituteMaster();
        try {
            BeanUtils.copyProperties(instituteRequest,instituteMaster);
            instituteMaster.setCreatedDate(new Date());
            this.instituteRepository.save(instituteMaster);
            mainResponse.setMessage("Institute create successfully");
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
    public MainResponse updateInstitute(InstituteRequest instituteRequest) {
        MainResponse mainResponse = new MainResponse();
        InstituteMaster instituteMaster = this.instituteRepository.findById(instituteRequest.getInstituteId()).get();
        try {
            BeanUtils.copyProperties(instituteRequest,instituteMaster);
            this.instituteRepository.save(instituteMaster);
            mainResponse.setMessage("Institute updated successfully");
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
    public List<InstituteMaster> getAll() {
        List<InstituteMaster> instituteMasters = this.instituteRepository.findAll();
        return instituteMasters;
    }

    @Override
    public List<InstituteMaster> getAllActive() {
        List<InstituteMaster> instituteMasters = this.instituteRepository.getAllActive();
        return instituteMasters;
    }

    @Override
    public InstituteMaster getById(Integer instituteId) {
        InstituteMaster instituteMaster = this.instituteRepository.findById(instituteId).get();
        return instituteMaster;
    }

    @Override
    public MainResponse deleteInstitute(Integer id) {
        MainResponse mainResponse = new MainResponse();
        try {
            Optional<InstituteMaster> optionalInstitute = instituteRepository.findById(id);

            if (optionalInstitute.isPresent()) {
                InstituteMaster institute = optionalInstitute.get();

                // Soft delete: mark status as "Deleted"
                institute.setStatus("Deleted");
                instituteRepository.save(institute);

                mainResponse.setMessage("Institute deleted successfully ");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } else {
                mainResponse.setMessage("Institute not found");
                mainResponse.setResponseCode(HttpStatus.NOT_FOUND.value());
                mainResponse.setFlag(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

}
