package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.request.StandardIdsReq;
import com.bezkoder.springjwt.payload.request.SubjectIdsReq;
import com.bezkoder.springjwt.payload.response.StandardWithSubjectsDto;
import com.bezkoder.springjwt.payload.response.SubjectDto;
import com.bezkoder.springjwt.payload.response.SubjectWithChaptersDto;
import com.bezkoder.springjwt.repository.ChapterRepository;
import com.bezkoder.springjwt.repository.SubjectRepository;
import com.bezkoder.springjwt.services.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/lookup")
@CrossOrigin("*")
@RequiredArgsConstructor
public class LookupController {


    @Autowired
    private LookupService lookupService;

    // POST.. /api/lookup/standards/12/subjects
    @PostMapping("/standards/subjects")
    public List<StandardWithSubjectsDto> subjects(
            @RequestBody @Valid StandardIdsReq req) {
        return lookupService.subjectsByStandards(req.getStandardIds());
    }

    /* POST /api/lookup/subjects/chapters */
    @PostMapping("/subjects/chapters")
    public List<SubjectWithChaptersDto> chapters(
            @RequestBody @Valid SubjectIdsReq req) {
        return lookupService.chaptersBySubjects(req.getSubjectIds());
    }
}
