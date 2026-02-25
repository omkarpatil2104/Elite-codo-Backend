package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.ThemeManagement;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.ThemeManagementRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ThemeManagementResponse;
import com.bezkoder.springjwt.repository.ThemeManagementRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.ThemeManagementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ThemeManagementServiceImpl implements ThemeManagementService {
    @Autowired
    private ThemeManagementRepository themeManagementRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public MainResponse create(ThemeManagementRequest themeManagementRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<User> user = this.userRepository.findById(themeManagementRequest.getId());
        ThemeManagement themeManagement = new ThemeManagement();
        BeanUtils.copyProperties(themeManagementRequest,themeManagement);
        try {
            themeManagement.setUser(user.get());
            themeManagement.setDate(new Date());
            this.themeManagementRepository.save(themeManagement);
            mainResponse.setMessage("Theme create successfully");
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
    public MainResponse update(ThemeManagementRequest themeManagementRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<User> user = this.userRepository.findById(themeManagementRequest.getId());
        ThemeManagement themeManagement = this.themeManagementRepository.findById(themeManagementRequest.getThemeId()).get();
        BeanUtils.copyProperties(themeManagementRequest,themeManagement);
        try {
            themeManagement.setUser(user.get());
            themeManagement.setDate(new Date());
            this.themeManagementRepository.save(themeManagement);
            mainResponse.setMessage("Theme updated successfully");
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
    public List<ThemeManagement> getAll() {
        List<ThemeManagement> themeManagements = themeManagementRepository.findAll();
        return themeManagements;
    }

    @Override
    public List<ThemeManagement> getAllActive() {
        List<ThemeManagement> themeManagements = themeManagementRepository.getAllActive();
        return themeManagements;
    }

    @Override
    public ThemeManagement getById(Integer themeId) {
        ThemeManagement themeManagement = this.themeManagementRepository.findById(themeId).orElse(null);
        return themeManagement;
    }

    @Override
    public List<ThemeManagementResponse> userIdWiseTheme(Long id) {
        List<ThemeManagementResponse> themeManagementResponses = this.themeManagementRepository.userIdWiseTheme(id);
        return themeManagementResponses;
    }
}
