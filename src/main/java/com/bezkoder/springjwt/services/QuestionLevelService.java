package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.QuestionLevel;
import com.bezkoder.springjwt.payload.request.QuestionLevelRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface QuestionLevelService {
    MainResponse create(QuestionLevelRequest questionLevelRequest);

    MainResponse update(QuestionLevelRequest questionLevelRequest);

    QuestionLevel getById(Integer questionLevelId);


    List<QuestionLevel> getAll();

    List<QuestionLevel> getAllActive();
}
