package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.dto.ChapterSummaryDto;
import com.bezkoder.springjwt.models.ChapterMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.payload.response.ChapterDto;
import com.bezkoder.springjwt.payload.response.StandardWithSubjectsDto;
import com.bezkoder.springjwt.payload.response.SubjectDto;
import com.bezkoder.springjwt.payload.response.SubjectWithChaptersDto;
import com.bezkoder.springjwt.repository.ChapterRepository;
import com.bezkoder.springjwt.repository.SubjectRepository;
import com.bezkoder.springjwt.services.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LookupServiceImpl implements LookupService {

    @Autowired
    private SubjectRepository subjectRepo;
    @Autowired
    private  ChapterRepository chapterRepo;

    /* ---------------- 1) subjects grouped by standard ---------------- */
    @Transactional(readOnly = true)
    public List<StandardWithSubjectsDto> subjectsByStandards(List<Integer> standardIds) {

        List<SubjectMaster> raw = subjectRepo.findByStandardIds(standardIds);

        /* group into the exact JSON structure the UI expects */
        Map<Integer, StandardWithSubjectsDto> map = new LinkedHashMap<>();

        for (SubjectMaster sub : raw) {
            for (StandardMaster std : sub.getStandardMaster()) {
                if (!standardIds.contains(std.getStandardId())) continue; // guard

                StandardWithSubjectsDto dto =
                        map.computeIfAbsent(std.getStandardId(), id -> {
                            StandardWithSubjectsDto d = new StandardWithSubjectsDto();
                            d.setStandardId(id);
                            d.setStandardName(std.getStandardName());
                            return d;
                        });

                dto.getSubjects()
                        .add(new SubjectDto(sub.getSubjectId(), sub.getSubjectName()));
            }
        }
        return new ArrayList<>(map.values());
    }

    /* ---------------- 2) chapters grouped by subject ---------------- */
    @Transactional(readOnly = true)
    public List<SubjectWithChaptersDto> chaptersBySubjects(List<Integer> subjectIds) {

        // Fetch chapter + questionCount using optimized JOIN query
        List<Object[]> raw = chapterRepo.findChaptersWithQuestionCount(subjectIds);

        Map<Integer, SubjectWithChaptersDto> map = new LinkedHashMap<>();

        for (Object[] row : raw) {

            Integer chapterId = (Integer) row[0];
            String chapterName = (String) row[1];
            Long questionCount = (Long) row[2];

            // Fetch chapter to determine subject
            ChapterMaster ch = chapterRepo.findById(chapterId).orElse(null);
            if (ch == null) continue;

            SubjectMaster sm = ch.getSubjectMaster();
            Integer subjectId = sm.getSubjectId();

            // Create subject entry if not exists
            SubjectWithChaptersDto dto =
                    map.computeIfAbsent(subjectId, id -> {
                        SubjectWithChaptersDto d = new SubjectWithChaptersDto();
                        d.setSubjectId(id);
                        d.setSubjectName(sm.getSubjectName());
                        return d;
                    });

            // Add chapter summary with count
            dto.getChapters().add(
                    new ChapterSummaryDto(
                            chapterId,
                            chapterName,
                            questionCount
                    )
            );
        }

        return new ArrayList<>(map.values());
    }

}
