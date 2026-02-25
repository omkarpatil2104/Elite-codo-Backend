package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.payload.response.StandardWithSubjectsDto;
import com.bezkoder.springjwt.payload.response.SubjectWithChaptersDto;

import java.util.List;

public interface LookupService {

    public List<StandardWithSubjectsDto> subjectsByStandards(List<Integer> standardIds);

    public List<SubjectWithChaptersDto> chaptersBySubjects(List<Integer> subjectIds);
}
