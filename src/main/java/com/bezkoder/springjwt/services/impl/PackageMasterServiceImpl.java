package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.PackageMaster;
import com.bezkoder.springjwt.payload.request.PackageMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.repository.PackageMasterRepository;
import com.bezkoder.springjwt.services.PackageMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PackageMasterServiceImpl implements PackageMasterService {
    @Autowired
    private PackageMasterRepository packageMasterRepository;

    @Override
    public MainResponse create(PackageMasterRequest packageMasterRequest) {
        MainResponse mainResponse = new MainResponse();
        PackageMaster packageMaster = new PackageMaster();
        BeanUtils.copyProperties(packageMasterRequest,packageMaster);
        try {
            packageMaster.setDate(new Date());
            this.packageMasterRepository.save(packageMaster);
            mainResponse.setMessage("Package create successfully");
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
    public MainResponse update(PackageMasterRequest packageMasterRequest) {
        MainResponse mainResponse = new MainResponse();
        PackageMaster packageMaster = this.packageMasterRepository.findById(packageMasterRequest.getPackageId()).get();
        BeanUtils.copyProperties(packageMasterRequest,packageMaster);
        try {
            packageMaster.setDate(new Date());
            this.packageMasterRepository.save(packageMaster);
            mainResponse.setMessage("Package updated successfully");
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
    public PackageMaster getById(Integer packageId) {
        Optional<PackageMaster> packageMaster = this.packageMasterRepository.findById(packageId);
        return packageMaster.get();
    }

    @Override
    public List<PackageMaster> getAll() {
        List<PackageMaster> packageMasters = this.packageMasterRepository.findAll();
        return packageMasters;
    }

    @Override
    public List<PackageMaster> getAllActive() {
        List<PackageMaster> packageMasters = this.packageMasterRepository.getAllActive();
        return packageMasters;
    }
}
