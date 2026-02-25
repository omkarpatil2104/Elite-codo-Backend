package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.QuestionType;
import com.bezkoder.springjwt.payload.request.QuestionTypeRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface QuestionTypeService {
    MainResponse create(QuestionTypeRequest questionTypeRequest);

    MainResponse update(QuestionTypeRequest questionTypeRequest);

    QuestionType getById(Integer questionTypeId);

    List<QuestionType> getAll();

    List<QuestionType> getAllActive();

    MainResponse delete(Integer questionTypeId);
}
