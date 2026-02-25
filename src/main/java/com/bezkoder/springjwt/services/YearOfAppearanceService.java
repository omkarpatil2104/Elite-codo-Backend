package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.YearOfAppearance;
import com.bezkoder.springjwt.payload.request.YearOfAppearanceRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface YearOfAppearanceService {
    MainResponse create(YearOfAppearanceRequest yearOfAppearanceRequest);

    MainResponse update(YearOfAppearanceRequest yearOfAppearanceRequest);

    YearOfAppearance getById(Integer yearOfAppearanceId);

    List<YearOfAppearance> getAll();

    List<YearOfAppearance> getAllActive();

    MainResponse delete(Integer yearOfAppearanceId);
}
