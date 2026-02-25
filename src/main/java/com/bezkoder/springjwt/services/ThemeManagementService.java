package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.ThemeManagement;
import com.bezkoder.springjwt.payload.request.ThemeManagementRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ThemeManagementResponse;

import java.util.List;

public interface ThemeManagementService {
    MainResponse create(ThemeManagementRequest themeManagementRequest);

    MainResponse update(ThemeManagementRequest themeManagementRequest);

    List<ThemeManagement> getAll();

    List<ThemeManagement> getAllActive();

    ThemeManagement getById(Integer themeId);

    List<ThemeManagementResponse> userIdWiseTheme(Long id);
}
