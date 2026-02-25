package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.YearOfAppearance;
import com.bezkoder.springjwt.payload.request.YearOfAppearanceRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.repository.YearOfAppearanceRepository;
import com.bezkoder.springjwt.services.YearOfAppearanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class YearOfAppearanceServiceImpl implements YearOfAppearanceService {
    @Autowired
    private YearOfAppearanceRepository yearOfAppearanceRepository;

    @Override
    public MainResponse create(YearOfAppearanceRequest yearOfAppearanceRequest) {
        MainResponse mainResponse = new MainResponse();
        YearOfAppearance yearOfAppearance = new YearOfAppearance();

        try {
            BeanUtils.copyProperties(yearOfAppearanceRequest,yearOfAppearance);
            yearOfAppearance.setDate(new Date());
            this.yearOfAppearanceRepository.save(yearOfAppearance);
            mainResponse.setMessage("Year of appearance create successfully");
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
    public MainResponse update(YearOfAppearanceRequest yearOfAppearanceRequest) {
        MainResponse mainResponse = new MainResponse();
        Optional<YearOfAppearance> yearOfAppearance = Optional.ofNullable(this.yearOfAppearanceRepository.findById(yearOfAppearanceRequest.getYearOfAppearanceId()).orElseThrow(()->new RuntimeException("Year of appearance not found")));

        try {
            BeanUtils.copyProperties(yearOfAppearanceRequest,yearOfAppearance.get());
            yearOfAppearance.get().setDate(new Date());
            this.yearOfAppearanceRepository.save(yearOfAppearance.get());
            mainResponse.setMessage("Year of appearance updated successfully");
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
    public YearOfAppearance getById(Integer yearOfAppearanceId) {
        Optional<YearOfAppearance> yearOfAppearance = Optional.ofNullable(this.yearOfAppearanceRepository.findById(yearOfAppearanceId).orElseThrow(()->new RuntimeException("Year of appearance not found")));
        if (yearOfAppearance.isPresent()){
            return yearOfAppearance.get();
        }else {
            return null;
        }
    }

    @Override
    public List<YearOfAppearance> getAll() {
        List<YearOfAppearance> yearOfAppearances = this.yearOfAppearanceRepository.findAll();
        return yearOfAppearances;
    }

    @Override
    public List<YearOfAppearance> getAllActive() {
        List<YearOfAppearance> yearOfAppearances = this.yearOfAppearanceRepository.getAllActive();
        return  yearOfAppearances.stream().sorted((a,b)-> Integer.compare(
                Integer.parseInt(b.getYearOfAppearance()),
                Integer.parseInt(a.getYearOfAppearance())
        )).collect(Collectors.toList());
//        return yearOfAppearances;
    }

    @Override
    public MainResponse delete(Integer yearOfAppearanceId) {
        System.out.println("APID = "+yearOfAppearanceId);
        MainResponse mainResponse = new MainResponse();
        try {
            this.yearOfAppearanceRepository.deleteById(yearOfAppearanceId);
            mainResponse.setMessage("Year of appearance deleted successfully");
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
}
