package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.ChapterWeightageMaster;
import com.bezkoder.springjwt.payload.request.ChapterWiseWeightageRequest;
import com.bezkoder.springjwt.payload.response.ChapterWeightageResponse;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface ChapterWeightageService {
    MainResponse chapterWiseWeightage(ChapterWiseWeightageRequest chapterWiseWeightageRequest);

    MainResponse update(ChapterWiseWeightageRequest chapterWiseWeightageRequest);

    ChapterWeightageResponse getById(Integer chapterWeightageId);

    List<ChapterWeightageResponse> getAll();
}
