package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.ChapterMaster;
import com.bezkoder.springjwt.payload.request.ChapterRequest;
import com.bezkoder.springjwt.payload.request.ChapterWiseWeightageRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface ChapterService {
    MainResponse create(ChapterRequest chapterRequest);

    MainResponse update(ChapterRequest chapterRequest);

    ChapterMaster getChapterById(Integer chapterId);

    List<ChapterMaster> getAll();

    List<ChapterMaster> getAllActiveChapters();

    List<ChapterMaster> subjectWiseChapter(Integer subjectId);

    MainResponse delete(Integer chapterId);

    MainResponse chapterWiseWeightage(ChapterWiseWeightageRequest chapterWiseWeightageRequest);


    List<ChapterMaster> subjectStandWiseChapter(Integer subjectId, String isactive, Integer standardId);
}
