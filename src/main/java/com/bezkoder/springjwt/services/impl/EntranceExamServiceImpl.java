package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.ChapterMaster;
import com.bezkoder.springjwt.models.EntranceExamMaster;
import com.bezkoder.springjwt.models.StandardMaster;
import com.bezkoder.springjwt.models.SubjectMaster;
import com.bezkoder.springjwt.payload.request.EntranceExamRequest;
import com.bezkoder.springjwt.payload.response.ChapterReportResponse;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ReportResponse;
import com.bezkoder.springjwt.payload.response.StandardResponse;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.EntranceExamService;
import com.bezkoder.springjwt.services.StandardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntranceExamServiceImpl implements EntranceExamService {
    @Autowired
    private EntranceExamRepository entranceExamRepository;

    @Autowired
    private StandardService standardService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
            private StandardRepository standardRepository;

    MainResponse mainResponse = new MainResponse();

    @Override
    public MainResponse create(EntranceExamRequest entranceExamRequest) {
        EntranceExamMaster entranceExamMaster = new EntranceExamMaster();
        BeanUtils.copyProperties(entranceExamRequest,entranceExamMaster);

         Set<StandardMaster> standardMasters = new HashSet<>();
        for (Integer standardId : entranceExamRequest.getStandardIds()) {
            StandardMaster standardMaster=new StandardMaster();
            standardMaster=standardRepository.findById(standardId).get();
            standardMasters.add(standardMaster);
        }
        entranceExamMaster.setStandardMasters(standardMasters);

        try {
            entranceExamMaster.setDate(new Date());
            entranceExamMaster.setStatus("Active");
            this.entranceExamRepository.save(entranceExamMaster);
            mainResponse.setMessage("Entrance Exam created successfully.");
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
    public MainResponse update(EntranceExamRequest entranceExamRequest) {
        Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(entranceExamRequest.getEntranceExamId());
        if (entranceExamMaster.isPresent()){
            BeanUtils.copyProperties(entranceExamRequest,entranceExamMaster.get());

            Set<StandardMaster> standardMasters = new HashSet<>();

            for (Integer standardId : entranceExamRequest.getStandardIds()) {
                StandardMaster standardMaster=new StandardMaster();
                standardMaster=standardRepository.findById(standardId).get();
                standardMasters.add(standardMaster);
            }
            entranceExamMaster.get().setStandardMasters(standardMasters);
            try {

                entranceExamMaster.get().setDate(new Date());
                this.entranceExamRepository.save(entranceExamMaster.get());
                mainResponse.setMessage("Entrance exam updated successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }catch (Exception e){
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        }else {
            mainResponse.setMessage("Entrance exam not found");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public EntranceExamMaster entranceExamById(Integer entranceExamId) {
        Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(entranceExamId);
        return entranceExamMaster.get();
    }

    @Override
    public List<EntranceExamMaster> getAll() {
        List<EntranceExamMaster> entranceExamMasters = this.entranceExamRepository.findAll();
        for (EntranceExamMaster entranceExamMaster : entranceExamMasters) {
            Set<StandardMaster> standardMasters = new HashSet<>();
            StandardMaster standardMaster = new StandardMaster();
            for (StandardMaster standardMaster1 : entranceExamMaster.getStandardMasters()) {
                standardMaster = this.standardRepository.findById(standardMaster1.getStandardId()).get();
                standardMasters.add(standardMaster);
            }
            entranceExamMaster.setStandardMasters(standardMasters);
        }
        Collections.reverse(entranceExamMasters);
        return entranceExamMasters;
    }

    @Override
    public List<EntranceExamMaster> allActiveEntranceExam() {
        List<EntranceExamMaster> entranceExamMasters = this.entranceExamRepository.allActiveEntranceExam();
        Collections.reverse(entranceExamMasters);
        return entranceExamMasters;
    }

    @Override
    public StandardResponse entranceExamWiseStandard(Integer entranceExamId) {
        StandardResponse standardResponses = new StandardResponse();

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(entranceExamId).get();
        standardResponses.setEntranceExamId(entranceExamMaster.getEntranceExamId());
        standardResponses.setEntranceExamName(entranceExamMaster.getEntranceExamName());
        List<StandardMaster> standardMasters = new ArrayList<>();
        for (StandardMaster standardMaster : entranceExamMaster.getStandardMasters()) {
            StandardMaster standardMaster1 = this.standardRepository.findById(standardMaster.getStandardId()).get();
            standardMasters.add(standardMaster1);
        }
        standardResponses.setStandardMasters(standardMasters);
        return standardResponses;
    }

    @Override
    public MainResponse delete(Integer entranceExamId) {
        Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(entranceExamId);
        if (entranceExamMaster.isPresent()) {
            try {
//                this.entranceExamRepository.deleteById(entranceExamId);
                entranceExamMaster.get().setStatus("Deleted");
                this.entranceExamRepository.save(entranceExamMaster.get());
                mainResponse.setMessage("Entrance exam deleted");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        }else {
            mainResponse.setMessage("Entrance exam not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public List<ReportResponse> getEntranceExamReport() {
        List<EntranceExamMaster> examMasterList = entranceExamRepository.findAll();
        List<ReportResponse> reportResponses = new ArrayList<>();

        for (EntranceExamMaster master : examMasterList) {
            ReportResponse response = new ReportResponse();
            response.setId(master.getEntranceExamId());
            response.setName(master.getEntranceExamName());

            int questionCount = questionRepository.countByEntranceExamId(master.getEntranceExamId());

            // ReportResponse.questionCount is a String, so convert from int
            response.setQuestionCount(questionCount);

            reportResponses.add(response);
        }

        return reportResponses;
    }

    @Override
    public List<?> getSubjectsReport(Integer examId) {
        // Find all subjects for this exam
        List<SubjectMaster> subjects = subjectRepository
                .findByEntranceExamMaster_EntranceExamId(examId);

        // Build the final response list
        List<ReportResponse> responseList = new ArrayList<>();

        for (SubjectMaster subject : subjects) {
            ReportResponse dto = new ReportResponse();

            // Convert subjectId to some string format or keep it as an integer
            // If you literally need "sub1", "sub2", etc., you might have to generate that naming scheme
            dto.setId(subject.getSubjectId());
            dto.setName(subject.getSubjectName());

            // Count how many questions exist for this exam + subject
            int questionCount = questionRepository.countByEntranceExamMaster_EntranceExamIdAndSubjectMaster_SubjectId(
                    examId, subject.getSubjectId()
            );
            dto.setQuestionCount(questionCount);

            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public List<ChapterReportResponse> getChaptersReport(Integer subjectId) {
        // 1) Find all chapters for the given subjectId
        List<ChapterMaster> chapters = chapterRepository.findBySubjectMaster_SubjectId(subjectId);

        // 2) Build the response
        List<ChapterReportResponse> responseList = new ArrayList<>();

        for (ChapterMaster chapter : chapters) {
            ChapterReportResponse dto = new ChapterReportResponse();

            // If you want "ch1", "ch2", etc.
            dto.setId(chapter.getChapterId());

            // If you want the raw integer ID, you could do: String.valueOf(chapter.getChapterId())

            dto.setName(chapter.getChapterName());

            // 3) Count questions for this chapter
            int questionCount = questionRepository.countByChapterMaster_ChapterId(chapter.getChapterId());
            dto.setQuestionCount(questionCount);

            // 4) Count topics for this chapter
            int topicCount = topicRepository.countByChapterMaster_ChapterId(chapter.getChapterId());
            dto.setTopicCount(topicCount);

            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public StandardResponse entranceexamwiseActiveStandard(Integer entranceExamId) {
        StandardResponse standardResponses = new StandardResponse();

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(entranceExamId).get();
        standardResponses.setEntranceExamId(entranceExamMaster.getEntranceExamId());
        standardResponses.setEntranceExamName(entranceExamMaster.getEntranceExamName());
        List<StandardMaster> standardMasters =  entranceExamMaster.getStandardMasters()
                .stream()
                .filter(a->"Active".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());


        standardResponses.setStandardMasters(standardMasters);
        return standardResponses;
    }
}
