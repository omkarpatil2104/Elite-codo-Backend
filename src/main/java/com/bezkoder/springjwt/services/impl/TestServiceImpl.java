package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import com.bezkoder.springjwt.dto.*;
import com.bezkoder.springjwt.dto.ChapterDto;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.models.ExamSet;
import com.bezkoder.springjwt.payload.request.*;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.TestService;
import com.bezkoder.springjwt.spec.HierarchyService;
import com.bezkoder.springjwt.spec.QuestionSpecifications;
import com.bezkoder.springjwt.spec.TeacherUsedSpec;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TestServiceImpl implements TestService {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TestTypeRepository testTypeRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private EntranceExamRepository entranceExamRepository;
    @Autowired
    private TestSubmissionRepository submissionRepository;
    @Autowired
    private TestSubmissionDetailRepository submissionDetailRepository;
    @Autowired
    private ExamSetRepository examSetRepository;
    @Autowired
    private StudentLeaderboardRepository studentLeaderboardRepository;

    @Autowired
    private TeacherUsageRepository usageRepo;

    @Autowired
    private PDFSetRepository pdfSetRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private SubTopicRepository subTopicRepository;
    @Autowired
    private YearOfAppearanceRepository yearOfAppearanceRepository;
    @Autowired
    private QuestionTypeRepository questionTypeRepository;
    @Autowired
    private QuestionLevelRepository questionLevelRepository;
    @Autowired
    private PatternRepository patternRepository;
    @Autowired
    private TestQuestionDraftRepository testQuestionDraftRepository;
    @Autowired
    private StudentManagementRepository studentManagementRepository;
    @Autowired
    private ChapterWeightageRepository chapterWeightageRepository;
    @Autowired
    private TestOfflineSubmissionRepository testOfflineSubmissionRepository;
    @Autowired
    private HierarchyService hierarchyService;

    private static final DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    @Override
    public MainResponse create(TestRequest testRequest) {
        MainResponse mainResponse = new MainResponse();

        Optional<User> teacher = Optional.ofNullable(
                this.userRepository.findById(testRequest.getCreatedBy())
                        .orElseThrow(() -> new RuntimeException("Teacher not found."))
        );

        TestMaster testMaster = testRequest.getTestId() != null
                ? this.testRepository.findById(testRequest.getTestId()).orElse(new TestMaster())
                : new TestMaster();

        BeanUtils.copyProperties(testRequest, testMaster);

        List<QuestionMaster> questionMasters = testMaster.getQuestionMasters() != null
                ? new ArrayList<>(testMaster.getQuestionMasters())
                : new ArrayList<>();

        // Use sets to ensure uniqueness for new entries
        Set<StandardMaster> standardMasters = testMaster.getStandardMaster() != null
                ? new HashSet<>(testMaster.getStandardMaster())
                : new HashSet<>();

        Set<SubjectMaster> subjectMasters = testMaster.getSubjectMaster() != null
                ? new HashSet<>(testMaster.getSubjectMaster())
                : new HashSet<>();

        Set<ChapterMaster> chapterMasters = testMaster.getChapterMasters() != null
                ? new HashSet<>(testMaster.getChapterMasters())
                : new HashSet<>();

        Set<TopicMaster> topicMasters = testMaster.getTopicMasters() != null
                ? new HashSet<>(testMaster.getTopicMasters())
                : new HashSet<>();

        for (Integer questionId : testRequest.getQuestionsId()) {
            QuestionMaster questionMaster = this.questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question " + questionId + " is not found"));

            questionMasters.add(questionMaster);

            Optional<StandardMaster> standardMaster = Optional.ofNullable(questionMaster.getStandardMaster());
            standardMaster.ifPresent(standardMasters::add);

            Optional<SubjectMaster> subjectMaster = Optional.ofNullable(questionMaster.getSubjectMaster());
            subjectMaster.ifPresent(subjectMasters::add);

            Optional<ChapterMaster> chapterMaster = Optional.ofNullable(questionMaster.getChapterMaster());
            chapterMaster.ifPresent(chapterMasters::add);

            Optional<TopicMaster> topicMaster = Optional.ofNullable(questionMaster.getTopicMaster());
            topicMaster.ifPresent(topicMasters::add);

            Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(questionMaster.getEntranceExamMaster());
            entranceExamMaster.ifPresent(testMaster::setEntranceExamMaster);

            Optional<YearOfAppearance> yearOfAppearance = Optional.ofNullable(questionMaster.getYearOfAppearance());
            yearOfAppearance.ifPresent(testMaster::setYearOfAppearance);
        }

        testMaster.setTypeOfTest(testRequest.getTypeOfTest());
        testMaster.setStandardMaster(new ArrayList<>(standardMasters));
        testMaster.setSubjectMaster(new ArrayList<>(subjectMasters));
        testMaster.setChapterMasters(new ArrayList<>(chapterMasters));
        testMaster.setTopicMasters(new ArrayList<>(topicMasters));
        testMaster.setQuestionMasters(questionMasters);

        testMaster.setCreatedDate(new Date());
        testMaster.setCreatedBy(teacher.get());
        testMaster.setMarks(testRequest.getMarks());
        testMaster.setTestDate(testRequest.getTestDate());

        System.out.println("  Test request ="+testRequest);

        testMaster.setStartTime(testRequest.getStartTime());
        testMaster.setEndTime(testRequest.getEndTime());
        testMaster.setStatus("Upcoming");
        testMaster.setTestName(testRequest.getTestName());

        try {
            this.testRepository.save(testMaster);
            System.out.println("Test created successfully");
            mainResponse.setMessage("Test created successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }

        return mainResponse;
    }


    @Override
    public MainResponse update(TestRequest testRequest) {
        MainResponse mainResponse = new MainResponse();

        Optional<User> teacher = Optional.ofNullable(this.userRepository.findById(testRequest.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Teacher not found.")));

        TestMaster testMaster = this.testRepository.findById(testRequest.getTestId())
                .orElseThrow(() -> new RuntimeException("Test not found."));

        BeanUtils.copyProperties(testRequest, testMaster);

        Set<QuestionMaster> questionMasters = new HashSet<>(testMaster.getQuestionMasters() != null ? testMaster.getQuestionMasters() : new ArrayList<>());
        Set<StandardMaster> standardMasters = new HashSet<>(testMaster.getStandardMaster() != null ? testMaster.getStandardMaster() : new ArrayList<>());
        Set<SubjectMaster> subjectMasters = new HashSet<>(testMaster.getSubjectMaster() != null ? testMaster.getSubjectMaster() : new ArrayList<>());
        Set<ChapterMaster> chapterMasters = new HashSet<>(testMaster.getChapterMasters() != null ? testMaster.getChapterMasters() : new ArrayList<>());
        Set<TopicMaster> topicMasters = new HashSet<>(testMaster.getTopicMasters() != null ? testMaster.getTopicMasters() : new ArrayList<>());


        for (Integer questionId : testRequest.getQuestionsId()) {
            QuestionMaster questionMaster = this.questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question " + questionId + " is not found"));

            questionMasters.add(questionMaster);

            Optional<StandardMaster> standardMaster = Optional.ofNullable(questionMaster.getStandardMaster());
            standardMaster.ifPresent(standardMasters::add);

            Optional<SubjectMaster> subjectMaster = Optional.ofNullable(questionMaster.getSubjectMaster());
            subjectMaster.ifPresent(subjectMasters::add);

            Optional<ChapterMaster> chapterMaster = Optional.ofNullable(questionMaster.getChapterMaster());
            chapterMaster.ifPresent(chapterMasters::add);

            Optional<TopicMaster> topicMaster = Optional.ofNullable(questionMaster.getTopicMaster());
            topicMaster.ifPresent(topicMasters::add);

            Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(questionMaster.getEntranceExamMaster());
            entranceExamMaster.ifPresent(testMaster::setEntranceExamMaster);

            Optional<YearOfAppearance> yearOfAppearance = Optional.ofNullable(questionMaster.getYearOfAppearance());
            yearOfAppearance.ifPresent(testMaster::setYearOfAppearance);
        }

        testMaster.setStandardMaster(new ArrayList<>(standardMasters));
        testMaster.setSubjectMaster(new ArrayList<>(subjectMasters));
        testMaster.setChapterMasters(new ArrayList<>(chapterMasters));
        testMaster.setTopicMasters(new ArrayList<>(topicMasters));
        testMaster.setQuestionMasters(new ArrayList<>(questionMasters));

        testMaster.setCreatedDate(new Date());
        testMaster.setCreatedBy(teacher.get());
        testMaster.setMarks(testRequest.getMarks());
        testMaster.setTypeOfTest(testRequest.getTypeOfTest());
        testMaster.setTestDate(testRequest.getTestDate());
        testMaster.setStartTime(testRequest.getStartTime());
        testMaster.setEndTime(testRequest.getEndTime());
        testMaster.setStatus("Upcoming");
        testMaster.setTestName(testRequest.getTestName());

        try {
            this.testRepository.save(testMaster);
            System.out.println("Test updated successfully");
            mainResponse.setMessage("Test updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }

        return mainResponse;
    }


    @Override
    public TestMaster getById(Integer testId) {
        Optional<TestMaster> testMaster = Optional.ofNullable(this.testRepository.findById(testId).orElseThrow(() -> new RuntimeException("Test not found")));
        if (testMaster.isPresent()) {
            return testMaster.get();
        } else {
            return null;
        }
    }

    @Override
    public List<TestMaster> getAll() {
        updateAllTestStatuses();
        List<TestMaster> testMasters = this.testRepository.findAll();
        return testMasters;
    }

    @Override
    public List<TestMaster> getAllUpcomingTests() {

        List<TestMaster> testMasters = this.testRepository.statusWiseTests("Upcoming");
        return testMasters;
    }

    @Override
    public List<TestMaster> statusWiseTests(String status) {
        String inputStatus = status;
        if (inputStatus.equals("Upcoming")) {
            List<TestMaster> testMasters = this.testRepository.statusWiseTests(status);
            return testMasters;
        } else if (inputStatus.equals("Canceled")) {
            List<TestMaster> testMasters = this.testRepository.statusWiseTests(status);
            return testMasters;
        } else if (inputStatus.equals("Completed")) {
            List<TestMaster> testMasters = this.testRepository.statusWiseTests(status);
            return testMasters;
        } else {
            return null;
        }
    }

    @Override
    public List<TestQuestionsResponse> getQuestions(TestQuestionRequest testQuestionRequest) {

        Integer entranceExamId = testQuestionRequest.getEntranceExamId();
        Integer standardId = testQuestionRequest.getStandardId();
        Integer subjectId = testQuestionRequest.getSubjectId();
        Integer chapterId = testQuestionRequest.getChapterId();
        Integer topicId = testQuestionRequest.getTopicId();
        Integer subTopicId = testQuestionRequest.getSubTopicId();
        Integer yearOfAppearanceId = testQuestionRequest.getYearOfAppearanceId();
        Integer questionTypeId = testQuestionRequest.getQuestionTypeId();
        Integer questionLevelId = testQuestionRequest.getQuestionLevelId();
        Integer patternId = testQuestionRequest.getPatternId();
        String questionCategory = testQuestionRequest.getQuestionCategory();

        List<TestQuestionsResponse> testQuestionsResponses = this.questionRepository.filter(entranceExamId, standardId, subjectId, chapterId, topicId, subTopicId,
//                yearOfAppearanceId,
                questionTypeId, questionLevelId, patternId, questionCategory);
        System.out.println("testQuestionsResponses = = " + testQuestionsResponses.size());
        return testQuestionsResponses;
    }

    @Override
    public TestQuestionResponse testIdWiseQuestions(Integer testId) {
        TestQuestionResponse testQuestionResponse = new TestQuestionResponse();
        List<Integer> questionsIdList = new ArrayList<>();
        TestMaster master = this.testRepository.findById(testId).orElseThrow(() -> new RuntimeException("Test not found"));
        testQuestionResponse.setTestId(master.getTestId());
        testQuestionResponse.setTestDate(master.getTestDate());
        testQuestionResponse.setMarks(master.getMarks());
        testQuestionResponse.setTestName(master.getTestName());
        testQuestionResponse.setStartTime(master.getStartTime());
        testQuestionResponse.setEndTime(master.getEndTime());
        testQuestionResponse.setCreatedBy(master.getCreatedBy().getId());

        List<QuestionResponse> questionResponseList = new ArrayList<>();

        for (QuestionMaster questionMaster : master.getQuestionMasters()) {
            QuestionResponse questionResponse = questionRepository.getQuestionByQuestionId(questionMaster.getQuestionId());
            BeanUtils.copyProperties(questionMaster, questionResponse);
//            QuestionResponse questionResponse=questionRepository.getQuestionByQuestionId(questionMaster.getQuestionId());
            questionResponseList.add(questionResponse);
//            questionsIdList.add(questionMaster.getQuestionId());
        }
        testQuestionResponse.setQuestionResponses(questionResponseList);
//        testQuestionResponse.setQuestionsId(questionsIdList);

        return testQuestionResponse;
    }

    @Override
    public TestQuestionResponse testIdWiseImportantQuestions(Integer testId, Long studentId) {
        // 1) Fetch the matching TestSubmission.
        TestSubmission testSubmission = submissionRepository.findByTest_TestIdAndUser_Id(testId, studentId)
                .orElseThrow(() -> new RuntimeException("TestSubmission not found for testId " + testId +
                        " and studentId " + studentId));

        // 2) Extract the TestMaster from that submission.
        TestMaster master = testSubmission.getTest();

        // 3) Build the response for test-level details.
        TestQuestionResponse testQuestionResponse = new TestQuestionResponse();
        testQuestionResponse.setTestId(master.getTestId());
        testQuestionResponse.setTestDate(master.getTestDate());
        testQuestionResponse.setMarks(master.getMarks());
        testQuestionResponse.setTestName(master.getTestName());
        testQuestionResponse.setStartTime(master.getStartTime());
        testQuestionResponse.setEndTime(master.getEndTime());
        testQuestionResponse.setCreatedBy(master.getCreatedBy().getId());

        // 4) Get only the important questions from the TestSubmission entity.
        List<QuestionMaster> importantQuestions = testSubmission.getImportantQuestions();
//        System.out.println("-----------------imp"+importantQuestions);
        if (importantQuestions == null || importantQuestions.isEmpty()) {
            // No important questions found, return with empty question list.
            testQuestionResponse.setQuestionResponses(Collections.emptyList());
            return testQuestionResponse;
        }

        // 5) Convert each QuestionMaster into a QuestionResponse.
        List<QuestionResponse> questionResponseList = new ArrayList<>();
        for (QuestionMaster questionMaster : importantQuestions) {
            if (questionMaster != null) {
                // Option A: Direct copy if all fields match.
                QuestionResponse questionResponse = new QuestionResponse();
                BeanUtils.copyProperties(questionMaster, questionResponse);

                // Option B: Or fetch from the question repository if needed.
                // QuestionResponse questionResponse =
                //         questionRepository.getQuestionByQuestionId(questionMaster.getQuestionId());
                // BeanUtils.copyProperties(questionMaster, questionResponse);

                questionResponseList.add(questionResponse);
            }
        }

        // 6) Assign the filtered list to the response DTO.
        testQuestionResponse.setQuestionResponses(questionResponseList);

        return testQuestionResponse;
    }


    @Override
    public List<TestQuestionsResponse> getRandomQuestions(TestQuestionRequest testQuestionRequest) {
        User creator = this.userRepository.findById(testQuestionRequest.getCreatedBy()).get();

        Integer count = testQuestionRequest.getEnd() - testQuestionRequest.getStart();

        // Get checked question IDs from draft repository
        List<Integer> checkedQuestionIdsList = this.testQuestionDraftRepository.checkedQuestionIdsList(
                creator.getId(), testQuestionRequest.getEntranceExamId(), testQuestionRequest.getStandardId(),
                testQuestionRequest.getSubjectId(), testQuestionRequest.getChapterId(),
                testQuestionRequest.getTopicId(), testQuestionRequest.getSubTopicId(),
                testQuestionRequest.getYearOfAppearanceId(), testQuestionRequest.getQuestionTypeId(),
                testQuestionRequest.getQuestionLevelId(), testQuestionRequest.getPatternId(), testQuestionRequest.getQuestionCategory()
        );

        List<Integer> allfilteredQuestionsListIds = new ArrayList<>();
        // Fetch all filtered questions
        List<TestQuestionsResponse> allfilteredQuestionsList = this.questionRepository.filter(
                testQuestionRequest.getEntranceExamId(), testQuestionRequest.getStandardId(),
                testQuestionRequest.getSubjectId(), testQuestionRequest.getChapterId(),
                testQuestionRequest.getTopicId(), testQuestionRequest.getSubTopicId(),
//                testQuestionRequest.getYearOfAppearanceId(),
                testQuestionRequest.getQuestionTypeId(),
                testQuestionRequest.getQuestionLevelId(), testQuestionRequest.getPatternId(), testQuestionRequest.getQuestionCategory()
        );

        for (TestQuestionsResponse testQuestionsResponse : allfilteredQuestionsList) {
            allfilteredQuestionsListIds.add(testQuestionsResponse.getQuestionId());
        }
        List<TestQuestionsResponse> finalFilteredList = new ArrayList<>();

        //  random aahe serial
        if (testQuestionRequest.getType().equalsIgnoreCase("Random")) {
            // Check if all questions match
            if (allfilteredQuestionsListIds.size() == checkedQuestionIdsList.size() &&
                    allfilteredQuestionsListIds.containsAll(checkedQuestionIdsList)) {
                this.testQuestionDraftRepository.deleteByQuestionIds(checkedQuestionIdsList, creator.getId());

                // Recursive call to re-run the method after deletion
                return this.getRandomQuestions(testQuestionRequest);
            }
            // Filter out already checked question IDs
            Set<Integer> checkedQuestionIdsSet = new HashSet<>(checkedQuestionIdsList);
            finalFilteredList = allfilteredQuestionsList.stream()
                    .filter(q -> !checkedQuestionIdsSet.contains(q.getQuestionId())) // Exclude checked IDs
                    .limit(count)
                    .collect(Collectors.toList());
        }

        if (testQuestionRequest.getType().equalsIgnoreCase("Serial")) {

            this.testQuestionDraftRepository.deleteByQuestionIds(checkedQuestionIdsList, creator.getId());

            Integer start = testQuestionRequest.getStart();
            Integer end = testQuestionRequest.getEnd();
            while (start <= end) {
                finalFilteredList.add(allfilteredQuestionsList.get(start));
                start++;
            }
        }


        // Save filtered questions if random flag is true
        for (TestQuestionsResponse testQuestionsResponse : finalFilteredList) {
            if (testQuestionRequest.getType().equalsIgnoreCase("Random")) {

                TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                testQuestionDraft.setQuestionId(testQuestionsResponse.getQuestionId());
                testQuestionDraft.setEntranceExamId(testQuestionRequest.getEntranceExamId());
                testQuestionDraft.setStandardId(testQuestionRequest.getStandardId());
                testQuestionDraft.setSubjectId(testQuestionRequest.getSubjectId());
                testQuestionDraft.setChapterId(testQuestionRequest.getChapterId());
                testQuestionDraft.setTopicId(testQuestionRequest.getTopicId());
                testQuestionDraft.setSubTopicId(testQuestionRequest.getSubTopicId());
                testQuestionDraft.setYearOfAppearanceId(testQuestionRequest.getYearOfAppearanceId());
                testQuestionDraft.setQuestionTypeId(testQuestionRequest.getQuestionTypeId());
                testQuestionDraft.setQuestionLevelId(testQuestionRequest.getQuestionLevelId());
                testQuestionDraft.setPatternId(testQuestionRequest.getPatternId());
                testQuestionDraft.setUserId(creator.getId());
                testQuestionDraft.setQuestionCategory(testQuestionRequest.getQuestionCategory());

                try {
                    testQuestionDraftRepository.save(testQuestionDraft);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return finalFilteredList;
    }


    @Override
    public List<TestMasterResponse> entranceAndStandardIdWiseUpComingTest(Integer entranceExamId, Integer standardId) {
        List<TestMasterResponse> testMasterResponses = this.testRepository.entranceAndStandardIdWiseUpComingTest(entranceExamId, standardId);
        return testMasterResponses;
    }

    @Override
    public Integer filterWiseQuestionsCount(TestQuestionRequest testQuestionRequest) {
        Integer entranceExamId = testQuestionRequest.getEntranceExamId();
        Integer standardId = testQuestionRequest.getStandardId();
        Integer subjectId = testQuestionRequest.getSubjectId();
        Integer chapterId = testQuestionRequest.getChapterId();
        Integer topicId = testQuestionRequest.getTopicId();
        Integer subTopicId = testQuestionRequest.getSubTopicId();
        Integer yearOfAppearanceId = testQuestionRequest.getYearOfAppearanceId();
        Integer questionTypeId = testQuestionRequest.getQuestionTypeId();
        Integer questionLevelId = testQuestionRequest.getQuestionLevelId();
        Integer patternId = testQuestionRequest.getPatternId();
        String questionCategory = testQuestionRequest.getQuestionCategory();

        List<TestQuestionsResponse> testQuestionsResponses = this.questionRepository.filter(entranceExamId, standardId, subjectId, chapterId, topicId, subTopicId,
//                yearOfAppearanceId,
                questionTypeId, questionLevelId, patternId, questionCategory);
        Integer count = testQuestionsResponses.size();
        return count;
    }


    @Override
    public MainResponse weightageWiseTestCreation(TestQuestionRequest testQuestionRequest) {
        MainResponse mainResponse = new MainResponse();

//        User creator = this.userRepository.findById(testQuestionRequest.getCreatedBy()).get();
//        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(testQuestionRequest.getEntranceExamId()).orElseThrow(()->new RuntimeException("Entrance exam not found"));
//        StandardMaster standardMaster = this.standardRepository.findById(testQuestionRequest.getStandardId()).orElseThrow(()->new RuntimeException("Standard not found"));
//        SubjectMaster subjectMaster = this.subjectRepository.findById(testQuestionRequest.getSubjectId()).orElseThrow(()->new RuntimeException("Subject not found"));
//
//        List<>
        return mainResponse;
    }

    @Override
    public List<StudentChapterWeightageResponse> chapterWeightageByStudent(Long id, Integer entranceExamId) {
        List<StudentChapterWeightageResponse> studentChapterWeightageResponses = new ArrayList<>();

        // Fetch student and entrance exam details
        User student = this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(entranceExamId)
                .orElseThrow(() -> new RuntimeException("Entrance exam not found"));

        // Fetch student's standards
        Set<Integer> studentStandard = this.studentManagementRepository
                .getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId);

        for (Integer standardId : studentStandard) {
            StandardMaster standardMaster = this.standardRepository.findById(standardId)
                    .orElseThrow(() -> new RuntimeException("Standard not found"));

            Set<Integer> subjectIds = this.studentManagementRepository
                    .getSubjectsByStudentIdEntranceExamIdStandardId(
                            student.getId(),
                            entranceExamMaster.getEntranceExamId(),
                            standardMaster.getStandardId()
                    );

            for (Integer subjectId : subjectIds) {
                SubjectMaster subjectMaster = this.subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found"));

                StudentChapterWeightageResponse studentChapterWeightageResponse = new StudentChapterWeightageResponse();
                studentChapterWeightageResponse.setSubjectId(subjectMaster.getSubjectId());

                List<StudentWeightageChapterResponse> studentWeightageChapterResponses = new ArrayList<>();

                List<Integer> chapterIds = this.chapterRepository
                        .getChaptersByEntranceStandardSubject(
                                entranceExamMaster.getEntranceExamId(),
                                standardMaster.getStandardId(),
                                subjectMaster.getSubjectId()
                        );

                for (Integer chapterId : chapterIds) {
                    ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId)
                            .orElseThrow(() -> new RuntimeException("Chapter not found"));

                    Optional<ChapterWeightageMaster> chapterWeightageMaster = this.chapterWeightageRepository.getByChapterId(chapterId);

                    StudentWeightageChapterResponse studentWeightageChapterResponse = new StudentWeightageChapterResponse();
                    studentWeightageChapterResponse.setChapterId(chapterMaster.getChapterId());
                    studentWeightageChapterResponse.setWeightage(chapterWeightageMaster.map(ChapterWeightageMaster::getWeightage).orElse(0.0));

                    studentWeightageChapterResponses.add(studentWeightageChapterResponse);
                }

                studentChapterWeightageResponse.setChapters(studentWeightageChapterResponses);

                studentChapterWeightageResponses.add(studentChapterWeightageResponse);
            }
        }

        return studentChapterWeightageResponses;
    }

//    @Override
//    public List<TestQuestionsResponse> studentFilter(TestQuestionRequest testQuestionRequest) {
//        System.out.println(" testQuestionRequest = " + testQuestionRequest.toString());
//        User creator = this.userRepository.findById(testQuestionRequest.getCreatedBy()).orElse(null);
//
//        if (creator == null) {
//            return new ArrayList<>();
//        }
//
//        List<TestQuestionsResponse> testQuestionsResponses = new ArrayList<>();
//
//        if (testQuestionRequest.getEnd() == null || testQuestionRequest.getStart() == null ||
//                testQuestionRequest.getPyq() == null || testQuestionRequest.getType() == null) {
//
//            testQuestionsResponses = this.questionRepository.filter1(
//                    testQuestionRequest.getEntranceExamId(), testQuestionRequest.getStandardId(),
//                    testQuestionRequest.getSubjectId(), testQuestionRequest.getChapterId(),
//                    testQuestionRequest.getTopicId(), testQuestionRequest.getSubTopicId(),
//                    testQuestionRequest.getQuestionTypeId(), testQuestionRequest.getQuestionLevelId(),
//                    testQuestionRequest.getPatternId(), testQuestionRequest.getQuestionCategory(), true);
//
//            System.out.println(testQuestionsResponses.size());
//            return testQuestionsResponses;
//        }
//
//        Integer count = testQuestionRequest.getEnd() - testQuestionRequest.getStart();
//
//        // Get checked question IDs from draft repository
//        List<Integer> checkedQuestionIdsList = this.testQuestionDraftRepository.checkedQuestionIdsList(
//                creator.getId(), testQuestionRequest.getEntranceExamId(), testQuestionRequest.getStandardId(),
//                testQuestionRequest.getSubjectId(), testQuestionRequest.getChapterId(),
//                testQuestionRequest.getTopicId(), testQuestionRequest.getSubTopicId(),
//                testQuestionRequest.getYearOfAppearanceId(), testQuestionRequest.getQuestionTypeId(),
//                testQuestionRequest.getQuestionLevelId(), testQuestionRequest.getPatternId(), testQuestionRequest.getQuestionCategory()
//        );
//
//        List<TestQuestionsResponse> allfilteredQuestionsList = new ArrayList<>();
//
//        // **Apply Previous Year Question Filter**
//        if (testQuestionRequest.getPyq().equalsIgnoreCase("asked")) {
//            allfilteredQuestionsList = this.questionRepository.filter1(
//                    testQuestionRequest.getEntranceExamId(), testQuestionRequest.getStandardId(),
//                    testQuestionRequest.getSubjectId(), testQuestionRequest.getChapterId(),
//                    testQuestionRequest.getTopicId(), testQuestionRequest.getSubTopicId(),
//                    testQuestionRequest.getQuestionTypeId(), testQuestionRequest.getQuestionLevelId(),
//                    testQuestionRequest.getPatternId(), testQuestionRequest.getQuestionCategory(), true
//            );
//        } else if (testQuestionRequest.getPyq().equalsIgnoreCase("notasked")) {
//            allfilteredQuestionsList = this.questionRepository.filter2(
//                    testQuestionRequest.getEntranceExamId(), testQuestionRequest.getStandardId(),
//                    testQuestionRequest.getSubjectId(), testQuestionRequest.getChapterId(),
//                    testQuestionRequest.getTopicId(), testQuestionRequest.getSubTopicId(),
//                    testQuestionRequest.getQuestionTypeId(), testQuestionRequest.getQuestionLevelId(),
//                    testQuestionRequest.getPatternId(), testQuestionRequest.getQuestionCategory(), false
//            );
//        }
//
//        // **Apply Used/Unused Filter**
//        if (testQuestionRequest.getUsed() != null) {
//            List<Integer> usedQuestionIds = this.testQuestionDraftRepository.findUsedQuestionsByUser(creator.getId());
//
//            if (testQuestionRequest.getUsed()) {
//                // Return only previously used questions
//                allfilteredQuestionsList = allfilteredQuestionsList.stream()
//                        .filter(q -> usedQuestionIds.contains(q.getQuestionId()))
//                        .collect(Collectors.toList());
//            } else {
//                // Exclude previously used questions
//                allfilteredQuestionsList = allfilteredQuestionsList.stream()
//                        .filter(q -> !usedQuestionIds.contains(q.getQuestionId()))
//                        .collect(Collectors.toList());
//            }
//        }
//
//        // **Random or Serial Selection**
//        List<TestQuestionsResponse> finalFilteredList = new ArrayList<>();
//
//        if (testQuestionRequest.getType().equalsIgnoreCase("Random")) {
//            Set<Integer> checkedQuestionIdsSet = new HashSet<>(checkedQuestionIdsList);
//            finalFilteredList = allfilteredQuestionsList.stream()
//                    .filter(q -> !checkedQuestionIdsSet.contains(q.getQuestionId()))
//                    .limit(count)
//                    .collect(Collectors.toList());
//        }
//
//        if (testQuestionRequest.getType().equalsIgnoreCase("Serial")) {
//            if (allfilteredQuestionsList.size() > testQuestionRequest.getEnd()) {
//                finalFilteredList = allfilteredQuestionsList.stream()
//                        .skip(testQuestionRequest.getStart())
//                        .limit(testQuestionRequest.getEnd() - testQuestionRequest.getStart())
//                        .collect(Collectors.toList());
//            } else {
//                finalFilteredList.addAll(allfilteredQuestionsList);
//            }
//        }
//
//        // **Mark Questions as Used**
//        for (TestQuestionsResponse testQuestionsResponse : finalFilteredList) {
//            if (testQuestionRequest.getType().equalsIgnoreCase("Random")) {
//                TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
//                testQuestionDraft.setQuestionId(testQuestionsResponse.getQuestionId());
//                testQuestionDraft.setEntranceExamId(testQuestionRequest.getEntranceExamId());
//                testQuestionDraft.setStandardId(testQuestionRequest.getStandardId());
//                testQuestionDraft.setSubjectId(testQuestionRequest.getSubjectId());
//                testQuestionDraft.setChapterId(testQuestionRequest.getChapterId());
//                testQuestionDraft.setTopicId(testQuestionRequest.getTopicId());
//                testQuestionDraft.setSubTopicId(testQuestionRequest.getSubTopicId());
//                testQuestionDraft.setYearOfAppearanceId(testQuestionRequest.getYearOfAppearanceId());
//                testQuestionDraft.setQuestionTypeId(testQuestionRequest.getQuestionTypeId());
//                testQuestionDraft.setQuestionLevelId(testQuestionRequest.getQuestionLevelId());
//                testQuestionDraft.setPatternId(testQuestionRequest.getPatternId());
//                testQuestionDraft.setUserId(creator.getId());
//                testQuestionDraft.setQuestionCategory(testQuestionRequest.getQuestionCategory());
//
//                try {
//                    testQuestionDraftRepository.save(testQuestionDraft);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        for (TestQuestionsResponse testQuestionsResponse : finalFilteredList) {
//            QuestionMaster questionMaster = this.questionRepository.findById(testQuestionsResponse.getQuestionId()).orElse(null);
//            if (questionMaster != null) {
//                testQuestionsResponse.setMultiAnswers(questionMaster.getMultiAnswers());
//            }
//        }
//
//        return finalFilteredList;
//    }

    @Override
    public List<TestQuestionsResponse> studentFilter(TestQuestionRequest testQuestionRequest) {
        System.out.println(" testQuestionRequest = " + testQuestionRequest.toString());
        User creator = this.userRepository.findById(testQuestionRequest.getCreatedBy()).orElse(null);

        if (creator == null) {
            return new ArrayList<>();
        }

        List<TestQuestionsResponse> allfilteredQuestionsList = new ArrayList<>();

        // **Retrieve the Current Year**
        int currentYear = LocalDate.now().getYear();

        // **Apply Previous Year Question (PYQ) Filter based on YearOfAppearance**
        if ("asked".equalsIgnoreCase(testQuestionRequest.getPyq())) {
            allfilteredQuestionsList = this.questionRepository.findQuestionsByYearOfAppearanceBefore(currentYear);
        } else if ("notasked".equalsIgnoreCase(testQuestionRequest.getPyq())) {
            allfilteredQuestionsList = this.questionRepository.findQuestionsByYearOfAppearance(currentYear);
        }

        // **Apply Used/Unused Filter**
        if (testQuestionRequest.getUsed() != null) {
            List<Integer> usedQuestionIds = this.testQuestionDraftRepository.findUsedQuestionsByUser(creator.getId());

            if (testQuestionRequest.getUsed()) {
                allfilteredQuestionsList = allfilteredQuestionsList.stream()
                        .filter(q -> usedQuestionIds.contains(q.getQuestionId()))
                        .collect(Collectors.toList());
            } else {
                allfilteredQuestionsList = allfilteredQuestionsList.stream()
                        .filter(q -> !usedQuestionIds.contains(q.getQuestionId()))
                        .collect(Collectors.toList());
            }
        }

        // **Get checked question IDs from draft repository**
        List<Integer> checkedQuestionIdsList = this.testQuestionDraftRepository.checkedQuestionIdsList(
                creator.getId(), testQuestionRequest.getEntranceExamId(), testQuestionRequest.getStandardId(),
                testQuestionRequest.getSubjectId(), testQuestionRequest.getChapterId(),
                testQuestionRequest.getTopicId(), testQuestionRequest.getSubTopicId(),
                testQuestionRequest.getYearOfAppearanceId(), testQuestionRequest.getQuestionTypeId(),
                testQuestionRequest.getQuestionLevelId(), testQuestionRequest.getPatternId(),
                testQuestionRequest.getQuestionCategory()
        );

        // **Random or Serial Selection**
        List<TestQuestionsResponse> finalFilteredList = new ArrayList<>();
        Integer count = testQuestionRequest.getEnd() - testQuestionRequest.getStart();

        if (testQuestionRequest.getType().equalsIgnoreCase("Random")) {
            Set<Integer> checkedQuestionIdsSet = new HashSet<>(checkedQuestionIdsList);
            finalFilteredList = allfilteredQuestionsList.stream()
                    .filter(q -> !checkedQuestionIdsSet.contains(q.getQuestionId())) // Remove checked questions
                    .limit(count)
                    .collect(Collectors.toList());
        }

        if (testQuestionRequest.getType().equalsIgnoreCase("Serial")) {
            if (allfilteredQuestionsList.size() > testQuestionRequest.getEnd()) {
                finalFilteredList = allfilteredQuestionsList.stream()
                        .skip(testQuestionRequest.getStart())
                        .limit(testQuestionRequest.getEnd() - testQuestionRequest.getStart())
                        .collect(Collectors.toList());
            } else {
                finalFilteredList.addAll(allfilteredQuestionsList);
            }
        }

        // **Mark Questions as Used**
        for (TestQuestionsResponse testQuestionsResponse : finalFilteredList) {
            if (testQuestionRequest.getType().equalsIgnoreCase("Random")) {
                TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                testQuestionDraft.setQuestionId(testQuestionsResponse.getQuestionId());
                testQuestionDraft.setEntranceExamId(testQuestionRequest.getEntranceExamId());
                testQuestionDraft.setStandardId(testQuestionRequest.getStandardId());
                testQuestionDraft.setSubjectId(testQuestionRequest.getSubjectId());
                testQuestionDraft.setChapterId(testQuestionRequest.getChapterId());
                testQuestionDraft.setTopicId(testQuestionRequest.getTopicId());
                testQuestionDraft.setSubTopicId(testQuestionRequest.getSubTopicId());
                testQuestionDraft.setYearOfAppearanceId(testQuestionRequest.getYearOfAppearanceId());
                testQuestionDraft.setQuestionTypeId(testQuestionRequest.getQuestionTypeId());
                testQuestionDraft.setQuestionLevelId(testQuestionRequest.getQuestionLevelId());
                testQuestionDraft.setPatternId(testQuestionRequest.getPatternId());
                testQuestionDraft.setUserId(creator.getId());
                testQuestionDraft.setQuestionCategory(testQuestionRequest.getQuestionCategory());

                try {
                    testQuestionDraftRepository.save(testQuestionDraft);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // **Fetch Multi-Answer Information**
        for (TestQuestionsResponse testQuestionsResponse : finalFilteredList) {
            QuestionMaster questionMaster = this.questionRepository.findById(testQuestionsResponse.getQuestionId()).orElse(null);
            if (questionMaster != null) {
                testQuestionsResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            }
        }

        return finalFilteredList;
    }



    @Override
    public List<TestResponse> getUserIdWiseTests(Long id) {
        List<TestResponse> testResponseList = new ArrayList<>();
        List<TestMaster> testMasters = testRepository.findAllByCreatedBy_Id(id);

        for (TestMaster testMaster : testMasters) {
            TestResponse testResponse = TestResponse.builder()
                    .testId(testMaster.getTestId())
                    .testName(testMaster.getTestName())
                    .startTime(testMaster.getStartTime())
                    .endTime(testMaster.getEndTime())
                    .marks(testMaster.getMarks())
                    .id(Optional.ofNullable(testMaster.getCreatedBy()).map(User::getId).orElse(null))
                    .subjectName(Optional.ofNullable(testMaster.getSubjectMaster())
                            .map(subjects -> subjects.stream()
                                    .map(SubjectMaster::getSubjectName)
                                    .collect(Collectors.toList()))
                            .orElse(Collections.emptyList()))
                    .status(testMaster.getStatus())
                    .typeOfTest(testMaster.getTypeOfTest())
                    .chapterName(Optional.ofNullable(testMaster.getChapterMasters())
                            .map(chapters -> chapters.stream()
                                    .map(ChapterMaster::getChapterName)
                                    .collect(Collectors.toList()))
                            .orElse(Collections.emptyList()))
                    .testDate(testMaster.getTestDate())
                    .standardName(Optional.ofNullable(testMaster.getStandardMaster())
                            .map(standards -> standards.stream()
                                    .map(StandardMaster::getStandardName)
                                    .collect(Collectors.toList()))
                            .orElse(Collections.emptyList()))
                    .entranceExamName(Optional.ofNullable(testMaster.getEntranceExamMaster())
                            .map(EntranceExamMaster::getEntranceExamName)
                            .orElse(null))
                    .yearOfAppearance(Optional.ofNullable(testMaster.getYearOfAppearance())
                            .map(YearOfAppearance::getYearOfAppearance)
                            .orElse(null))
                    .createdDate(testMaster.getCreatedDate())
                    .questionCount(Optional.ofNullable(testMaster.getQuestionMasters())
                            .map(List::size)
                            .orElse(0))
                    .build();

            testResponseList.add(testResponse);
        }

        return testResponseList;
    }

    @Override
    public List<TestResponse> getStudentIdWiseTests(Long id) {
        List<TestResponse> testResponseList = new ArrayList<>();

        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found Of Id = " + id));

        Set<User> teachers = Optional.ofNullable(student.getTeacher()).orElse(Collections.emptySet());
        List<TestResponse> testResponses = new ArrayList<>();

        for (User teacher : teachers) {
            testResponses.addAll(getUserIdWiseTests(teacher.getId()));
        }

        return testResponses;
    }


    @Override
    public List<TestResponse> getTestCreatedByStudent(Long id) {
        List<TestResponse> testResponseList = new ArrayList<>();

        // Fetch the student user
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found with ID = " + id));

        // Fetch all tests created by the student
        List<TestMaster> testMasters = testRepository.findAllByCreatedBy(student);

        // Map TestMaster to TestResponse
        for (TestMaster  testMaster : testMasters) {
            TestResponse testResponse = TestResponse.builder()
                    .testId(testMaster.getTestId())
                    .testName(testMaster.getTestName())
                    .startTime(testMaster.getStartTime())
                    .endTime(testMaster.getEndTime())
                    .marks(testMaster.getMarks())
                    .id(testMaster.getCreatedBy() != null ? testMaster.getCreatedBy().getId() : null)
                    .subjectName(testMaster.getSubjectMaster() != null
                            ? testMaster.getSubjectMaster().stream().map(SubjectMaster::getSubjectName).collect(Collectors.toList())
                            : Collections.emptyList())
                    .status(testMaster.getStatus())
                    .typeOfTest(testMaster.getTypeOfTest())
                    .chapterName(testMaster.getChapterMasters() != null
                            ? testMaster.getChapterMasters().stream().map(ChapterMaster::getChapterName).collect(Collectors.toList())
                            : Collections.emptyList())
                    .testDate(testMaster.getTestDate())
                    .standardName(testMaster.getStandardMaster() != null
                            ? testMaster.getStandardMaster().stream().map(StandardMaster::getStandardName).collect(Collectors.toList())
                            : Collections.emptyList())
                    .entranceExamName(testMaster.getEntranceExamMaster() != null
                            ? testMaster.getEntranceExamMaster().getEntranceExamName()
                            : "N/A")
                    .yearOfAppearance(testMaster.getYearOfAppearance() != null
                            ? testMaster.getYearOfAppearance().getYearOfAppearance()
                            : null)
                    .createdDate(testMaster.getCreatedDate())
                    .questionCount(testMaster.getQuestionMasters() != null ? testMaster.getQuestionMasters().size() : 0)
                    .build();

            testResponseList.add(testResponse);
        }

        return testResponseList;
    }

    @Override
    public MainResponse createD(TestRequestD testRequestD) {
        MainResponse mainResponse = new MainResponse();

        Optional<User> teacher = Optional.ofNullable(
                this.userRepository.findById(testRequestD.getCreatedBy())
                        .orElseThrow(() -> new RuntimeException("Teacher not found."))
        );

        TestMaster testMaster = testRequestD.getTestId() != null
                ? this.testRepository.findById(testRequestD.getTestId()).orElse(new TestMaster())
                : new TestMaster();

        BeanUtils.copyProperties(testRequestD, testMaster);

        List<QuestionMaster> questionMasters = testMaster.getQuestionMasters() != null
                ? new ArrayList<>(testMaster.getQuestionMasters())
                : new ArrayList<>();

        // Use sets to ensure uniqueness for new entries
        Set<StandardMaster> standardMasters = testMaster.getStandardMaster() != null
                ? new HashSet<>(testMaster.getStandardMaster())
                : new HashSet<>();

        Set<SubjectMaster> subjectMasters = testMaster.getSubjectMaster() != null
                ? new HashSet<>(testMaster.getSubjectMaster())
                : new HashSet<>();

        Set<ChapterMaster> chapterMasters = testMaster.getChapterMasters() != null
                ? new HashSet<>(testMaster.getChapterMasters())
                : new HashSet<>();

        Set<TopicMaster> topicMasters = testMaster.getTopicMasters() != null
                ? new HashSet<>(testMaster.getTopicMasters())
                : new HashSet<>();

        String testGeneratedId=UUID.randomUUID().toString();
        System.out.println(" testGenerated Id ="+testGeneratedId);
        testMaster.setTestGeneratedId(testGeneratedId);

        Integer set=1;
        for (QuestionReq questionReq : testRequestD.getQuestionReqs()) {
            testMaster.setTestSet(set);//testMaster.setSet(set);
            for (Integer quesId : questionReq.getQuestionsId()) {
            QuestionMaster questionMaster = this.questionRepository.findById(quesId)
                    .orElseThrow(() -> new RuntimeException("Question " + quesId + " is not found"));
            questionMasters.add(questionMaster);
            Optional<StandardMaster> standardMaster = Optional.ofNullable(questionMaster.getStandardMaster());
            standardMaster.ifPresent(standardMasters::add);
            Optional<SubjectMaster> subjectMaster = Optional.ofNullable(questionMaster.getSubjectMaster());
            subjectMaster.ifPresent(subjectMasters::add);
            Optional<ChapterMaster> chapterMaster = Optional.ofNullable(questionMaster.getChapterMaster());
            chapterMaster.ifPresent(chapterMasters::add);
            Optional<TopicMaster> topicMaster = Optional.ofNullable(questionMaster.getTopicMaster());
            topicMaster.ifPresent(topicMasters::add);
            Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(questionMaster.getEntranceExamMaster());
            entranceExamMaster.ifPresent(testMaster::setEntranceExamMaster);
            Optional<YearOfAppearance> yearOfAppearance = Optional.ofNullable(questionMaster.getYearOfAppearance());
            yearOfAppearance.ifPresent(testMaster::setYearOfAppearance);
            }
            set++;
        }

        testMaster.setTypeOfTest(testRequestD.getTypeOfTest());
        testMaster.setStandardMaster(new ArrayList<>(standardMasters));
        testMaster.setSubjectMaster(new ArrayList<>(subjectMasters));
        testMaster.setChapterMasters(new ArrayList<>(chapterMasters));
        testMaster.setTopicMasters(new ArrayList<>(topicMasters));
        testMaster.setQuestionMasters(questionMasters);

        testMaster.setCreatedDate(new Date());
        testMaster.setCreatedBy(teacher.get());
        testMaster.setMarks(testRequestD.getMarks());
        testMaster.setTestDate(testRequestD.getTestDate());

        System.out.println("  Test request ="+testRequestD);

        testMaster.setStartTime(testRequestD.getStartTime());
        testMaster.setEndTime(testRequestD.getEndTime());
        testMaster.setStatus("Upcoming");
        testMaster.setTestName(testRequestD.getTestName());

        try {
            this.testRepository.save(testMaster);
            System.out.println("Test created successfully");
            mainResponse.setMessage("Test created successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }

        return mainResponse;

    }


    @Override
    public MainResponse createTestFromStudentSide(CreateTestFromStudent createTestFromStudent) {
        MainResponse mainResponse = new MainResponse();

        mainResponse.setId(createTestFromStudent.getId());

        // Validate student existence
        User student = userRepository.findById(createTestFromStudent.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!"Active".equalsIgnoreCase(student.getStatus())) {
            mainResponse.setMessage("Student not active");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
            return mainResponse;
        }

        String testType = createTestFromStudent.getTestType();
        Integer questionLevel = createTestFromStudent.getLevel();
        Integer questionType = createTestFromStudent.getType();

        try
        {
//            //          ======================      Create Chapter Wise Test        ============================
            if (testType.equalsIgnoreCase("Create Chapter Wise Test")) {
                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);
                log.info("  enter in chapter wise ");
                // Fetch necessary IDs
                Integer entranceExamId = studentManagementRepository.getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));
                log.info("  enter in chapter wise 2");
                System.out.println("ENTRANCE EXAM ID = " + entranceExamId);

                Integer standardId = studentManagementRepository.getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));

                System.out.println("Standard ID = " + standardId);

                Set<Integer> subjectIds = studentManagementRepository.getSubjectsByStudentIdEntranceExamIdStandardId(
                        student.getId(), entranceExamId, standardId);
                log.info("  enter in chapter wise 3");
                System.out.println("subjectIDS = " + subjectIds);

                if (!subjectIds.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID not valid for this student.");
                }
                log.info("  enter in chapter wise 4");
                // Validate and process chapters
                List<Integer> validChapters = chapterRepository.getChaptersByEntranceStandardSubject(
                        entranceExamId, standardId, createTestFromStudent.getSubjectId());

                System.out.println("VALID CHAPTERS = " + validChapters);

                log.info("  enter in chapter wise 5 ");

                List<QuestionMaster> questions = new ArrayList<>();
                List<ChapterMaster> chapterMasters = new ArrayList<>();

                for (StudentTestChaptersRequest chapter : createTestFromStudent.getChapters()) {
                    if (chapter.getQuestionCount() > 0) {
                        Integer chapterId = chapter.getChapterId();
                        Integer questionsCount = chapter.getQuestionCount();

                        if (!validChapters.contains(chapterId)) {
                            log.info("  enter in chapter wise 6");
                            System.out.println("INVALID CHAPTER");
                            throw new RuntimeException("Chapter ID " + chapterId + " is not valid for the selected subject.");
                        }
                        log.info("  enter in chapter wise 7");

                        ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId).get();
                        chapterMasters.add(chapterMaster);
                        // Fetch and set limit to questions
                        List<QuestionMaster> chapterQuestions = questionRepository.questionsByChapterId(
                                entranceExamId, standardId, chapterId,
                                createTestFromStudent.getSubjectId(),
                                questionLevel, questionType,
                                createTestFromStudent.getAsked()
                        ).stream().collect(Collectors.toList());

                        Integer countChapterQuestions = chapterQuestions.size();
                        Integer limitQuestions = questionsCount;

                        System.out.println("countChapterQuestions = " + countChapterQuestions);
                        System.out.println("limitQuestions = " + limitQuestions);
                        log.info("  enter in chapter wise 8");
                        if (countChapterQuestions >= limitQuestions) {
                            log.info("  enter in chapter wise 9");
                            chapterQuestions = chapterQuestions.stream().limit(limitQuestions).collect(Collectors.toList());

                        } else {
                            log.info("  enter in chapter wise 10");
                            throw new RuntimeException("Requested questions exceed available questions. Available questions: " + countChapterQuestions);
                        }

                        for (QuestionMaster chapterQuestion : chapterQuestions) {
                            System.out.println("DRAFT 1");
                            QuestionMaster questionMaster = this.questionRepository.findById(chapterQuestion.getQuestionId()).get();

                            System.out.println("DRAFT 2 ");
                            TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                            System.out.println("DRAFT 3");
                            System.out.println("TopicMaster: " + questionMaster.getTopicMaster());
                            System.out.println("PatternMaster: " + questionMaster.getPatternMaster());
                            System.out.println("YearOfAppearance: " + questionMaster.getYearOfAppearance());

                            testQuestionDraft.setQuestionId(questionMaster.getQuestionId());
                            testQuestionDraft.setEntranceExamId(questionMaster.getEntranceExamMaster().getEntranceExamId());
                            testQuestionDraft.setStandardId(questionMaster.getStandardMaster().getStandardId());
                            testQuestionDraft.setSubjectId(questionMaster.getSubjectMaster().getSubjectId());
                            testQuestionDraft.setChapterId(questionMaster.getChapterMaster().getChapterId());
                           // testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());

                            if (questionMaster.getTopicMaster() != null)
                                testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());

                            testQuestionDraft.setQuestionCategory(questionMaster.getQuestionCategory());
                            testQuestionDraft.setUserId(student.getId());

                            //testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());

                            if (questionMaster.getQuestionLevel() != null)
                                testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());

                            if (questionMaster.getQuestionType() != null)
                                testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());

                            if (questionMaster.getPatternMaster() != null)
                                testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());

                            if (questionMaster.getYearOfAppearance() != null)
                                testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                          //  testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                            //testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                            //testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                            testQuestionDraft.setTypeOfTest(createTestFromStudent.getTestType());
                            System.out.println("DRAFT 4");
                            try {

                                System.out.println(">>> Saving Draft for Question ID: " + questionMaster.getQuestionId());
                                System.out.println("EntranceExamId: " + (questionMaster.getEntranceExamMaster() != null ? questionMaster.getEntranceExamMaster().getEntranceExamId() : "NULL"));
                                System.out.println("StandardId: " + (questionMaster.getStandardMaster() != null ? questionMaster.getStandardMaster().getStandardId() : "NULL"));
                                System.out.println("SubjectId: " + (questionMaster.getSubjectMaster() != null ? questionMaster.getSubjectMaster().getSubjectId() : "NULL"));
                                System.out.println("ChapterId: " + (questionMaster.getChapterMaster() != null ? questionMaster.getChapterMaster().getChapterId() : "NULL"));
                                System.out.println("TopicId: " + (questionMaster.getTopicMaster() != null ? questionMaster.getTopicMaster().getTopicId() : "NULL"));
                                System.out.println("PatternId: " + (questionMaster.getPatternMaster() != null ? questionMaster.getPatternMaster().getPatternId() : "NULL"));
                                System.out.println("YearOfAppearanceId: " + (questionMaster.getYearOfAppearance() != null ? questionMaster.getYearOfAppearance().getYearOfAppearanceId() : "NULL"));
                                this.testQuestionDraftRepository.save(testQuestionDraft);
                                System.out.println("DRAFT SAVE");
                            }catch (Exception e) {
                                log.error("Error while saving testQuestionDraft: ", e);
                                e.printStackTrace(); // ✅ Force print to console
                                mainResponse.setMessage("Error while saving draft: " + e.toString());
                                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                                mainResponse.setFlag(false);
                                return mainResponse;
                            }

                        }
                        System.out.println("CHAPTRE QUESTIONS =---- " + chapterQuestions);

                        // Create and save test

                        log.info("  enter in chapter wise 11");

                        questions.addAll(chapterQuestions);
//                    testMaster.setQuestionMasters(chapterQuestions);
                    }
                }

                testMaster.setQuestionMasters(questions);

//                Double marks = questions.size() * createTestFromStudent.getMark();
//                testMaster.setMarks(marks);

                testMaster.setCreatedBy(student);
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setCreatedDate(new Date());
                //testMaster.setEntranceExamMaster(student.getEntranceExamMasters().stream().findFirst().get());

                if (student.getEntranceExamMasters() != null && !student.getEntranceExamMasters().isEmpty()) {
                    EntranceExamMaster entranceExamMaster = student.getEntranceExamMasters().iterator().next();
                    testMaster.setEntranceExamMaster(entranceExamMaster);
                } else {
                    log.error("Student has no entrance exam assigned!");
                    mainResponse.setMessage("Student has no entrance exam assigned!");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                    return mainResponse;
                }

                StandardMaster standardMaster = this.standardRepository.findById(standardId).get();
                List<StandardMaster> standardMasters = new ArrayList<>();
                standardMasters.add(standardMaster);
                testMaster.setStandardMaster(standardMasters);

                SubjectMaster subjectMaster = subjectRepository.findById(createTestFromStudent.getSubjectId()).get();
                List<SubjectMaster> subjectMasters = new ArrayList<>();
                subjectMasters.add(subjectMaster);
                testMaster.setSubjectMaster(subjectMasters);

                testMaster.setChapterMasters(chapterMasters);

                testMaster.setStatus("Upcoming");

                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());
                testMaster.setTestName(createTestFromStudent.getTestName());

                log.info("  enter in chapter wise 12");

                log.info("Before saving test: {}", testMaster);
                log.info("Entrance Exam Masters size: {}", student.getEntranceExamMasters().size());
                try {
                    testRepository.save(testMaster);
                    log.info("  enter in chapter wise 12");
                    System.out.println("TEST CREATEd");

                    System.out.println("Chapter Wise Test Created Successfully");
                    mainResponse.setMessage("Chapter Wise Test Created Successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);

                } catch (Exception e) {
                    log.error("Error while creating Chapter Wise Test", e);
                    mainResponse.setMessage(e.getMessage());
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            }
            /* =====================  Create Weightage Wise Test  ===================== */
            else if (testType.equalsIgnoreCase("Create Weightage Wise Test")) {

                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);

                /* --------  common look-ups (entrance-exam, standard, subject, etc.)  -------- */
                Integer entranceExamId = studentManagementRepository.getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));

                Integer standardId = studentManagementRepository
                        .getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));

                /*  subject validation  */
                Set<Integer> subjectIds = studentManagementRepository
                        .getSubjectsByStudentIdEntranceExamIdStandardId(student.getId(), entranceExamId, standardId);

                if (!subjectIds.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID not valid for this student.");
                }

                /*  valid chapters for that subject  */
                List<Integer> validChapters = chapterRepository.getChaptersByEntranceStandardSubject(
                        entranceExamId, standardId, createTestFromStudent.getSubjectId());

                /*  ------  weightage computation  ------ */

                int totalQuestions = createTestFromStudent.getTotalQuestions();             // e.g. 100
                int weightSum     = createTestFromStudent.getChapters()
                        .stream().mapToInt(StudentTestChaptersRequest::getQuestionCount).sum();

                if (weightSum == 0) {
                    throw new RuntimeException("Chapter weights / counts cannot all be zero.");
                }

                /*  containers  */
                List<QuestionMaster>       questions       = new ArrayList<>();
                List<ChapterMaster>        chapterMasters  = new ArrayList<>();

                int runningAllocated = 0;        // to keep final total exactly == totalQuestions

                for (int i = 0; i < createTestFromStudent.getChapters().size(); i++) {

                    StudentTestChaptersRequest chReq = createTestFromStudent.getChapters().get(i);
                    int chapterId  = chReq.getChapterId();
                    int weight     = chReq.getQuestionCount();          // weight / percentage / absolute

                    if (weight <= 0) continue;                          // skip zero-weight chapters
                    if (!validChapters.contains(chapterId)) {
                        throw new RuntimeException("Chapter ID " + chapterId + " is not valid for the selected subject.");
                    }

                    /*  how many questions should come from this chapter?   */
                    int planned = (int) Math.round((double) weight * totalQuestions / weightSum);

                    /*  ensure the last chapter absorbs rounding difference so grand-total == totalQuestions  */
                    if (i == createTestFromStudent.getChapters().size() - 1) {
                        planned = totalQuestions - runningAllocated;
                    }
                    runningAllocated += planned;

                    /*  fetch questions  */
                    List<QuestionMaster> chapterQuestions = questionRepository.questionsByChapterId(
                            entranceExamId, standardId, chapterId,
                            createTestFromStudent.getSubjectId(),
                            questionLevel, questionType,
                            createTestFromStudent.getAsked());

                    if (chapterQuestions.size() < planned) {
                        throw new Apierrorr("Not enough questions in chapter " + chapterRepository.findById(chapterId).get().getChapterName()
                                + ". Required " + planned + ", available " + chapterQuestions.size(),"404");
                    }

                    Collections.shuffle(chapterQuestions);

                    chapterQuestions = chapterQuestions.stream().limit(planned).collect(Collectors.toList());

                    /*  persist drafts  */
                    for (QuestionMaster q : chapterQuestions) {
                        TestQuestionDraft draft = new TestQuestionDraft();
                        draft.setQuestionId(q.getQuestionId());
                        draft.setEntranceExamId(q.getEntranceExamMaster().getEntranceExamId());
                        draft.setStandardId(q.getStandardMaster().getStandardId());
                        draft.setSubjectId(q.getSubjectMaster().getSubjectId());
                        draft.setChapterId(q.getChapterMaster().getChapterId());
                        draft.setTopicId(q.getTopicMaster().getTopicId());
                        draft.setQuestionCategory(q.getQuestionCategory());
                        draft.setUserId(student.getId());
                        draft.setQuestionLevelId(q.getQuestionLevel().getQuestionLevelId());
                        draft.setQuestionTypeId(q.getQuestionType().getQuestionTypeId());
                        draft.setPatternId(q.getPatternMaster().getPatternId());
                        draft.setYearOfAppearanceId(q.getYearOfAppearance().getYearOfAppearanceId());
                        draft.setTypeOfTest(testType);
                        testQuestionDraftRepository.save(draft);
                    }

                    /*  accumulate  */
                    questions.addAll(chapterQuestions);
                    chapterMasters.add(chapterRepository.findById(chapterId).get());
                }

                /*  ----------------  finish TestMaster  ---------------- */
                testMaster.setQuestionMasters(questions);
                testMaster.setChapterMasters(chapterMasters);
                testMaster.setCreatedBy(student);
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setCreatedDate(new Date());
                //testMaster.setEntranceExamMaster(student.getEntranceExamMasters().stream().findFirst().get());

                if (student.getEntranceExamMasters() != null && !student.getEntranceExamMasters().isEmpty()) {
                    EntranceExamMaster entranceExamMaster = student.getEntranceExamMasters().iterator().next();
                    testMaster.setEntranceExamMaster(entranceExamMaster);
                } else {
                    log.error("Student has no entrance exam assigned!");
                    mainResponse.setMessage("Student has no entrance exam assigned!");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                    return mainResponse;
                }

                testMaster.setStatus("Upcoming");
                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());
                testMaster.setTestName(createTestFromStudent.getTestName());

                /*  subject / standard lists  */
                testMaster.setSubjectMaster(Collections.singletonList(
                        subjectRepository.findById(createTestFromStudent.getSubjectId()).get()));
                testMaster.setStandardMaster(Collections.singletonList(
                        standardRepository.findById(standardId).get()));

                testRepository.save(testMaster);

                mainResponse.setMessage("Weightage Wise Test Created Successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }


//          ======================      Create Subject Wise Test        ============================

            else if (testType.equalsIgnoreCase("Create Subject Wise Test")) {
                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);
                log.info("  enter in Subject wise ");
                // Fetch necessary IDs
                Integer entranceExamId = studentManagementRepository.getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));
                log.info("  enter in Subject wise 2");
                System.out.println("ENTRANCE EXAM ID = " + entranceExamId);

                Integer standardId = studentManagementRepository.getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));

                System.out.println("Standard ID = " + standardId);

                Set<Integer> subjectIds = studentManagementRepository.getSubjectsByStudentIdEntranceExamIdStandardId(
                        student.getId(), entranceExamId, standardId);
                log.info("  enter in Subject wise 3");
                System.out.println("subjectIDS = " + subjectIds);

                if (!subjectIds.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID not valid for this student.");
                }
                log.info("  enter in Subject wise 4");
                // Validate and process chapters
                List<Integer> validChapters = chapterRepository.getChaptersByEntranceStandardSubject(
                        entranceExamId, standardId, createTestFromStudent.getSubjectId());

                System.out.println("VALID CHAPTERS = " + validChapters);
                log.info("  enter in Subject wise 5 ");

                List<QuestionMaster> questions = new ArrayList<>();
                List<ChapterMaster> chapterMasters = new ArrayList<>();
                for (StudentTestChaptersRequest chapter : createTestFromStudent.getChapters()) {
                    if(chapter.getQuestionCount()>0) {
                        Integer chapterId = chapter.getChapterId();
                        Integer questionsCount = chapter.getQuestionCount();

                        if (!validChapters.contains(chapterId)) {
                            log.info("  enter in Subject wise 6");
                            System.out.println("INVALID CHAPTER");
                            throw new RuntimeException("Chapter ID " + chapterId + " is not valid for the selected subject.");
                        }
                        log.info("  enter in Subject wise 7");
                        // Fetch and set limit to questions

                        ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId).get();
                        chapterMasters.add(chapterMaster);
                        List<QuestionMaster> chapterQuestions = questionRepository.questionsByChapterId(
                                entranceExamId, standardId, chapterId,
                                createTestFromStudent.getSubjectId(),
                                questionLevel, questionType,
                                createTestFromStudent.getAsked()
                        ).stream().collect(Collectors.toList());

                        Integer countChapterQuestions = chapterQuestions.size();
                        Integer limitQuestions = questionsCount;

                        System.out.println("countChapterQuestions = " + countChapterQuestions);
                        System.out.println("limitQuestions = " + limitQuestions);
                        log.info("  enter in Subject wise 8");
                        if (countChapterQuestions >= limitQuestions) {
                            log.info("  enter in Subject wise 9");
                            chapterQuestions = chapterQuestions.stream().limit(limitQuestions).collect(Collectors.toList());

                        } else {
                            log.info("  enter in Subject wise 10");
                            throw new RuntimeException("Requested questions exceed available questions. Available questions: " + countChapterQuestions);
                        }

                        for (QuestionMaster chapterQuestion : chapterQuestions) {
                            System.out.println("DRAFT 1");
                            QuestionMaster questionMaster = this.questionRepository.findById(chapterQuestion.getQuestionId()).get();

                            System.out.println("DRAFT 2 ");
                            TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                            System.out.println("DRAFT 3");
                            testQuestionDraft.setQuestionId(questionMaster.getQuestionId());
                            testQuestionDraft.setEntranceExamId(questionMaster.getEntranceExamMaster().getEntranceExamId());
                            testQuestionDraft.setStandardId(questionMaster.getStandardMaster().getStandardId());
                            testQuestionDraft.setSubjectId(questionMaster.getSubjectMaster().getSubjectId());
                            testQuestionDraft.setChapterId(questionMaster.getChapterMaster().getChapterId());

                            //testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());
                            if (questionMaster.getTopicMaster() != null)
                                testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());

                            testQuestionDraft.setQuestionCategory(questionMaster.getQuestionCategory());
                            testQuestionDraft.setUserId(student.getId());
                            //testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                            if (questionMaster.getQuestionLevel() != null)
                                testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());

                           // testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                            if (questionMaster.getQuestionType() != null)
                                testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());

                            //testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                            if (questionMaster.getPatternMaster() != null)
                                testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                            //testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());

                            if (questionMaster.getYearOfAppearance() != null)
                                testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                            testQuestionDraft.setTypeOfTest(createTestFromStudent.getTestType());
                            System.out.println("DRAFT 4");
                            this.testQuestionDraftRepository.save(testQuestionDraft);
                            System.out.println("DRAFT SAVE");

                        }

                        System.out.println("Subject QUESTIONS =---- " + chapterQuestions);

                        // Create and save test

                        log.info("  enter in Subject wise 11");

                        questions.addAll(chapterQuestions);
//                    testMaster.setQuestionMasters(chapterQuestions);
                    }
                }

                testMaster.setQuestionMasters(questions);

//                Double marks = questions.size() * createTestFromStudent.getMark();
//                testMaster.setMarks(marks);

                testMaster.setCreatedBy(student);
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setCreatedDate(new Date());
               // testMaster.setEntranceExamMaster(student.getEntranceExamMasters().stream().findFirst().get());

                if (student.getEntranceExamMasters() != null && !student.getEntranceExamMasters().isEmpty()) {
                    EntranceExamMaster entranceExamMaster = student.getEntranceExamMasters().iterator().next();
                    testMaster.setEntranceExamMaster(entranceExamMaster);
                } else {
                    log.error("Student has no entrance exam assigned!");
                    mainResponse.setMessage("Student has no entrance exam assigned!");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                    return mainResponse;
                }

                StandardMaster standardMaster = this.standardRepository.findById(standardId).get();
                List<StandardMaster> standardMasters = new ArrayList<>();
                standardMasters.add(standardMaster);
                testMaster.setStandardMaster(standardMasters);

                SubjectMaster subjectMaster = subjectRepository.findById(createTestFromStudent.getSubjectId()).get();
                List<SubjectMaster> subjectMasters = new ArrayList<>();
                subjectMasters.add(subjectMaster);
                testMaster.setSubjectMaster(subjectMasters);

                testMaster.setChapterMasters(chapterMasters);

                testMaster.setStatus("Upcoming");


                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());
                testMaster.setTestName(createTestFromStudent.getTestName());


                log.info("  enter in Subject wise 12");
                testRepository.save(testMaster);
                log.info("  enter in Subject wise 12");
                System.out.println("TEST CREATEd");

                System.out.println("Subject Wise Test Created Successfully");
                mainResponse.setMessage("Subject Wise Test Created Successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);

            }

            // ===============  Create Numerical Wise Test ===========

            else if (testType.equalsIgnoreCase("Create Numerical Wise Test")) {
                MainResponse resp = new MainResponse();
                resp.setId(student.getId());

                /* ----------  basic look-ups  ---------- */

                Integer entranceExamId = studentManagementRepository
                        .getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));

                Integer standardId = studentManagementRepository
                        .getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));

                Set<Integer> allowedSubjects = studentManagementRepository
                        .getSubjectsByStudentIdEntranceExamIdStandardId(student.getId(),
                                entranceExamId, standardId);

                if (!allowedSubjects.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID " + createTestFromStudent.getSubjectId()
                            + " not valid for this student.");
                }

                /* ----------  containers  ---------- */

                List<QuestionMaster>   questions        = new ArrayList<>();
                List<TestQuestionDraft>drafts           = new ArrayList<>();
                List<ChapterMaster>    chapterMasters   = new ArrayList<>();
                SubjectMaster          subjectMaster    =
                        subjectRepository.findById(createTestFromStudent.getSubjectId())
                                .orElseThrow(() -> new RuntimeException("Subject not found"));

                /* ----------  iterate over requested topics  ---------- */

                for (StudentTestTopicRequest tReq : createTestFromStudent.getTopics()) {

                    Integer topicId        = tReq.getTopicId();
                    int     limitQuestions = tReq.getQuestionCount();

                    if (limitQuestions <= 0) continue;              // ignore 0-count topics

                    // look up Topic + Chapter so we can validate chapter -> subject mapping
                    TopicMaster topicMaster = topicRepository.findById(topicId)
                            .orElseThrow(() -> new RuntimeException("Topic not found: " + topicId));

                    ChapterMaster chap = topicMaster.getChapterMaster();
                    if (chap == null)
                        throw new RuntimeException("Topic " + topicId + " has no chapter mapping");

                    // ensure chapter really belongs to the selected subject
                    if (!chap.getSubjectMaster().getSubjectId().equals(createTestFromStudent.getSubjectId())) {
                        throw new RuntimeException("Topic " + topicId + " is not part of subject "
                                + createTestFromStudent.getSubjectId());
                    }

                    /* ---- fetch questions for this topic ---- */
                    List<QuestionMaster> topicQs = questionRepository.questionsByTopicId(
                            entranceExamId,
                            standardId,
                            topicId,
                            createTestFromStudent.getSubjectId(),
                            questionLevel,
                            questionType,
                            createTestFromStudent.getAsked());

                    if (topicQs.size() < limitQuestions) {
                        throw new RuntimeException("Topic " + topicMaster.getTopicName()
                                + " has only " + topicQs.size() + " questions, requested "
                                + limitQuestions);
                    }

                    Collections.shuffle(topicQs);                   // optional randomness
                    topicQs = topicQs.subList(0, limitQuestions);

                    /* ---- persist draft rows ---- */
                    for (QuestionMaster q : topicQs) {
                        TestQuestionDraft d = new TestQuestionDraft();
                        d.setQuestionId(q.getQuestionId());
                        d.setEntranceExamId(q.getEntranceExamMaster().getEntranceExamId());
                        d.setStandardId(q.getStandardMaster().getStandardId());
                        d.setSubjectId(q.getSubjectMaster().getSubjectId());
                        d.setChapterId(q.getChapterMaster().getChapterId());
                        d.setTopicId(q.getTopicMaster().getTopicId());
                        d.setQuestionCategory(q.getQuestionCategory());
                        d.setUserId(student.getId());
                        d.setQuestionLevelId(q.getQuestionLevel().getQuestionLevelId());
                        d.setQuestionTypeId(q.getQuestionType().getQuestionTypeId());
                        d.setPatternId(q.getPatternMaster().getPatternId());
                        d.setYearOfAppearanceId(q.getYearOfAppearance().getYearOfAppearanceId());
                        d.setTypeOfTest(createTestFromStudent.getTestType());

                        drafts.add(d);      // batch save later
                    }

                    questions.addAll(topicQs);
                    chapterMasters.add(chap);
                }

                if (questions.isEmpty()) {
                    throw new RuntimeException("No questions found for any requested topic");
                }

                /* ----------  bulk save drafts  ---------- */
                testQuestionDraftRepository.saveAll(drafts);

                /* ----------  build & persist TestMaster  ---------- */

                TestMaster tm = new TestMaster();
                tm.setTypeOfTest(createTestFromStudent.getTestType());
                tm.setQuestionMasters(questions);
                tm.setChapterMasters(chapterMasters);
                tm.setSubjectMaster(Collections.singletonList(subjectMaster));
                tm.setStandardMaster(Collections.singletonList(
                        standardRepository.findById(standardId).get()));
                tm.setEntranceExamMaster(
                        entranceExamRepository.findById(entranceExamId).get());

                tm.setCreatedBy(student);
                tm.setCreatedDate(new Date());
                tm.setTestDate(createTestFromStudent.getTestDate());
                tm.setStartTime(createTestFromStudent.getStartTime());
                tm.setEndTime(createTestFromStudent.getEndTime());
                tm.setTestName(createTestFromStudent.getTestName());
                tm.setStatus("Upcoming");

                testRepository.save(tm);

                /* ----------  response  ---------- */
                resp.setFlag(true);
                resp.setResponseCode(HttpStatus.OK.value());
                resp.setMessage("Numerical Wise Test Created Successfully ("
                        + questions.size() + " questions)");

            }

            // ===============  Create Theoretical  Wise Test ===========

            else if (testType.equalsIgnoreCase("Create Theoretical Wise Test")) {
                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);
                log.info("  enter in Theoretical wise ");
                // Fetch necessary IDs
                Integer entranceExamId = studentManagementRepository.getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));
                log.info("  enter in Theoretical wise 2");
                System.out.println("ENTRANCE EXAM ID = " + entranceExamId);

                Integer standardId = studentManagementRepository.getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));

                System.out.println("Standard ID = " + standardId);

                Set<Integer> subjectIds = studentManagementRepository.getSubjectsByStudentIdEntranceExamIdStandardId(
                        student.getId(), entranceExamId, standardId);
                log.info("  enter in Theoretical wise 3");
                System.out.println("subjectIDS = " + subjectIds);

                if (!subjectIds.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID not valid for this student.");
                }
                log.info("  enter in Theoretical wise 4");
                // Validate and process chapters
                List<Integer> validChapters = chapterRepository.getChaptersByEntranceStandardSubject(
                        entranceExamId, standardId, createTestFromStudent.getSubjectId());

                System.out.println("VALID CHAPTERS = " + validChapters);
                log.info("  enter in Theoretical wise 5 ");

                List<QuestionMaster> questions = new ArrayList<>();
                List<ChapterMaster> chapterMasters = new ArrayList<>();
                for (StudentTestChaptersRequest chapter : createTestFromStudent.getChapters()) {
                    Integer chapterId = chapter.getChapterId();
                    Integer questionsCount = chapter.getQuestionCount();

                    if (!validChapters.contains(chapterId)) {
                        log.info("  enter in Theoretical wise 6");
                        System.out.println("INVALID CHAPTER");
                        throw new RuntimeException("Chapter ID " + chapterId + " is not valid for the selected subject.");
                    }
                    log.info("  enter in Theoretical wise 7");
                    // Fetch and set limit to questions

                    ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId).get();
                    chapterMasters.add(chapterMaster);
                    List<QuestionMaster> chapterQuestions = questionRepository.questionsByChapterId(
                            entranceExamId, standardId, chapterId,
                            createTestFromStudent.getSubjectId(),
                            questionLevel, questionType,
                            createTestFromStudent.getAsked()
                    ).stream().collect(Collectors.toList());

                    Integer countChapterQuestions = chapterQuestions.size();
                    Integer limitQuestions = questionsCount;

                    System.out.println("countChapterQuestions = " + countChapterQuestions);
                    System.out.println("limitQuestions = " + limitQuestions);
                    log.info("  enter in Theoretical wise 8");
                    if (countChapterQuestions >= limitQuestions) {
                        log.info("  enter in Theoretical wise 9");
                        chapterQuestions = chapterQuestions.stream().limit(limitQuestions).collect(Collectors.toList());
                    } else {
                        log.info("  enter in Theoretical wise 10");
                        throw new RuntimeException("Requested questions exceed available questions. Available questions: " + countChapterQuestions);
                    }

                    // GET Theoretical QUESTIONS ONLY
                    chapterQuestions = chapterQuestions.stream().filter(e -> e.getQuestionCategory().equalsIgnoreCase("Theoretical")).collect(Collectors.toList());

                    for (QuestionMaster chapterQuestion : chapterQuestions) {
                        System.out.println("DRAFT 1");
                        QuestionMaster questionMaster = this.questionRepository.findById(chapterQuestion.getQuestionId()).get();

                        System.out.println("DRAFT 2 ");
                        TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                        System.out.println("DRAFT 3");
                        testQuestionDraft.setQuestionId(questionMaster.getQuestionId());
                        testQuestionDraft.setEntranceExamId(questionMaster.getEntranceExamMaster().getEntranceExamId());
                        testQuestionDraft.setStandardId(questionMaster.getStandardMaster().getStandardId());
                        testQuestionDraft.setSubjectId(questionMaster.getSubjectMaster().getSubjectId());
                        testQuestionDraft.setChapterId(questionMaster.getChapterMaster().getChapterId());
                        //testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());
                        if (questionMaster.getTopicMaster() != null)
                            testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());
                        testQuestionDraft.setQuestionCategory(questionMaster.getQuestionCategory());
                        testQuestionDraft.setUserId(student.getId());
                        //testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                        if (questionMaster.getQuestionLevel() != null)
                            testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                        //testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                        if (questionMaster.getQuestionType() != null)
                            testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                        //testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                        if (questionMaster.getPatternMaster() != null)
                            testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                       // testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                        if (questionMaster.getYearOfAppearance() != null)
                            testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                        testQuestionDraft.setTypeOfTest(createTestFromStudent.getTestType());
                        System.out.println("DRAFT 4");
                        this.testQuestionDraftRepository.save(testQuestionDraft);
                        System.out.println("DRAFT SAVE");

                    }
                    System.out.println("Theoretical QUESTIONS =---- " + chapterQuestions);

                    // Create and save test

                    log.info("  enter in Theoretical wise 11");

                    questions.addAll(chapterQuestions);
//                    testMaster.setQuestionMasters(chapterQuestions);
                }

                testMaster.setQuestionMasters(questions);

//                Double marks = questions.size() * createTestFromStudent.getMark();
//                testMaster.setMarks(marks);

                testMaster.setCreatedBy(student);
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setCreatedDate(new Date());
                //testMaster.setEntranceExamMaster(student.getEntranceExamMasters().stream().findFirst().get());

                if (student.getEntranceExamMasters() != null && !student.getEntranceExamMasters().isEmpty()) {
                    EntranceExamMaster entranceExamMaster = student.getEntranceExamMasters().iterator().next();
                    testMaster.setEntranceExamMaster(entranceExamMaster);
                } else {
                    log.error("Student has no entrance exam assigned!");
                    mainResponse.setMessage("Student has no entrance exam assigned!");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                    return mainResponse;
                }

                StandardMaster standardMaster = this.standardRepository.findById(standardId).get();
                List<StandardMaster> standardMasters = new ArrayList<>();
                standardMasters.add(standardMaster);
                testMaster.setStandardMaster(standardMasters);

                SubjectMaster subjectMaster = subjectRepository.findById(createTestFromStudent.getSubjectId()).get();
                List<SubjectMaster> subjectMasters = new ArrayList<>();
                subjectMasters.add(subjectMaster);
                testMaster.setSubjectMaster(subjectMasters);

                testMaster.setChapterMasters(chapterMasters);

                testMaster.setStatus("Upcoming");


                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());
                testMaster.setTestName(createTestFromStudent.getTestName());

                log.info("  enter in Theoretical wise 12"+testMaster);
                testRepository.save(testMaster);
                log.info("  enter in Theoretical wise 13");
                System.out.println("Theoretical TEST CREATEd");

                System.out.println("Theoretical Wise Test Created Successfully");
                mainResponse.setMessage("Theoretical Wise Test Created Successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);

            }

//            =================     Create PYQ Wise Test    =======================
//            =====================================================================

            else if (testType.equalsIgnoreCase("Create PYQ Wise Test")) {
                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);
                log.info("  enter in PYQ wise ");


                // Fetch necessary IDs
                Integer entranceExamId = studentManagementRepository.getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));
                log.info("  enter in PYQ wise 2");
                System.out.println("ENTRANCE EXAM ID = " + entranceExamId);

                Integer standardId = studentManagementRepository.getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));

                System.out.println("Standard ID = " + standardId);

                Set<Integer> subjectIds = studentManagementRepository.getSubjectsByStudentIdEntranceExamIdStandardId(
                        student.getId(), entranceExamId, standardId);
                log.info("  enter in PYQ wise 3");
                System.out.println("subjectIDS = " + subjectIds);

                if (!subjectIds.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID not valid for this student.");
                }
                log.info("  enter in PYQ wise 4");
                // Validate and process chapters
                List<Integer> validChapters = chapterRepository.getChaptersByEntranceStandardSubject(
                        entranceExamId, standardId, createTestFromStudent.getSubjectId());

                System.out.println("VALID CHAPTERS = " + validChapters);
                log.info("  enter in PYQ wise 5 ");



                List<QuestionMaster> questions = new ArrayList<>();
                List<ChapterMaster> chapterMasters = new ArrayList<>();
                for (StudentTestChaptersRequest chapter : createTestFromStudent.getChapters()) {
                    Integer chapterId = chapter.getChapterId();
                    Integer questionsCount = chapter.getQuestionCount();

                    if (!validChapters.contains(chapterId)) {
                        log.info("  enter in PYQ wise 6");
                        System.out.println("INVALID CHAPTER");
                        throw new RuntimeException("Chapter ID " + chapterId + " is not valid for the selected subject.");
                    }
                    log.info("  enter in PYQ wise 7");
                    // Fetch and set limit to questions

                    ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId).get();
                    chapterMasters.add(chapterMaster);
                    List<QuestionMaster> chapterQuestions = questionRepository.questionsByChapterId(
                            entranceExamId, standardId, chapterId,
                            createTestFromStudent.getSubjectId(),
                            questionLevel, questionType,
                            createTestFromStudent.getAsked()
                    ).stream().collect(Collectors.toList());

                    Integer countChapterQuestions = chapterQuestions.size();
                    Integer limitQuestions = questionsCount;

                    System.out.println("countChapterQuestions = " + countChapterQuestions);
                    System.out.println("limitQuestions = " + limitQuestions);
                    log.info("  enter in PYQ wise 8");
                    if (countChapterQuestions >= limitQuestions) {
                        log.info("  enter in PYQ wise 9");
                        chapterQuestions = chapterQuestions.stream().limit(limitQuestions).collect(Collectors.toList());
                    } else {
                        log.info("  enter in PYQ wise 10");
                        throw new RuntimeException("Requested questions exceed available questions. Available questions: " + countChapterQuestions);
                    }

                    // GET PYQ QUESTIONS ONLY
                    chapterQuestions = chapterQuestions.stream().filter(e -> e.getAsked().equals(true)).collect(Collectors.toList());

                    for (QuestionMaster chapterQuestion : chapterQuestions) {
                        System.out.println("DRAFT 1");
                        QuestionMaster questionMaster = this.questionRepository.findById(chapterQuestion.getQuestionId()).get();

                        System.out.println("DRAFT 2 ");
                        TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                        System.out.println("DRAFT 3");
                        testQuestionDraft.setQuestionId(questionMaster.getQuestionId());
                        testQuestionDraft.setEntranceExamId(questionMaster.getEntranceExamMaster().getEntranceExamId());
                        testQuestionDraft.setStandardId(questionMaster.getStandardMaster().getStandardId());
                        testQuestionDraft.setSubjectId(questionMaster.getSubjectMaster().getSubjectId());
                        testQuestionDraft.setChapterId(questionMaster.getChapterMaster().getChapterId());
                       // testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());
                        if (questionMaster.getTopicMaster() != null)
                            testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());

                        testQuestionDraft.setQuestionCategory(questionMaster.getQuestionCategory());
                        testQuestionDraft.setUserId(student.getId());
                        //testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                        if (questionMaster.getQuestionLevel() != null)
                            testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                      //  testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                        if (questionMaster.getQuestionType() != null)
                            testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                        //testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                        if (questionMaster.getPatternMaster() != null)
                            testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                       // testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                        if (questionMaster.getYearOfAppearance() != null)
                            testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                        testQuestionDraft.setTypeOfTest(createTestFromStudent.getTestType());
                        System.out.println("DRAFT 4");
                        this.testQuestionDraftRepository.save(testQuestionDraft);
                        System.out.println("DRAFT SAVE");

                    }
                    System.out.println("PYQ QUESTIONS =---- " + chapterQuestions);

                    // Create and save test

                    log.info("  enter in PYQ wise 11");

                    questions.addAll(chapterQuestions);
//                    testMaster.setQuestionMasters(chapterQuestions);
                }

                testMaster.setQuestionMasters(questions);

//                Double marks = questions.size() * createTestFromStudent.getMark();
//                testMaster.setMarks(marks);

                testMaster.setCreatedBy(student);
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setCreatedDate(new Date());
                //testMaster.setEntranceExamMaster(student.getEntranceExamMasters().stream().findFirst().get());
                if (student.getEntranceExamMasters() != null && !student.getEntranceExamMasters().isEmpty()) {
                    EntranceExamMaster entranceExamMaster = student.getEntranceExamMasters().iterator().next();
                    testMaster.setEntranceExamMaster(entranceExamMaster);
                } else {
                    log.error("Student has no entrance exam assigned!");
                    mainResponse.setMessage("Student has no entrance exam assigned!");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                    return mainResponse;
                }

                StandardMaster standardMaster = this.standardRepository.findById(standardId).get();
                List<StandardMaster> standardMasters = new ArrayList<>();
                standardMasters.add(standardMaster);
                testMaster.setStandardMaster(standardMasters);

                SubjectMaster subjectMaster = subjectRepository.findById(createTestFromStudent.getSubjectId()).get();
                List<SubjectMaster> subjectMasters = new ArrayList<>();
                subjectMasters.add(subjectMaster);
                testMaster.setSubjectMaster(subjectMasters);

                testMaster.setChapterMasters(chapterMasters);

                testMaster.setStatus("Upcoming");

                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());
                testMaster.setTestName(createTestFromStudent.getTestName());

                log.info("  enter in PYQ wise 12");
                testRepository.save(testMaster);
                log.info("  enter in PYQ wise 13");
                System.out.println("PYQ TEST CREATEd");

                System.out.println("PYQ Wise Test Created Successfully");
                mainResponse.setMessage("PYQ Wise Test Created Successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);

            }

//            ======================        Create Random Test      ====================

            if (testType.equalsIgnoreCase("Create Random Test")) {
                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);
                log.info("  enter in Random wise ");
                // Fetch necessary IDs
                Integer entranceExamId = studentManagementRepository.getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));
                log.info("  enter in Random wise 2");
                System.out.println("ENTRANCE EXAM ID = " + entranceExamId);

                Integer standardId = studentManagementRepository.getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));

                System.out.println("Standard ID = " + standardId);

                Set<Integer> subjectIds = studentManagementRepository.getSubjectsByStudentIdEntranceExamIdStandardId(
                        student.getId(), entranceExamId, standardId);
                log.info("  enter in Random wise 3");
                System.out.println("subjectIDS = " + subjectIds);

                if (!subjectIds.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID not valid for this student.");
                }
                log.info("  enter in Random wise 4");
                // Validate and process chapters
                List<Integer> validChapters = chapterRepository.getChaptersByEntranceStandardSubject(
                        entranceExamId, standardId, createTestFromStudent.getSubjectId());

                System.out.println("VALID CHAPTERS = " + validChapters);

                log.info("  enter in Random wise 5 ");

                List<QuestionMaster> questions = new ArrayList<>();
                List<ChapterMaster> chapterMasters = new ArrayList<>();

                for (StudentTestChaptersRequest chapter : createTestFromStudent.getChapters()) {
                    Integer chapterId = chapter.getChapterId();
                    Integer questionsCount = chapter.getQuestionCount();

                    if (!validChapters.contains(chapterId)) {
                        log.info("  enter in Random wise 6");
                        System.out.println("INVALID CHAPTER");
                        throw new RuntimeException("Chapter ID " + chapterId + " is not valid for the selected subject.");
                    }
                    log.info("  enter in Random wise 7");

                    ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId).get();
                    chapterMasters.add(chapterMaster);
                    // Fetch and set limit to questions
                    List<QuestionMaster> chapterQuestions = questionRepository.questionsByChapterId(
                            entranceExamId, standardId, chapterId,
                            createTestFromStudent.getSubjectId(),
                            questionLevel, questionType,
                            createTestFromStudent.getAsked()
                    ).stream().collect(Collectors.toList());

                    Integer countChapterQuestions = chapterQuestions.size();
                    Integer limitQuestions = questionsCount;

                    System.out.println("countChapterQuestions = " + countChapterQuestions);
                    System.out.println("limitQuestions = " + limitQuestions);
                    log.info("  enter in Random wise 8");
                    if (countChapterQuestions >= limitQuestions) {
                        log.info("  enter in Random wise 9");
                        chapterQuestions = chapterQuestions.stream().limit(limitQuestions).collect(Collectors.toList());
                    } else {
                        log.info("  enter in Random wise 10");
                        throw new RuntimeException("Requested questions exceed available questions. Available questions: " + countChapterQuestions);
                    }

                    for (QuestionMaster chapterQuestion : chapterQuestions) {
                        System.out.println("DRAFT 1");
                        QuestionMaster questionMaster = this.questionRepository.findById(chapterQuestion.getQuestionId()).get();

                        System.out.println("DRAFT 2 ");
                        TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                        System.out.println("DRAFT 3");
                        testQuestionDraft.setQuestionId(questionMaster.getQuestionId());
                        testQuestionDraft.setEntranceExamId(questionMaster.getEntranceExamMaster().getEntranceExamId());
                        testQuestionDraft.setStandardId(questionMaster.getStandardMaster().getStandardId());
                        testQuestionDraft.setSubjectId(questionMaster.getSubjectMaster().getSubjectId());
                        testQuestionDraft.setChapterId(questionMaster.getChapterMaster().getChapterId());
                        //testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());
                        if (questionMaster.getTopicMaster() != null)
                            testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());

                        testQuestionDraft.setQuestionCategory(questionMaster.getQuestionCategory());
                        testQuestionDraft.setUserId(student.getId());
                        //testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                        if (questionMaster.getQuestionLevel() != null)
                            testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());

                       // testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                        if (questionMaster.getQuestionType() != null)
                            testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());

                      //  testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());

                        if (questionMaster.getPatternMaster() != null)
                            testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());

                        //testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                        if (questionMaster.getYearOfAppearance() != null)
                            testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());

                        testQuestionDraft.setTypeOfTest(createTestFromStudent.getTestType());
                        System.out.println("DRAFT 4");
                        this.testQuestionDraftRepository.save(testQuestionDraft);
                        System.out.println("DRAFT SAVE");

                    }

                    System.out.println("Random QUESTIONS =---- " + chapterQuestions);

                    // Create and save test

                    log.info("  enter in Random wise 11");

                    questions.addAll(chapterQuestions);
//                    testMaster.setQuestionMasters(chapterQuestions);
                }

                testMaster.setQuestionMasters(questions);

//                Double marks = questions.size() * createTestFromStudent.getMark();
//                testMaster.setMarks(marks);

                testMaster.setCreatedBy(student);
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setCreatedDate(new Date());
                //testMaster.setEntranceExamMaster(student.getEntranceExamMasters().stream().findFirst().get());
                if (student.getEntranceExamMasters() != null && !student.getEntranceExamMasters().isEmpty()) {
                    EntranceExamMaster entranceExamMaster = student.getEntranceExamMasters().iterator().next();
                    testMaster.setEntranceExamMaster(entranceExamMaster);
                } else {
                    log.error("Student has no entrance exam assigned!");
                    mainResponse.setMessage("Student has no entrance exam assigned!");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                    return mainResponse;
                }

                StandardMaster standardMaster = this.standardRepository.findById(standardId).get();
                List<StandardMaster> standardMasters = new ArrayList<>();
                standardMasters.add(standardMaster);
                testMaster.setStandardMaster(standardMasters);

                SubjectMaster subjectMaster = subjectRepository.findById(createTestFromStudent.getSubjectId()).get();
                List<SubjectMaster> subjectMasters = new ArrayList<>();
                subjectMasters.add(subjectMaster);
                testMaster.setSubjectMaster(subjectMasters);

                testMaster.setChapterMasters(chapterMasters);

                testMaster.setStatus("Upcoming");


                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());
                testMaster.setTestName(createTestFromStudent.getTestName());


                log.info("  enter in Random wise 12");
                testRepository.save(testMaster);
                log.info("  enter in Random wise 12");
                System.out.println("Random CREATEd");

                System.out.println("Random Wise Test Created Successfully");
                mainResponse.setMessage("Random Wise Test Created Successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);

            }

            ///Group wise test /////////////////////////////////////////
            if (testType.equalsIgnoreCase("Create Group Wise Test")) {

                // Debugging to ensure that the required IDs are not null
                if (student.getId() == null) {
                    throw new Apierrorr("Student ID cannot be null","400");
                            //IllegalArgumentException("Student ID cannot be null");
                }

                System.out.println("REQUEST = " + createTestFromStudent.toString());
                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);

                Integer entranceExamId = studentManagementRepository.getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new Apierrorr("Entrance exam ID not found","404"));
                                //RuntimeException("Entrance Exam ID not found"));
                log.info("Entrance Exam ID: " + entranceExamId);

                Integer standardId = studentManagementRepository.getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new Apierrorr("Standard ID not found","404"));
                                //RuntimeException("Standard ID not found"));
                log.info("Standard ID: " + standardId);

                Set<Integer> subjectIds = studentManagementRepository.getSubjectsByStudentIdEntranceExamIdStandardId(
                        student.getId(), entranceExamId, standardId);
                log.info("Subject IDs: " + subjectIds);

                if (subjectIds == null || subjectIds.isEmpty()) {
                    throw new Apierrorr("No subjects found for the student.","400");
                            //RuntimeException("No subjects found for the student.");
                }

                List<SubjectMaster> subjectMasters = new ArrayList<>();
                List<ChapterMaster> chapterMasters = new ArrayList<>();
                List<StandardMaster> standardMasters = new ArrayList<>();
                List<EntranceExamMaster> entranceExamMasters = new ArrayList<>();
                List<QuestionMaster> allQuestions = new ArrayList<>();

                Double totalMarks = 0.0; // Variable to calculate total marks dynamically

                for (StudentTestSubjectsRequest groupSubject : createTestFromStudent.getGroupSubjects()) {
                    if (subjectIds.contains(groupSubject.getSubjectId())) {
                        List<Integer> chapters = chapterRepository.getChaptersByEntranceStandardSubject(
                                entranceExamId, standardId, groupSubject.getSubjectId());
                           boolean subjectCount = false;
                        for (StudentTestChaptersRequest chapter : groupSubject.getChapters()) {
                            if (chapter.getChapterId() == null) {
                                throw new Apierrorr("Chapter ID in group subject cannot be null","400");
                                        //IllegalArgumentException("Chapter ID in group subject cannot be null");
                            }
                            if(chapter.getQuestionCount()==0){
                                continue;
                            }
                            subjectCount =true;
                            if (chapters.contains(chapter.getChapterId())) {
                                List<QuestionMaster> chapterQuestions = questionRepository.questionsByChapterId(
                                        entranceExamId, standardId, chapter.getChapterId(),
                                        groupSubject.getSubjectId(), questionLevel, questionType,
                                        createTestFromStudent.getAsked());

                                Integer chapterId = chapter.getChapterId();
                                Integer countChapterQuestions = chapterQuestions.size();
                                Integer limitQuestions = chapter.getQuestionCount();

                                System.out.println("SUBJECT ID = " + groupSubject.getSubjectId());
                                System.out.println("Chapter Id = " + chapterId);
                                System.out.println("Available Questions: " + countChapterQuestions);
                                System.out.println("Limit Questions: " + limitQuestions);

                                if (countChapterQuestions >= limitQuestions) {
                                    chapterQuestions = chapterQuestions.stream().limit(limitQuestions).collect(Collectors.toList());
                                } else {
                                    throw new Apierrorr("Requested questions exceed available questions. Available questions: " + countChapterQuestions+" ","400");
                                            //RuntimeException("Requested questions exceed available questions. Available questions: " + countChapterQuestions);
                                }

                                for (QuestionMaster chapterQuestion : chapterQuestions) {
                                    System.out.println("DRAFT 1");
                                    QuestionMaster questionMaster = this.questionRepository.findById(chapterQuestion.getQuestionId()).get();

                                    System.out.println("DRAFT 2 ");
                                    TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                                    System.out.println("DRAFT 3");
                                    testQuestionDraft.setQuestionId(questionMaster.getQuestionId());
                                    testQuestionDraft.setEntranceExamId(questionMaster.getEntranceExamMaster().getEntranceExamId());
                                    testQuestionDraft.setStandardId(questionMaster.getStandardMaster().getStandardId());
                                    testQuestionDraft.setSubjectId(questionMaster.getSubjectMaster().getSubjectId());
                                    testQuestionDraft.setChapterId(questionMaster.getChapterMaster().getChapterId());
                                   // testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());
                                    if (questionMaster.getTopicMaster() != null)
                                        testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());

                                    testQuestionDraft.setQuestionCategory(questionMaster.getQuestionCategory());
                                    testQuestionDraft.setUserId(student.getId());

                                   // testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                                    if (questionMaster.getQuestionLevel() != null)
                                        testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());

                                   // testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                                    //testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                                    ///testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                                    if (questionMaster.getQuestionType() != null)
                                        testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());

                                    if (questionMaster.getPatternMaster() != null)
                                        testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());

                                    if (questionMaster.getYearOfAppearance() != null)
                                        testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                                    testQuestionDraft.setTypeOfTest(createTestFromStudent.getTestType());
                                    System.out.println("DRAFT 4");
                                    this.testQuestionDraftRepository.save(testQuestionDraft);
                                    System.out.println("DRAFT SAVE");

                                }

                                allQuestions.addAll(chapterQuestions);

                                ChapterMaster chapterMaster = chapterRepository.findById(chapter.getChapterId())
                                        .orElseThrow(() -> new Apierrorr("Chapter not found with ID: " + chapter.getChapterId(),"404"));
                                                //RuntimeException("Chapter not found with ID: " + chapter.getChapterId()));
                                chapterMasters.add(chapterMaster);
                            }
                        }
                        if(!subjectCount){
                            continue;
                        }

                        SubjectMaster subjectMaster = subjectRepository.findById(groupSubject.getSubjectId())
                                .orElseThrow(() -> new Apierrorr("Subject not found with ID: " + groupSubject.getSubjectId(),"404"));
                                        //RuntimeException("Subject not found with ID: " + groupSubject.getSubjectId()));
                        subjectMasters.add(subjectMaster);
                    }
                }

                testMaster.setQuestionMasters(allQuestions);
//                Double marks = allQuestions.size() * createTestFromStudent.getMark();
//                testMaster.setMarks(marks); // Set calculated marks dynamically

                testMaster.setCreatedBy(student);
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setCreatedDate(new Date());

                EntranceExamMaster entranceExamMaster = student.getEntranceExamMasters().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam Master not found."));
                testMaster.setEntranceExamMaster(entranceExamMaster);

                StandardMaster standardMaster = standardRepository.findById(standardId)
                        .orElseThrow(() -> new RuntimeException("Standard not found with ID: " + standardId));
                standardMasters.add(standardMaster);
                testMaster.setStandardMaster(standardMasters);

                testMaster.setSubjectMaster(subjectMasters);
                testMaster.setChapterMasters(chapterMasters);

                testMaster.setStatus("Upcoming");
                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());
                testMaster.setTestName(createTestFromStudent.getTestName());

                testRepository.save(testMaster);
                log.info("Group Wise Test Created Successfully");

                mainResponse.setMessage("Group Wise Test Created Successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }


//            ======================        Create Topic Wise Test      ==========================
//            log.debug("Payload groupSubjects: {}", createTestFromStudent.getGroupSubjects());
//            log.debug("Payload topics:        {}",  createTestFromStudent.getTopics());


            if (testType.equalsIgnoreCase("Create Topic Wise Test")) {
                // 1️⃣ Prepare response, student & testMaster
                mainResponse.setId(createTestFromStudent.getId());
                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);

                // 2️⃣ Fetch and validate exam, standard, subject
                Integer entranceExamId = studentManagementRepository
                        .getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));

                Integer standardId = studentManagementRepository
                        .getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));

                Set<Integer> allowedSubjects = studentManagementRepository
                        .getSubjectsByStudentIdEntranceExamIdStandardId(student.getId(), entranceExamId, standardId);

                if (!allowedSubjects.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID not valid for this student.");
                }

                // 3️⃣ Containers for everything we’ll save
                List<QuestionMaster>   allQuestions      = new ArrayList<>();
                List<TestQuestionDraft> allDrafts         = new ArrayList<>();
                List<ChapterMaster>    chapterMasters    = new ArrayList<>();
                SubjectMaster          subjectMaster     = subjectRepository
                        .findById(createTestFromStudent.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("Subject not found"));
                EntranceExamMaster     examMaster        = student.getEntranceExamMasters()
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam Master not found"));

                // 4️⃣ Loop over each requested topic
                for (StudentTestTopicRequest topicReq : createTestFromStudent.getTopics()) {
                    int topicId        = topicReq.getTopicId();
                    int limitQuestions = topicReq.getQuestionCount();
                    if (limitQuestions <= 0) continue;

                    // Fetch the topic (and its chapter)
                    TopicMaster topicMaster = topicRepository.findById(topicId)
                            .orElseThrow(() -> new RuntimeException("Topic not found: " + topicId));
                    ChapterMaster chap = topicMaster.getChapterMaster();
                    if (chap == null || !chap.getSubjectMaster().getSubjectId()
                            .equals(createTestFromStudent.getSubjectId())) {
                        throw new RuntimeException("Topic " + topicId
                                + " is not part of subject " + createTestFromStudent.getSubjectId());
                    }

                    // 5️⃣ Load questions via new repo method
                    List<QuestionMaster> topicQs = questionRepository.questionsByTopicId(
                            entranceExamId,
                            standardId,
                            topicId,
                            createTestFromStudent.getSubjectId(),
                            questionLevel,
                            questionType,
                            createTestFromStudent.getAsked()
                    );
                    if (topicQs.size() < limitQuestions) {
                        throw new RuntimeException("Topic " + topicMaster.getTopicName()
                                + " has only " + topicQs.size() + " questions; requested "
                                + limitQuestions);
                    }
                    Collections.shuffle(topicQs);
                    topicQs = topicQs.subList(0, limitQuestions);

                    // 6️⃣ Persist drafts
                    for (QuestionMaster q : topicQs) {
                        TestQuestionDraft d = new TestQuestionDraft();
                        d.setQuestionId(q.getQuestionId());
                        d.setEntranceExamId(q.getEntranceExamMaster().getEntranceExamId());
                        d.setStandardId(q.getStandardMaster().getStandardId());
                        d.setSubjectId(q.getSubjectMaster().getSubjectId());
                        d.setChapterId(q.getChapterMaster().getChapterId());
                        d.setTopicId(q.getTopicMaster().getTopicId());
                        d.setQuestionCategory(q.getQuestionCategory());
                        d.setUserId(student.getId());
                        d.setQuestionLevelId(q.getQuestionLevel().getQuestionLevelId());
                        d.setQuestionTypeId(q.getQuestionType().getQuestionTypeId());
                        d.setPatternId(q.getPatternMaster().getPatternId());
                        d.setYearOfAppearanceId(q.getYearOfAppearance().getYearOfAppearanceId());
                        d.setTypeOfTest(testType);
                        allDrafts.add(d);
                    }

                    // 7️⃣ Accumulate for TestMaster
                    allQuestions.addAll(topicQs);
                    chapterMasters.add(chap);
                }

                // 8️⃣ Guard against zero questions
                if (allQuestions.isEmpty()) {
                    throw new RuntimeException("No questions generated for any requested topic.");
                }

                // 9️⃣ Save all drafts in batch
                testQuestionDraftRepository.saveAll(allDrafts);

                // 🔟 Build TestMaster & save
                testMaster.setQuestionMasters(allQuestions);
                testMaster.setChapterMasters(chapterMasters);
                testMaster.setSubjectMaster(Collections.singletonList(subjectMaster));
                testMaster.setStandardMaster(Collections.singletonList(
                        standardRepository.findById(standardId).get()));
                testMaster.setEntranceExamMaster(examMaster);
                testMaster.setCreatedBy(student);
                testMaster.setCreatedDate(new Date());
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());
                testMaster.setTestName(createTestFromStudent.getTestName());
                testMaster.setStatus("Upcoming");
                testRepository.save(testMaster);

                // 11️⃣ Return success
                mainResponse.setMessage("Topic Wise Test Created Successfully ("
                        + allQuestions.size() + " questions)");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }

            //   LEVEL WISE TEST CREATION=============

            if (testType.equalsIgnoreCase("Level Wise Test")) {

                // Validate Student ID
                if (student.getId() == null) {
                    throw new IllegalArgumentException("Student ID cannot be null");
                }

                System.out.println("REQUEST = " + createTestFromStudent.toString());
                TestMaster testMaster = new TestMaster();
                testMaster.setTypeOfTest(testType);

                // Fetch Entrance Exam ID
                Integer entranceExamId = studentManagementRepository.getStudentEntranceExamById(student.getId())
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Entrance Exam ID not found"));
                log.info("Entrance Exam ID: " + entranceExamId);

                // Fetch Standard ID
                Integer standardId = studentManagementRepository.getStudentIdAndEntranceExamIdWiseStandard(student.getId(), entranceExamId)
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("Standard ID not found"));
                log.info("Standard ID: " + standardId);

                // Fetch Subject IDs
                Set<Integer> subjectIds = studentManagementRepository.getSubjectsByStudentIdEntranceExamIdStandardId(
                        student.getId(), entranceExamId, standardId);
                log.info("Subject IDs: " + subjectIds);

                // Validate Subject ID
                if (!subjectIds.contains(createTestFromStudent.getSubjectId())) {
                    throw new RuntimeException("Subject ID not valid for this student.");
                }

                // Validate and Process Chapters
                List<Integer> validChapters = chapterRepository.getChaptersByEntranceStandardSubject(
                        entranceExamId, standardId, createTestFromStudent.getSubjectId());
                log.info("Valid Chapters: " + validChapters);

                List<QuestionMaster> chapterQuestions = new ArrayList<>();
                List<QuestionMaster> questionMasterHardList = new ArrayList<>();
                List<QuestionMaster> questionMasterEasyList = new ArrayList<>();
                List<QuestionMaster> questionMasterMediumList = new ArrayList<>();
                List<ChapterMaster> chapterMasters = new ArrayList<>();

                Integer easyQuestions = createTestFromStudent.getLevelWise().getEasy();
                Integer mediumQuestions = createTestFromStudent.getLevelWise().getMedium();
                Integer hardQuestions = createTestFromStudent.getLevelWise().getHard();

                for (Integer chapterId : createTestFromStudent.getSelectedChapterIds()) {

                    if (!validChapters.contains(chapterId)) {
                        throw new RuntimeException("Chapter ID " + chapterId + " is not valid for the selected subject.");
                    }

                    ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId).get();
                    chapterMasters.add(chapterMaster);

                    QuestionLevel easy = questionLevelRepository.findByQuestionLevel("Easy");
                    QuestionLevel medium = questionLevelRepository.findByQuestionLevel("Medium");
                    QuestionLevel hard = questionLevelRepository.findByQuestionLevel("Hard");

                    // Fetch questions by level
                    questionMasterHardList.addAll(questionRepository.questionsByChapterId(
                            entranceExamId, standardId, chapterId, createTestFromStudent.getSubjectId(),
                            hard.getQuestionLevelId(), questionType, createTestFromStudent.getAsked()));

                    questionMasterEasyList.addAll(questionRepository.questionsByChapterId(
                            entranceExamId, standardId, chapterId, createTestFromStudent.getSubjectId(),
                            easy.getQuestionLevelId(), questionType, createTestFromStudent.getAsked()));

                    questionMasterMediumList.addAll(questionRepository.questionsByChapterId(
                            entranceExamId, standardId, chapterId, createTestFromStudent.getSubjectId(),
                            medium.getQuestionLevelId(), questionType, createTestFromStudent.getAsked()));
                }

                chapterQuestions.addAll(questionMasterEasyList.stream().limit(easyQuestions).collect(Collectors.toList()));
                chapterQuestions.addAll(questionMasterMediumList.stream().limit(mediumQuestions).collect(Collectors.toList()));
                chapterQuestions.addAll(questionMasterHardList.stream().limit(hardQuestions).collect(Collectors.toList()));

                for (QuestionMaster chapterQuestion : chapterQuestions) {

                    QuestionMaster questionMaster = this.questionRepository.findById(chapterQuestion.getQuestionId()).get();

                    TestQuestionDraft testQuestionDraft = new TestQuestionDraft();
                    testQuestionDraft.setQuestionId(questionMaster.getQuestionId());
                    testQuestionDraft.setEntranceExamId(questionMaster.getEntranceExamMaster().getEntranceExamId());
                    testQuestionDraft.setStandardId(questionMaster.getStandardMaster().getStandardId());
                    testQuestionDraft.setSubjectId(questionMaster.getSubjectMaster().getSubjectId());
                    testQuestionDraft.setChapterId(questionMaster.getChapterMaster().getChapterId());
                    testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());
                    if (questionMaster.getTopicMaster() != null)
                        testQuestionDraft.setTopicId(questionMaster.getTopicMaster().getTopicId());

                    testQuestionDraft.setQuestionCategory(questionMaster.getQuestionCategory());
                    testQuestionDraft.setUserId(student.getId());
                    //testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                    //testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());
                    //testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());
                    //testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());
                    if (questionMaster.getQuestionLevel() != null)
                        testQuestionDraft.setQuestionLevelId(questionMaster.getQuestionLevel().getQuestionLevelId());
                    if (questionMaster.getQuestionType() != null)
                        testQuestionDraft.setQuestionTypeId(questionMaster.getQuestionType().getQuestionTypeId());

                    if (questionMaster.getPatternMaster() != null)
                        testQuestionDraft.setPatternId(questionMaster.getPatternMaster().getPatternId());

                    if (questionMaster.getYearOfAppearance() != null)
                        testQuestionDraft.setYearOfAppearanceId(questionMaster.getYearOfAppearance().getYearOfAppearanceId());

                    testQuestionDraft.setTypeOfTest(createTestFromStudent.getTestType());

                    this.testQuestionDraftRepository.save(testQuestionDraft);
                    System.out.println("DRAFT SAVE");
                }

                // Set Chapters in TestMaster
                testMaster.setChapterMasters(chapterMasters);

                // Set other details in TestMaster
//                testMaster.setMarks(chapterQuestions.size() * createTestFromStudent.getMark());

                testMaster.setQuestionMasters(chapterQuestions);
                testMaster.setTestDate(createTestFromStudent.getTestDate());
                testMaster.setCreatedBy(student);
                testMaster.setTestName(createTestFromStudent.getTestName());
                testMaster.setStatus("Upcoming");
                testMaster.setCreatedDate(new Date());

                EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(entranceExamId).get();
                testMaster.setEntranceExamMaster(entranceExamMaster);
                testMaster.setStartTime(createTestFromStudent.getStartTime());
                testMaster.setEndTime(createTestFromStudent.getEndTime());

                List<StandardMaster> standardMasters = new ArrayList<>();
                StandardMaster standardMaster = this.standardRepository.findById(standardId).get();
                standardMasters.add(standardMaster);
                testMaster.setStandardMaster(standardMasters);

                List<SubjectMaster> subjectMasters = new ArrayList<>();
                SubjectMaster subjectMaster = this.subjectRepository.findById(createTestFromStudent.getSubjectId()).get();
                subjectMasters.add(subjectMaster);
                testMaster.setSubjectMaster(subjectMasters);

                this.testRepository.save(testMaster);

                System.out.println("Level Wise Test Created Successfully");
                mainResponse.setMessage("Level Wise Test Created Successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            }



        } catch (RuntimeException ex) {
            mainResponse.setMessage(ex.getMessage());
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            mainResponse.setMessage("An unexpected error occurred during test creation");
            mainResponse.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            mainResponse.setFlag(false);
        }

        System.out.println("  main response =" + mainResponse.toString());
        return mainResponse;
    }


    @Autowired
    private TemplateEngine templateEngine;
    public byte[] generatePdf(List<QuestionDTO> questionList) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Adding Title: "Physical World And Measurement"
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Physical World And Measurement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n")); // Space after title
            Context context = new Context();
            context.setVariable("questions", questionList);
            String htmlContent = templateEngine.process("question-answer-template", context)
                    .replace("&nbsp;", "&#160;");
            addHtmlToPdf(document, writer, htmlContent);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    @Transactional
    public MainResponse createQuestionSets(TestRequestSetWise req) {

        MainResponse main = new MainResponse();

        /* ── 1. find teacher ─────────────────────────────────────────── */
        User teacher = userRepository.findById(req.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Teacher not found."));

        /* ── 2. fetch or create TestMaster ───────────────────────────── */
        TestMaster test = Optional.ofNullable(req.getTestId())
                .flatMap(testRepository::findById)
                .orElse(new TestMaster());

        BeanUtils.copyProperties(req, test);

        /* ── 3. resolve ID-sets from the request (can be null) ───────── */
        Set<StandardMaster> fromIdsStandards = new HashSet<>();
        if (req.getStandardId() != null) {
            req.getStandardId().stream()
                    .filter(id -> id != null && id > 0)
                    .forEach(id -> standardRepository.findById(id)
                            .ifPresent(fromIdsStandards::add));
        }

        Set<SubjectMaster>  fromIdsSubjects  = new HashSet<>();
        if (req.getSubjectId() != null) {
            req.getSubjectId().stream()
                    .filter(id -> id != null && id > 0)
                    .forEach(id -> subjectRepository.findById(id)
                            .ifPresent(fromIdsSubjects::add));
        }

        Set<ChapterMaster>  fromIdsChapters  = new HashSet<>();
        if (req.getChapterMasters() != null) {
            req.getChapterMasters().stream()
                    .filter(id -> id != null && id > 0)
                    .forEach(id -> chapterRepository.findById(id)
                            .ifPresent(fromIdsChapters::add));
        }

        Set<TopicMaster>    fromIdsTopics    = new HashSet<>();
        if (req.getTopicMasters() != null) {
            req.getTopicMasters().stream()
                    .filter(id -> id != null && id > 0)
                    .forEach(id -> topicRepository.findById(id)
                            .ifPresent(fromIdsTopics::add));
        }

        /* ── 4. start from whatever is already on the TestMaster ─────── */
        Set<StandardMaster> std   = test.getStandardMaster() != null
                ? new HashSet<>(test.getStandardMaster()) : new HashSet<>();
        Set<SubjectMaster>  sub   = test.getSubjectMaster() != null
                ? new HashSet<>(test.getSubjectMaster())  : new HashSet<>();
        Set<ChapterMaster>  chap  = test.getChapterMasters() != null
                ? new HashSet<>(test.getChapterMasters()) : new HashSet<>();
        Set<TopicMaster>    top   = test.getTopicMasters() != null
                ? new HashSet<>(test.getTopicMasters())   : new HashSet<>();

        /* ── 5. add the sets resolved from IDs ───────────────────────── */
        std .addAll(fromIdsStandards);
        sub .addAll(fromIdsSubjects);
        chap.addAll(fromIdsChapters);
        top .addAll(fromIdsTopics);

        /* ── 6. loop exam-sets and collect further dimensions ────────── */
        List<ExamSet> examSets = new ArrayList<>();
        List<QuestionMaster> allQuestions = test.getQuestionMasters() != null
                ? new ArrayList<>(test.getQuestionMasters()) : new ArrayList<>();
        List<Integer> allPickedIds = new ArrayList<>();

        for (ExamSetRequest esReq : req.getExamSetRequests()) {

            ExamSet es = new ExamSet();
            es.setExamSetName(esReq.getExamSetName());
            es.setQuestionIds(esReq.getQuestionIds());
            es.setTestMaster(test);

            for (Integer qid : esReq.getQuestionIds()) {
                QuestionMaster q = questionRepository.findById(qid)
                        .orElseThrow(() ->
                                new RuntimeException("Question "+ qid +" not found"));

                allQuestions.add(q);
                allPickedIds.add(qid);

                if (q.getStandardMaster()!=null) std .add(q.getStandardMaster());
                if (q.getSubjectMaster() !=null) sub .add(q.getSubjectMaster());
                if (q.getChapterMaster()!=null) chap.add(q.getChapterMaster());
                if (q.getTopicMaster()  !=null) top .add(q.getTopicMaster());
            }
            examSets.add(es);
        }

        /* ── 7. push collections back into TestMaster ────────────────── */
        test.setExamSets(examSets);
        test.setStandardMaster(new ArrayList<>(std));
        test.setSubjectMaster (new ArrayList<>(sub));
        test.setChapterMasters(new ArrayList<>(chap));
        test.setTopicMasters  (new ArrayList<>(top));
        test.setQuestionMasters(allQuestions);

        /* scalar fields */
        test.setCreatedBy(teacher);
        test.setCreatedDate(new Date());
        test.setStatus("Upcoming");

        /* ── 8. save + mark used ─────────────────────────────────────── */
        try {
            testRepository.save(test);

            LocalDateTime now = LocalDateTime.now();
            allPickedIds.forEach(id -> {
                TeacherQuestionUsage.PK pk = new TeacherQuestionUsage.PK();
                pk.setTeacherId(teacher.getId());
                pk.setQuestionId(id);

                if (!usageRepo.existsById(pk)) {
                    TeacherQuestionUsage u = new TeacherQuestionUsage();
                    u.setTeacherId(teacher.getId());
                    u.setQuestionId(id);
                    u.setUsedAt(now);
                    usageRepo.save(u);
                }
            });

            main.setMessage("Test created successfully");
            main.setFlag(true);
            main.setResponseCode(HttpStatus.OK.value());

        } catch (Exception e) {
            e.printStackTrace();
            main.setMessage("Something went wrong");
            main.setFlag(false);
            main.setResponseCode(HttpStatus.BAD_REQUEST.value());
        }

        return main;
    }


    @Override
    public TestQuestionsStudentResponse getTestQuestionsForStudent(Long userId, Integer testId) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TestMaster testMaster = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

//        if (!"ACTIVE".equalsIgnoreCase(testMaster.getStatus())) {
//            throw new RuntimeException("Test is not active or accessible.");
//        }

        List<QuestionMaster> questions = testMaster.getQuestionMasters();
        List<QuestionResponseForStudent> questionResponses = questions.stream().map(q -> new QuestionResponseForStudent(
                q.getQuestionId(),
                q.getQuestion(),
                q.getOption1(),
                q.getOption2(),
                q.getOption3(),
                q.getOption4(),
                q.getMarks(),
                false, // Default answered as false
                (Set<String>) q.getMultiAnswers()
        )).collect(Collectors.toList());

        return new TestQuestionsStudentResponse(
                testMaster.getTestId(),
                testMaster.getTestName(),
                testMaster.getTestDate(),
                testMaster.getStartTime(),
                testMaster.getEndTime(),
                testMaster.getMarks(),
                questionResponses,
                testMaster.getCreatedBy().getId()
        );
    }

    public TestQuestionsStudentResponse getTestQuestionsFromTeacher(Integer testId) {
        // Fetch a random ExamSet for the given testId
        ExamSet randomExamSet = examSetRepository.findRandomExamSetByTestId(testId)
                .orElseThrow(() -> new RuntimeException("No random test found for testId: " + testId));

        // Fetch questions using the questionIds from ExamSet while maintaining order
        List<Integer> questionIds = randomExamSet.getQuestionIds();

// Fetch all questions from DB (JPA does not maintain order)
        List<QuestionMaster> unorderedQuestions = questionRepository.findByQuestionIdIn(questionIds);

// Maintain the order of questions as stored in ExamSet
        Map<Integer, QuestionMaster> questionMap = unorderedQuestions.stream()
                .collect(Collectors.toMap(QuestionMaster::getQuestionId, q -> q));

        List<QuestionMaster> orderedQuestions = questionIds.stream()
                .map(questionMap::get)
                .filter(Objects::nonNull) // In case some questions are missing in DB
                .collect(Collectors.toList());

// Convert questions to response format
        List<QuestionResponseForStudent> questionResponses = orderedQuestions.stream().map(q -> new QuestionResponseForStudent(
                q.getQuestionId(),
                q.getQuestion(),
                q.getOption1(),
                q.getOption2(),
                q.getOption3(),
                q.getOption4(),
                q.getMarks(),
                false, // Default answered as false
                q.getMultiAnswers()
        )).collect(Collectors.toList());

// Fetch TestMaster details
        TestMaster testMaster = randomExamSet.getTestMaster();

        // Return response
        return new TestQuestionsStudentResponse(
                testMaster.getTestId(),
                testMaster.getTestName(),
                testMaster.getTestDate(),
                testMaster.getStartTime(),
                testMaster.getEndTime(),
                testMaster.getMarks(),
                questionResponses,
                testMaster.getCreatedBy().getId()
        );
    }

    @Override
    public TestResultResponse getSubmittedTest(Long studentId, Integer testId) {
        try{
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        TestMaster test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        TestResultResponse response = new TestResultResponse();
        response.setTestName(test.getTestName());
        response.setTestId(testId);
        response.setTestDate(test.getTestDate());
        response.setStartTime(test.getStartTime());
        response.setEndTime(test.getEndTime());
        response.setCreatedBy(test.getCreatedBy().getId());
        response.setMarks(test.getMarks());


        // Fetch all the questions that appeared in this test
        List<QuestionMaster> questions = questionRepository.findAllByTestId(testId);

        // Fetch the user's submitted answers
        List<TestSubmissionDetail> submittedDetails = submissionDetailRepository.findAllByUserAndTest(studentId, testId);
//        System.out.println(submittedDetails);

        // Convert submission details into a Map for quick lookup
            Map<Integer, TestSubmissionDetail> userAnswersMap = submittedDetails.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            detail -> detail.getQuestion().getQuestionId(),
                            detail -> detail,
                            // Merge function: if there's a duplicate key, keep 'newVal'
                            (existingVal, newVal) -> newVal
                    ));


            // Convert Questions to DTOs and include user-selected answers
        List<TestResultResponse.QuestionResponseDTO> questionDtos = questions.stream()
                .filter(Objects::nonNull)
                .map(q -> {
                    TestResultResponse.QuestionResponseDTO dto = new TestResultResponse.QuestionResponseDTO();
                    dto.setQuestionNumber(q.getQuestionId());
                    dto.setQuestion(q.getQuestion());
                    dto.setOption1(q.getOption1());
                    dto.setOption2(q.getOption2());
                    dto.setOption3(q.getOption3());
                    dto.setOption4(q.getOption4());
                    dto.setMarks(q.getMarks());
                    dto.setExplanation(q.getExplanation());
                    dto.setMultiAnswers(q.getMultiAnswers());
                    dto.setAnswered(false);

                    // If user answered this question, populate details
                    if (userAnswersMap.containsKey(q.getQuestionId())) {
                        TestSubmissionDetail detail = userAnswersMap.get(q.getQuestionId());
                        dto.setUserSelectedOptions(
                                detail.getSelectedAnswers() != null
                                        ? detail.getSelectedAnswers()
                                        : new ArrayList<>()
                        );
                        dto.setTimeSpentSeconds(detail.getTimeSpentSeconds());
                        dto.setVisits(detail.getVisits());
                        dto.setAnswered(true);
                    } else {
                        // No submission => defaults
                        dto.setUserSelectedOptions(new ArrayList<>());
                        dto.setTimeSpentSeconds(0);
                        dto.setVisits(0);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        // 8) Attach question details to response
        response.setQuestionResponses(questionDtos);

        return response;

    } catch (NoSuchElementException e) {
        // This typically means a record was not found
        throw new NoSuchElementException("Error retrieving test submission data: " + e.getMessage());
    } catch (Exception e) {
        // Catch-all for other unexpected exceptions
        throw new RuntimeException("An unexpected error occurred while retrieving test submission data", e);
    }
    }

    @Override
    public Page<ModeWiseTestResponse> getModeWiseTests(Long createdById, String testMode, int page, int size) {

        if (!Arrays.asList("Online", "Offline", "Both").contains(testMode)) {
            throw new IllegalArgumentException("Invalid test mode! Allowed values: Online, Offline, Both.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("testDate").descending());

        // Fetch paginated results using projection
        Page<ModeWiseTestProjection> testPage = testRepository.findByTestModeAndCreatedBy( testMode,createdById ,pageable);

        // Get testIds to fetch subjects separately
        List<Integer> testIds = testPage.stream()
                .map(ModeWiseTestProjection::getTestId)
                .collect(Collectors.toList());

        // Fetch subjects for all testIds in a single query
        Map<Integer, List<String>> subjectMap = testIds.stream()
                .collect(Collectors.toMap(
                        testId -> testId,
                        testId -> subjectRepository.findSubjectsByTestId(testId)
                ));

        // Convert projection to DTO and attach subjects
        return testPage.map(test -> new ModeWiseTestResponse(
                test.getTestId(),
                test.getTestName(),
                subjectMap.getOrDefault(test.getTestId(), Collections.emptyList()), // Attach subjects
                test.getTestDate(),
                test.getMarks().intValue(),
                test.getStatus()
        ));
    }

    @Override
    public List<TestOfflineResponse> getAllOfflineTests(Long userId) {
        List<TestOfflineResponse> response = testRepository.findAllOfflineTestsByUserId(userId);
        return response;
    }

    @Override
    public List<TestOfflineResponse> getOfflineTestById(Integer testId) {
        // 1. Find the test by ID (using JPA)
        TestMaster testMaster  = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with ID: " + testId));


        TestOfflineResponse dto  = new TestOfflineResponse(
                testMaster .getTestId(),
                testMaster .getTestName()
        );

        // 3) Convert the List<SubjectMaster> to List<SubjectMastersResponse>
        List<SubjectOfflineResponse> subjectResponses = new ArrayList<>();
        if (testMaster.getSubjectMaster() != null) {
            for (SubjectMaster sm : testMaster.getSubjectMaster()) {
                SubjectOfflineResponse smr = new SubjectOfflineResponse();
                smr.setId(sm.getSubjectId());
                smr.setName(sm.getSubjectName());
                subjectResponses.add(smr);
            }
        }

        // 4) Attach the subject list to your DTO
        dto.setSubjects(subjectResponses);

        List<User> allStudents  = userRepository.findByCreatorId(testMaster.getCreatedBy().getId());

        List<User> studentsWithRecords = testOfflineSubmissionRepository.findStudentsByTestId(testId);

        List<User> studentsWithoutRecords = allStudents.stream()
                .filter(student -> !studentsWithRecords.contains(student))
                .collect(Collectors.toList());

        // 5) Convert students to List<StudentResponse>
        List<StudentGradeResponse> studentResponses = new ArrayList<>();
        for (User student : studentsWithoutRecords) {
            List<GradeResponse> grades = new ArrayList<>();

            // 6) Assign subject IDs with null values for grades
            for (SubjectOfflineResponse subject : subjectResponses) {
                grades.add(new GradeResponse(subject.getId(), null));
            }

            // 7) Create StudentResponse object
            studentResponses.add(new StudentGradeResponse(student.getId(), student.getFirstName(), grades));
        }
        dto.setStudents(studentResponses);

        // 8) Return the result
        List<TestOfflineResponse> result = new ArrayList<>();
        result.add(dto);
        return result;
    }

    @Override
    public void saveTestResult(List<TestOfflineSubRequest> requests) {
        // Fetch TestMaster by ID
        for (TestOfflineSubRequest request:requests) {
            TestMaster test = testRepository.findById(request.getTestId())
                    .orElseThrow(() -> new RuntimeException("Test not found"));

            // Fetch User (Student) by ID
            User student = userRepository.findById((long) request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Create a new TestResult object
            TestOfflineSubmission testResult = new TestOfflineSubmission();
            testResult.setTest(test);
            testResult.setStudent(student);
            testResult.setGrades(request.getGrades());
            double totalMarks=0;

            for (Map.Entry<String, Integer> entry : request.getGrades().entrySet()) {
                // Try to find the subject object in DB by name

                    totalMarks += entry.getValue();

            }
            Double maxMarks = test.getMarks();
            double percentage = 0.0;
            testResult.setScore(totalMarks);
            if (maxMarks != null && maxMarks > 0) {
                percentage = (totalMarks / maxMarks) * 100.0;
            }
            if(percentage<35) {
                testResult.setIsPass(Boolean.FALSE);
            }else{
                testResult.setIsPass(Boolean.TRUE);
            }

            updateStudentLeaderboard(student, percentage);

            // Save TestResult in the database
            testOfflineSubmissionRepository.save(testResult);
        }
    }

    @Override
    public List<AllOfflineTestByIdResponse> getOfflineTById(Long teacherId) {
        // Fetch all offline tests created by the teacher
        List<OfflineTestQueryResponse> offlineTests = testRepository.findAllOfflineTByUserId(teacherId);

        if (offlineTests.isEmpty()) {
            throw new Apierrorr("No offline tests found for teacher ID: " + teacherId ,"NOT_FOUND");
        }

        List<AllOfflineTestByIdResponse> responses = new ArrayList<>();

        for (OfflineTestQueryResponse testQueryResponse : offlineTests) {
            // Fetch the TestMaster entity to get the subject details
            TestMaster test = testRepository.findById(testQueryResponse.getId())
                    .orElseThrow(() -> new RuntimeException("Test not found for ID: " + testQueryResponse.getId()));

            // Convert the list of SubjectMaster to a list of subject names
            List<String> subjectNames = test.getSubjectMaster().stream()
                    .map(SubjectMaster::getSubjectName)
                    .collect(Collectors.toList());

            AllOfflineTestByIdResponse dto = new AllOfflineTestByIdResponse(
                    testQueryResponse.getId(),
                    testQueryResponse.getName(),
                    subjectNames,
                    testQueryResponse.getDate(),
                    testQueryResponse.getTotalMarks(),
                    testQueryResponse.getStatus(),
                    testQueryResponse.getEndTime()
            );
            responses.add(dto);
        }
        return responses;
    }

    @Override
    public List<ReportOfflineTestResponse> getAllOfflineTestResult(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<TestOfflineSubmission> testOfflineSubmissions = testOfflineSubmissionRepository.findAllByStudentId(studentId);

        if (testOfflineSubmissions.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReportOfflineTestResponse> responses = new ArrayList<>();

        for (TestOfflineSubmission offlineSubmission : testOfflineSubmissions) {
            if (offlineSubmission.getTest() == null) continue;

            ReportOfflineTestResponse response = new ReportOfflineTestResponse();
            response.setId(offlineSubmission.getTest().getTestId());
            response.setName(offlineSubmission.getTest().getTestName());
            response.setDate(offlineSubmission.getTest().getTestDate());
            response.setSemester(offlineSubmission.getTest().getTypeOfTest());
            response.setMode("Offline");

            double totalTestMarks = offlineSubmission.getTest().getMarks();
            int obtainedMarks = 0;

            // ✅ Map to store Subject-wise marks
            Map<String, ReportOfflineTestResponse.SubjectResult> subjectMap = new HashMap<>();

            // ✅ Get all questions in the test
            List<QuestionMaster> testQuestions = offlineSubmission.getTest().getQuestionMasters();

            // ✅ Compute total marks per subject
            Map<String, Integer> subjectTotalMarksMap = new HashMap<>();
            for (QuestionMaster question : testQuestions) {
                if (question.getSubjectMaster() == null) continue;
                String subjectName = question.getSubjectMaster().getSubjectName();
                int questionMarks = question.getMarks();
                subjectTotalMarksMap.put(subjectName, subjectTotalMarksMap.getOrDefault(subjectName, 0) + questionMarks);
            }

            // ✅ Iterate over obtained grades and calculate subject-wise marks
            for (Map.Entry<String, Integer> entry : offlineSubmission.getGrades().entrySet()) {
                String subjectName = entry.getKey();
                int subjectMarksObtained = entry.getValue();
                obtainedMarks += subjectMarksObtained;

                // ✅ Fetch total marks for this subject from the calculated map
                int subjectTotalMarks = subjectTotalMarksMap.getOrDefault(subjectName, 0);

                // ✅ Update subject-wise results
                subjectMap.computeIfAbsent(subjectName, key -> new ReportOfflineTestResponse.SubjectResult())
                        .updateSubjectResults(subjectName, subjectTotalMarks, subjectMarksObtained);
            }

            // ✅ Set overall test-level results
            response.setTotalMarks(totalTestMarks);
            response.setObtainedMarks(obtainedMarks);
            double overallPercentage = (totalTestMarks > 0) ? (obtainedMarks * 100.0 / totalTestMarks) : 0.0;
            response.setPercentage(overallPercentage);
            response.setGrade(calculateGrade(overallPercentage));
            response.setStatus(overallPercentage >= 40.0 ? "Pass" : "Fail");

            // ✅ Convert ReportOfflineTestResponse.SubjectResult to ReportForStudentTestResponse.SubjectResult
            List<ReportForStudentTestResponse.SubjectResult> convertedSubjectResults = subjectMap.values().stream()
                    .map(subjectResult -> new ReportForStudentTestResponse.SubjectResult(
                            subjectResult.getSubject(),
                            subjectResult.getTotalMarks(),
                            subjectResult.getObtainedMarks(),
                            subjectResult.getPercentage(),
                            subjectResult.getGrade(),
                            subjectResult.getStatus()
                    ))
                    .collect(Collectors.toList());

// ✅ Set the converted list
            response.setSubjectResults(convertedSubjectResults);


            responses.add(response);
        }

        return responses;
    }

    @Override
    public StudentPassRetReport getTeacherReport(Long tid) {

        updateAllTestStatuses();

        User teacher = userRepository.findById(tid)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id = " + tid));

        StudentPassRetReport report = new StudentPassRetReport();

        List<TestMaster> teacherTests = testRepository.findAllByCreatedBy_Id(tid);

        if (teacherTests.isEmpty()) {
            report.setPassRate(0.0);
        } else {
            List<Integer> testIds = teacherTests.stream()
                    .map(TestMaster::getTestId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<TestSubmission> onlineSubmissions =
                    submissionRepository.findByTestIds(testIds);
            List<TestOfflineSubmission> offlineSubmissions =
                    testOfflineSubmissionRepository.findByTestIds(testIds);

            long onlinePass = onlineSubmissions.stream()
                    .filter(s -> Boolean.TRUE.equals(s.getIsPass()))
                    .count();

            long offlinePass = offlineSubmissions.stream()
                    .filter(s -> Boolean.TRUE.equals(s.getIsPass()))
                    .count();

            long total = onlineSubmissions.size() + offlineSubmissions.size();
            long pass = onlinePass + offlinePass;

            double passRate = total == 0 ? 0.0 :
                    Math.round(((double) pass / total) * 10000.0) / 100.0;

            report.setPassRate(passRate);
        }

        // ✅ FIXED
        report.setTotalStudent(
                Optional.ofNullable(
                        userRepository.countStudentsByTeacherId(teacher.getId())
                ).orElse(0L)
        );

        report.setActiveExams(testRepository.countTotalActiveTest(tid));

        return report;
    }


    //    @Override
//    public List<AllTestReportByTeachId> getTestReportByTeacher(Long tid) {
//        User teacher = userRepository.findById(tid).orElseThrow(()->new RuntimeException("Teacher not found"));
//        List<TestMaster> testMasters = testRepository.findAllByCreatedBy(teacher);
//
//
//        return null;
//    }
@Override
public List<AllTestReportByTeachId> getTestReportByTeacher(Long tid) {
    User teacher = userRepository.findById(tid)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));

    List<TestMaster> testMasters = testRepository.findAllByCreatedBy(teacher);
    List<AllTestReportByTeachId> reports = new ArrayList<>();

    for (TestMaster test : testMasters) {
        AllTestReportByTeachId report = new AllTestReportByTeachId();
        report.setTestId(test.getTestId());
        report.setExamName(test.getTestName());

        // Set subjects
        List<String> subjectNames = test.getSubjectMaster().stream()
                .map(SubjectMaster::getSubjectName)
                .collect(Collectors.toList());
        report.setSubject(subjectNames);

        report.setDate(test.getTestDate());

        // Calculate student performance metrics
        calculateTestPerformanceMetrics(test, report);

        reports.add(report);
    }

    return reports;
}

    private void calculateTestPerformanceMetrics(TestMaster test, AllTestReportByTeachId report) {
        // Get all submissions for this test

        List<TestSubmission> onlineSubmissions = submissionRepository.findByTest_TestId(test.getTestId());
        List<TestOfflineSubmission> offlineSubmissions = testOfflineSubmissionRepository.findByTest_TestId(test.getTestId());

        int totalStudents = onlineSubmissions.size() + offlineSubmissions.size();
        report.setTotalStudents(totalStudents);

        if (totalStudents == 0) {
            report.setPassPercentage(0.0);
            report.setAverageScore(0.0);
            report.setHighestScore(0.0);
            return;
        }

        // Calculate pass percentage
        long passCount = onlineSubmissions.stream().filter(s -> Boolean.TRUE.equals(s.getIsPass())).count() +
                offlineSubmissions.stream().filter(s -> Boolean.TRUE.equals(s.getIsPass())).count();
        report.setPassPercentage(Math.round((double) passCount / totalStudents * 100*100)/100.0);

        // Calculate average score
        double totalScore = onlineSubmissions.stream().mapToDouble(TestSubmission::getScore).sum() +
                offlineSubmissions.stream().mapToDouble(TestOfflineSubmission::getScore).sum();
        report.setAverageScore(Math.round(totalScore / totalStudents *100)/100.0);

        // Find highest score
        double onlineMax = onlineSubmissions.stream().mapToDouble(TestSubmission::getScore).max().orElse(0.0);
        double offlineMax = offlineSubmissions.stream().mapToDouble(TestOfflineSubmission::getScore).max().orElse(0.0);
        report.setHighestScore(Math.max(onlineMax, offlineMax));

        // Set test status
        report.setStatus(test.getStatus());
    }


    /**
     * Updates or creates a StudentLeaderboard record for the given student
     * based on the percentage for one more attempted test.
     *
     * @param student   the User who completed the test
     * @param percentage the new test's percentage
     */
    private void updateStudentLeaderboard(User student, double percentage) {
        // 1) Check if a leaderboard record already exists for this student
        StudentLeaderboard existingRecord = studentLeaderboardRepository.findByStudentId(student.getId());

        if (existingRecord != null) {
            // 2) The student already has a leaderboard entry => update
            int testAttempted = existingRecord.getTestAttempted();
            Double currentAvg = existingRecord.getTotalMarks(); // existing average

            // Convert existing average back to total sum across attempts
            // (avg * number_of_tests)
            double currentTotal = currentAvg * testAttempted;

            // Now add one more test attempt
            testAttempted++;
            double newTotal = currentTotal + percentage;

            // Recompute the average = total / testAttempted
            double newAverage = newTotal / testAttempted;

            existingRecord.setTestAttempted(testAttempted);
            existingRecord.setTotalMarks(newAverage);
            existingRecord.setLastUpdated(LocalDateTime.now());
            studentLeaderboardRepository.save(existingRecord);

        } else {
            // 3) No existing record => create a new one
            StudentLeaderboard newRecord = new StudentLeaderboard();
            newRecord.setStudent(student);
            newRecord.setTestAttempted(1);     // first test
            newRecord.setTotalMarks(percentage); // average is just the new test's percentage
            newRecord.setLastUpdated(LocalDateTime.now());
            studentLeaderboardRepository.save(newRecord);
        }
    }




    @Override
    @Transactional
    public TestSubmitResponse saveTestSubmission(TestSubmissionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("TestSubmissionRequest cannot be null");
        }

        // Validate Student
        User user = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found for ID: " + request.getStudentId()));

        // Validate Test
        TestMaster test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new RuntimeException("Test not found for ID: " + request.getTestId()));

        // Initialize TestSubmission entity
        TestSubmission submission = new TestSubmission();
        submission.setUser(user);
        submission.setTest(test);

        double totalObtained = 0.0;
        double totalPossible = 0.0;

        // Ensure answers are present
        List<TestSubmissionDetail> detailsList = new ArrayList<>();
        if (request.getAnswers() != null) {
            for (TestSubmissionRequest.QuestionSubmission qs : request.getAnswers()) {
                if (qs == null || qs.getQuestionId() == null) {
                    continue; // Skip if the question submission is null
                }

                // Fetch question, handle missing case
                QuestionMaster question = questionRepository.findById(qs.getQuestionId())
                        .orElseThrow(() -> new RuntimeException("Question not found for ID: " + qs.getQuestionId()));

                TestSubmissionDetail detail = new TestSubmissionDetail();
                detail.setTestSubmission(submission);
                detail.setQuestion(question);

                // Ensure selectedAnswers is not null
                detail.setSelectedAnswers(qs.getSelectedAnswers() != null ? qs.getSelectedAnswers() : new ArrayList<>());

                // Ensure timeSpent and visits are valid
                detail.setTimeSpentSeconds(Optional.ofNullable(qs.getTimeSpent()).orElse(0));
                detail.setVisits(Optional.ofNullable(qs.getVisits()).orElse(0));

                boolean correct = checkIfAnswerIsCorrect(qs.getSelectedAnswers(), question.getMultiAnswers());
                if (question.getMarks() != null) {
                    double questionMarks = question.getMarks();
                    totalPossible += questionMarks;
                    if (correct) {
                        totalObtained += questionMarks;
                    }
                }

                detailsList.add(detail);
            }
        }

        submission.setSubmissionDetails(detailsList);

        // Ensure importantQuestions is not null before fetching them
        List<QuestionMaster> importantQuestions = new ArrayList<>();
        if (request.getImportantQuestions() != null && !request.getImportantQuestions().isEmpty()) {
            importantQuestions = questionRepository.findAllByQuestionIdIn(request.getImportantQuestions());
        }
        submission.setImportantQuestions(importantQuestions);


         submission.setScore(totalObtained);
        // 7) Calculate percentage
        double percentage = 0.0;
        if (totalPossible > 0) {
            percentage = (totalObtained / totalPossible) * 100.0;
        }
        if(percentage<35) {
            submission.setIsPass(Boolean.FALSE);
        }else{
            submission.setIsPass(Boolean.TRUE);
        }

        TestSubmission submission1 =  submissionRepository.save(submission);
        // 8) Update StudentLeaderboard
        updateStudentLeaderboard(user, percentage);

            TestSubmitResponse response = new TestSubmitResponse();
            response.setName(user.getFirstName()+" "+user.getLastName());
            response.setSubmittedAt(submission1.getSubmittedAt());
         return response;
    }



        /**
         * Update status of each test based on current date/time.
         * - 'Upcoming' if now < startDateTime
         * - 'Active'   if startDateTime <= now <= endDateTime
         * - 'Ended'    if now > endDateTime
         */
        public void updateAllTestStatuses() {
            List<TestMaster> allTests = testRepository.findAll();
            LocalDateTime now = LocalDateTime.now();

            for (TestMaster test : allTests) {
                if (test == null) {
                    continue; // skip null test objects
                }

                Date testDate = test.getTestDate();
                LocalTime startTime = test.getStartTime();
                LocalTime endTime = test.getEndTime();

                // Validate that all required fields are non-null
                if (testDate == null || startTime == null || endTime == null) {
                    System.err.println("Skipping Test ID " + test.getTestId() + " due to null date or time fields.");
                    continue;
                }

                try {
                    // Convert java.util.Date to LocalDate
                    LocalDate datePart = testDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    LocalDateTime startDateTime = LocalDateTime.of(datePart, startTime);
                    LocalDateTime endDateTime = LocalDateTime.of(datePart, endTime);

                    if (now.isBefore(startDateTime)) {
                        test.setStatus("Upcoming");
                    } else if (now.isAfter(endDateTime)) {
                        test.setStatus("Ended");
                    } else {
                        test.setStatus("Active");
                    }

                } catch (Exception e) {
                    System.err.println("Failed to process Test ID " + test.getTestId() + ": " + e.getMessage());
                }
            }

            // Save all updated statuses back to the database
            testRepository.saveAll(allTests);
        }


    @Override
    public List<ReportForStudentTestResponse> getResultForAllTest(Long studentId) {
        List<TestSubmission> testSubmissions = submissionRepository.findByUserId(studentId);
        if (testSubmissions == null || testSubmissions.isEmpty()) {
            return Collections.emptyList();
        }

        List<ReportForStudentTestResponse> testResponses = new ArrayList<>();

        Set<Long> processedSubmissions = new HashSet<>(); // ✅ Track processed submissions

        for (TestSubmission submission : testSubmissions) {
            if (submission == null || submission.getTest() == null) continue;

            // ✅ Ensure we don’t process the same submission multiple times
            if (processedSubmissions.contains(submission.getTestSubmissionId())) {
                continue;
            }
            processedSubmissions.add(submission.getTestSubmissionId());

            ReportForStudentTestResponse response = new ReportForStudentTestResponse();
            response.setId(submission.getTest().getTestId());
            response.setName(submission.getTest().getTestName());
            response.setDate(submission.getSubmittedAt());
            response.setSemester(submission.getTest().getTypeOfTest());

            int totalMarks = 0, obtainedMarks = 0;
            Map<String, ReportForStudentTestResponse.SubjectResult> subjectMap = new HashMap<>();

            if (submission.getSubmissionDetails() != null) {
                for (TestSubmissionDetail detail : submission.getSubmissionDetails()) {
                    if (detail == null || detail.getQuestion() == null) continue;

                    QuestionMaster question = detail.getQuestion();
                    if (question.getMarks() == null) continue;

                    String subjectName = Optional.ofNullable(question.getSubjectMaster())
                            .map(SubjectMaster::getSubjectName)
                            .orElse("Unknown");

                    int questionMarks = question.getMarks();
                    totalMarks += questionMarks;

                    List<String> selectedAnswers = Optional.ofNullable(detail.getSelectedAnswers()).orElse(new ArrayList<>());
                    Set<String> correctAnswers = Optional.ofNullable(question.getMultiAnswers()).orElse(new HashSet<>());

                    boolean isCorrect = checkIfAnswerIsCorrect(selectedAnswers, correctAnswers);
                    int obtainedForQuestion = isCorrect ? questionMarks : 0;
                    obtainedMarks += obtainedForQuestion;

                    subjectMap.computeIfAbsent(subjectName, key -> new ReportForStudentTestResponse.SubjectResult())
                            .updateSubjectResults(subjectName, questionMarks, obtainedForQuestion);
                }
            }

            response.setTotalMarks(totalMarks);
            response.setObtainedMarks(obtainedMarks);
            double overallPercentage = (totalMarks > 0) ? (obtainedMarks * 100.0 / totalMarks) : 0.0;
            response.setPercentage(overallPercentage);
            response.setGrade(calculateGrade(overallPercentage));
            response.setStatus(overallPercentage >= 40.0 ? "Pass" : "Fail");
            response.setSubjectResults(new ArrayList<>(subjectMap.values()));

            testResponses.add(response);
        }

        return testResponses;
    }





    /**
     * ✅ Checks if the student's selected answers exactly match the correct answers.
     * Comparison is done in a case-insensitive way.
     */
    private boolean checkIfAnswerIsCorrect(List<String> selectedAnswers, Set<String> correctAnswers) {
        // Check for null references and empty collections
        if (selectedAnswers == null || correctAnswers == null) {
            System.out.println("Error: One of the answer sets is null");
            return false;
        }

        if (selectedAnswers.isEmpty() || correctAnswers.isEmpty()) {
            System.out.println("Warning: Empty selected or correct answers");
            return false;
        }

        // Filter out null values and convert to lowercase sets
        Set<String> selectedSet = selectedAnswers.stream()
                .filter(Objects::nonNull) // Remove any null entries
                .map(String::toLowerCase) // Convert to lowercase for case-insensitive comparison
                .collect(Collectors.toSet());

        Set<String> correctSet = correctAnswers.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // Check if both sets are identical
        boolean isCorrect = selectedSet.equals(correctSet);

        // Debugging logs
        System.out.println("Selected Answers: " + selectedSet);
        System.out.println("Correct Answers: " + correctSet);
        System.out.println("Is Answer Correct? " + isCorrect);

        return isCorrect;
    }



    /**
     * Calculates the grade based on the provided percentage.
     * (Implement your grading logic here.)
     *
     * @param percentage the percentage score
     * @return the grade as a String
     */
    private String calculateGrade(double percentage) {
        if (percentage >= 90) {
            return "A+";
        } else if (percentage >= 80) {
            return "A";
        } else if (percentage >= 70) {
            return "B+";
        } else if (percentage >= 60) {
            return "B";
        } else if (percentage >= 50) {
            return "C";
        } else if (percentage >= 40) {
            return "D";
        } else {
            return "F";
        }
    }




    private void addHtmlToPdf(Document document, PdfWriter writer, String html) throws IOException, DocumentException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes());
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, inputStream);
    }



    @Override
    public TestReportResponse getTestReport(Integer testId) {
        // 1) Find the test
        TestMaster test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found with ID " + testId));

        // 2) Initialize response DTO
        TestReportResponse response = new TestReportResponse();
        response.setId(testId);
        response.setExamName(test.getTestName());
        response.setSubject(extractSubject(test));
        response.setDate(extractTestDate(test));
        response.setDuration(computeDuration(test));
        response.setStatus(test.getStatus() != null ? test.getStatus() : "N/A");
        response.setTotalQuestions(test.getQuestionMasters() != null ? test.getQuestionMasters().size() : 0);


        // 3) Retrieve all submissions (online + offline) for this test
        List<TestSubmission> onlineSubs = submissionRepository.findByTest_TestId(testId);
        List<TestOfflineSubmission> offlineSubs = testOfflineSubmissionRepository.findByTest_TestId(testId);

        // 4) Combine them into a single list for convenience
        //    We'll track pass/fail, score, user, etc.
        int totalStudents = onlineSubs.size() + offlineSubs.size();
        response.setTotalStudents(totalStudents);

        // If no students, fill zero data and return
        if (totalStudents == 0) {
            response.setPassPercentage(0.0);
            response.setAverageScore(0.0);
            response.setHighestScore(0.0);
            response.setSections(Collections.emptyList());
            response.setStudentResults(Collections.emptyList());
            return response;
        }

        // 5) Calculate pass rate, average, highest
        double sumScores = 0.0;
        double maxScore = 0.0;
        int passCount = 0;

        // StudentResults accumulation
        List<StudentResultDTO> studentResults = new ArrayList<>();

        // 6) Process online submissions
        for (TestSubmission sub : onlineSubs) {
            double score = sub.getScore(); // from the entity
            sumScores += score;
            if (score > maxScore) {
                maxScore = score;
            }

            boolean pass = Boolean.TRUE.equals(sub.getIsPass());
            if (pass) passCount++;

            // Build the StudentResultDTO
            StudentResultDTO dto = new StudentResultDTO();
            dto.setId(sub.getTestSubmissionId());  // or sub.getUser().getId() if you prefer studentId
            dto.setName(sub.getUser().getFirstName() + " " + sub.getUser().getLastName());
            dto.setScore(score);

            double overallPercentage = (test.getMarks() > 0) ? (score * 100.0 / test.getMarks()) : 0.0;
//            System.out.println("oP-----------"+overallPercentage);
//            System.out.println("tm-----------"+test.getMarks());
//            System.out.println("obtained marks-----------"+score);

            dto.setGrade(calculateGrade(overallPercentage));
            dto.setStatus(pass ? "Pass" : "Fail");
            dto.setSubmissionTime(formatSubmissionTime(sub.getSubmittedAt()));

            // For demonstration, gather answers stats from submissionDetails
            StudentResultDTO.AnswerStats stats = computeAnswerStats(sub,response);
            dto.setAnswers(stats);

            studentResults.add(dto);
        }

        // 7) Process offline submissions
        for (TestOfflineSubmission off : offlineSubs) {
            double score = off.getScore();
            sumScores += score;
            if (score > maxScore) {
                maxScore = score;
            }

            boolean pass = Boolean.TRUE.equals(off.getIsPass());
            if (pass) passCount++;

            // Build StudentResultDTO
            StudentResultDTO dto = new StudentResultDTO();
            dto.setId(off.getId()); // or off.getStudent().getId()
            dto.setName(off.getStudent().getFirstName() + " " + off.getStudent().getLastName());
            dto.setScore(score);
            dto.setGrade(calculateGrade(score));
            dto.setStatus(pass ? "Pass" : "Fail");

            // For offline, we may not have a "submittedAt" field.
            // We'll use test's date or something:
            dto.setSubmissionTime("N/A");

            // We typically don't have correct/incorrect/skipped for offline, so 0 or skip
            StudentResultDTO.AnswerStats stats = new StudentResultDTO.AnswerStats(0,0,0);
            dto.setAnswers(stats);

            studentResults.add(dto);
        }

        double maxMarks = test.getMarks();
        double passingScore = maxMarks * 0.35;          // 35% of total
        response.setPassingScore(passingScore);

        // 8) Compute pass percentage, average score, highest
        double passPercentage = ((double) passCount / totalStudents) * 100.0;
        double averageScore = sumScores / totalStudents;

        response.setPassPercentage(passPercentage);
        response.setAverageScore(averageScore);
        response.setHighestScore(maxScore);

        // 9) Compute sections if needed (for example, from test chapters or topics)
        List<SectionDTO> sections = computeSections(test);
        response.setSections(sections);

        // 10) Attach all student results
        response.setStudentResults(studentResults);

        return response;
    }

    @Override
    public TestDetailCountResponse getTestDetailCount(Long teacherId) {

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        long totalTests = testRepository.countByCreatedById(teacherId);
        int upcoming = testRepository.countTotalUpcomingTest(teacherId);
        int ended = testRepository.countEndedTest(teacherId);

        // ✅ FIXED
        long studentCount =
                Optional.ofNullable(
                        userRepository.countStudentsByTeacherId(teacher.getId())
                ).orElse(0L);

        return new TestDetailCountResponse(
                totalTests,
                ended,
                upcoming,
                studentCount
        );
    }


    /**
     * Example: If test has only one subject in 'subjectMaster'
     * or we just fetch the first subject's name.
     * If multiple, you could join them with a comma.
     */
    private String extractSubject(TestMaster test) {
        if (test.getSubjectMaster() != null && !test.getSubjectMaster().isEmpty()) {
            return test.getSubjectMaster().get(0).getSubjectName();
        }
        return "N/A";
    }

    /**
     * Example to extract date as string from testDate
     * or from createdDate, whichever is appropriate.
     */
    private String extractTestDate(TestMaster test) {
        if (test.getTestDate() != null) {
            // Convert Date to string, e.g. yyyy-MM-dd
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(test.getTestDate());
        }
        return "N/A";
    }

    /**
     * If you store startTime/endTime, you can compute the difference,
     * or store a fixed "2 hours" if not available.
     */
    private String computeDuration(TestMaster test) {
        if (test.getStartTime() != null && test.getEndTime() != null) {
            // Example difference in hours
            long diff = Math.abs(test.getEndTime().toSecondOfDay() - test.getStartTime().toSecondOfDay());
            // convert to hours/minutes
            long hours = diff / 3600;
            long minutes = (diff % 3600) / 60;
            return hours + " hours " + minutes + " mins";
        }
        return "N/A";
    }

    /**
     * Convert the LocalDateTime to a string format.
     */
    private String formatSubmissionTime(java.time.LocalDateTime submittedAt) {
        if (submittedAt == null) return "N/A";
        return submittedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * Example "grade" logic: A/B/C...
     */


    /**
     * For each submission detail, determine correct/incorrect/skipped
     * if your domain supports it.
     * This is an example approach.
     */
    private StudentResultDTO.AnswerStats computeAnswerStats(TestSubmission submission,TestReportResponse response) {
        int correct = 0;
        int incorrect = 0;
        int skipped = 0;


        if (submission.getSubmissionDetails() != null) {
            for (TestSubmissionDetail detail : submission.getSubmissionDetails()) {
                // If you store a boolean detail.isCorrect, or compare detail answers
                // Then increment correct/incorrect. If selectedAnswers is empty => skip
                if (detail.getSelectedAnswers() == null || detail.getSelectedAnswers().isEmpty()) {
                    skipped++;
                } else {
                    // for demonstration, assume isCorrect means all selected answers match question
                    boolean isCorrect = checkIfAnswerIsCorrect(detail.getSelectedAnswers(),detail.getQuestion().getMultiAnswers());
                    if (isCorrect){
                        correct++;
                    }else{
                     incorrect++;

                    }
                }
//                skipped=response.getTotalQuestions()-(correct+incorrect);
            }
        }
        return new StudentResultDTO.AnswerStats(correct, incorrect, skipped);
    }

    private boolean isAnswerCorrect(TestSubmissionDetail detail) {
        // Implement your actual correctness logic, e.g., compare selectedAnswers to question's correctAnswers
        return true;
    }

    /**
     * Example: If you want "sections" from chapters or topics.
     * This is just a placeholder approach.
     */
    private List<SectionDTO> computeSections(TestMaster test) {
        // For demonstration, let's assume we find them in test.getChapterMasters()
        // or test.getTopicMasters().
        // We'll pretend each is a "section" with a random averageScore, etc.
        if (test.getChapterMasters() == null || test.getChapterMasters().isEmpty()) {
            return Collections.emptyList();
        }

        List<SectionDTO> sectionList = new ArrayList<>();
        for (ChapterMaster ch : test.getChapterMasters()) {
            SectionDTO sec = new SectionDTO();
            sec.setName(ch.getChapterName());
            sec.setAverageScore(70 + Math.random()*10);  // placeholder
            sec.setQuestions(15);                       // placeholder
            sectionList.add(sec);
        }
        return sectionList;
    }


    public List<MonthWiseReportDTO> getMonthWiseReport(Long teacherId, String year) {

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        int targetYear = Integer.parseInt(year);

        List<TestMaster> teacherTests =
                testRepository.findAllByCreatedBy_Id(teacherId)
                        .stream()
                        .filter(t -> t.getTestDate() != null &&
                                t.getTestDate().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                        .getYear() == targetYear)
                        .collect(Collectors.toList());

        // ✅ FIXED
        long totalStudents =
                Optional.ofNullable(
                        userRepository.countStudentsByTeacherId(teacher.getId())
                ).orElse(0L);

        Map<Integer, List<Double>> scoresByMonth = new HashMap<>();
        Map<Integer, Integer> submissionsByMonth = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            scoresByMonth.put(i, new ArrayList<>());
            submissionsByMonth.put(i, 0);
        }

        List<Integer> testIds = teacherTests.stream()
                .map(TestMaster::getTestId)
                .collect(Collectors.toList());

        submissionRepository.findByTestIdIn(testIds).forEach(s -> {
            int m = s.getSubmittedAt().getMonthValue();
            scoresByMonth.get(m).add(s.getScore());
            submissionsByMonth.put(m, submissionsByMonth.get(m) + 1);
        });

        List<TestOfflineSubmission> offline =
                testOfflineSubmissionRepository.findByTestIdIn(testIds);

        for (TestOfflineSubmission o : offline) {
            int m = o.getTest().getTestDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .getMonthValue();
            scoresByMonth.get(m).add(o.getScore());
            submissionsByMonth.put(m, submissionsByMonth.get(m) + 1);
        }

        List<MonthWiseReportDTO> result = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            List<Double> scores = scoresByMonth.get(m);
            double avg = scores.isEmpty() ? 0 :
                    scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            double attendance = totalStudents == 0 ? 0 :
                    ((double) submissionsByMonth.get(m) / totalStudents) * 100;

            result.add(new MonthWiseReportDTO(
                    Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    Math.round(avg * 100.0) / 100.0,
                    Math.round(attendance * 100.0) / 100.0
            ));
        }

        return result;
    }


    // Helper method to check if a test is in the targetYear based on test.getTestDate()
    private boolean isTestInYear(TestMaster test, int targetYear) {
        if (test.getTestDate() == null) return false;
        // Convert Date to LocalDate
        LocalDate local = test.getTestDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return local.getYear() == targetYear;
    }

    // Extract month from LocalDateTime
    private int extractMonth(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return 1; // fallback
        }
        return dateTime.getMonthValue(); // 1..12
    }

    // Overload for Date
    private int extractMonth(java.util.Date date) {
        if (date == null) {
            return 1;
        }
        LocalDate local = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return local.getMonthValue();
    }

    // Return "Jan", "Feb", etc.
    private String getMonthName(int monthNumber) {
        return java.time.Month.of(monthNumber)
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // e.g. "Jan", "Feb"
    }

    // Example: fetch how many students the teacher has
    // If your domain doesn't store it, you can define a static # or a real query.
    private int getTotalStudentsForTeacher(User teacher) {
        // e.g., if teacher has a field teacher.getTeacherKeys() or a list of students.
        // For demonstration, we'll just return 100.
        return 100;
    }

    // If no tests, we might return a list of 12 months with zero average & attendance
    private List<MonthWiseReportDTO> buildEmptyYearReport() {
        List<MonthWiseReportDTO> emptyList = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            emptyList.add(new MonthWiseReportDTO(getMonthName(m), 0.0, 0.0));
        }
        return emptyList;
    }

    public List<StudentPerformanceDTO> getAllStudentsPerformance(Long teacherId) {
        // 1) Fetch all students for this teacher.
        List<Long> students = userRepository.findStudentsByTeacherId(teacherId);
        if (students.isEmpty()) {
            return Collections.emptyList();
        }

        List<StudentPerformanceDTO> reports = new ArrayList<>();
        // 2) For each student, build the performance DTO
        for (Long student : students) {
            StudentPerformanceDTO dto = getStudentPerformance(student);
            reports.add(dto);
        }

        return reports;
    }

    @Override
    public List<ExamResponse> getTeacherWiseTest(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found for id: " + teacherId));

        List<TestMaster> testMasters = testRepository.findAllByCreatedBy(teacher);
        if (testMasters.isEmpty()) {
            return Collections.emptyList();
        }

        List<ExamResponse> examResponses = new ArrayList<>();

        for (TestMaster tm : testMasters) {
            ExamResponse examResponse = new ExamResponse();
            examResponse.setExamId(tm.getTestId());
            examResponse.setExamName(tm.getTestName());
            examResponse.setDate(tm.getTestDate()); // or createdDate, depends on your domain

            Integer totalOfflineStudents = testOfflineSubmissionRepository.countOfflineSubmittedTestsByTestId(tm.getTestId());
            Integer totalOnlineStudents = submissionRepository.countSubmittedTestsByTestId(tm.getTestId());
            int totalStudents = (totalOfflineStudents != null ? totalOfflineStudents : 0)
                    + (totalOnlineStudents != null ? totalOnlineStudents : 0);

            examResponse.setTotalStudents(totalStudents);


            long passCount = computePassCount(tm.getTestId());
            double passPercentage = (totalStudents == 0) ? 0.0 : (passCount / (double) totalStudents) * 100.0;
            examResponse.setPassPercentage(passPercentage);

            // 3c) Build subject-wise details
            List<SubjectRes1> subjectResponses = new ArrayList<>();
            if (tm.getSubjectMaster() != null) {
                for (SubjectMaster sm : tm.getSubjectMaster()) {
                    SubjectRes1 subjectRes = new SubjectRes1();
                    subjectRes.setSubjectName(sm.getSubjectName());

                    // For this subject, we compute pass % among those who took the subject,
                    // plus average score, highest & lowest. We'll define placeholders or custom logic.
                    // We'll fetch all offline/online submissions for the test,
                    // then in memory filter for the subject.
                    // But how do we know which subject an offline/online submission belongs to?
                    // Possibly from question-level or from test subject itself.
                    // If the entire test is for multiple subjects, we need a deeper approach.
                    // For demonstration, we'll do placeholders or a simplified logic:

                    SubjectStats stats = computeSubjectStats(tm.getTestId(), sm);
                    subjectRes.setPassPercentage(stats.getPassPercentage());
                    subjectRes.setAvgScore(stats.getAvgScore());
                    subjectRes.setHighestScore(stats.getHighestScore());
                    subjectRes.setLowestScore(stats.getLowestScore());

                    subjectResponses.add(subjectRes);
                }
            }
            examResponse.setSubjects(subjectResponses);

            // 4) Add to our main list
            examResponses.add(examResponse);
        }

        return examResponses;
    }

    @Override
    public List<TestSummaryResponse> getAllTestSummaries() {
        List<TestMaster> tests = testRepository.findAll();
        List<TestSummaryResponse> summaries = new ArrayList<>();

        for (TestMaster test : tests) {
            int testId = test.getTestId();

            // Fetch all online and offline submissions for this test
            List<TestSubmission> onlineSubs = submissionRepository.findOnlineSubsByTestId(testId);
            List<TestOfflineSubmission> offlineSubs = testOfflineSubmissionRepository.findOfflineSubsByTestId(testId);

            List<Double> allScores = new ArrayList<>();
            int totalParticipants = 0;
            int totalPass = 0;
            int totalTime = 0;

            for (TestSubmission s : onlineSubs) {
                allScores.add(s.getScore());
                totalTime += s.getSubmissionDetails().stream().mapToInt(d -> d.getTimeSpentSeconds()).sum();
                totalParticipants++;
                if (Boolean.TRUE.equals(s.getIsPass())) totalPass++;
            }

            for (TestOfflineSubmission s : offlineSubs) {
                allScores.add(s.getScore());
                totalParticipants++;
                if (Boolean.TRUE.equals(s.getIsPass())) totalPass++;
            }

            double avgScore = allScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double passRate = (totalParticipants == 0) ? 0 : (totalPass * 100.0) / totalParticipants;
            double avgTime = (totalParticipants == 0) ? 0 : (totalTime / 60.0) / totalParticipants;

            summaries.add(new TestSummaryResponse(
                    testId,
                    test.getTestName(),
                    totalParticipants,
                    Math.round(avgScore),
                    Math.round(passRate),
                    Math.round(avgTime)
            ));
        }

        return summaries;
    }

    @Override
    public List<UpcomingEventDTO> getUpcomingEvents(Long parentId) {

        // 1) resolve the student linked to this parent
        Long studentId = userRepository.getStudentIdByParentId(parentId);
        if (studentId == null)
            throw new RuntimeException("No student linked to parent " + parentId);

        // 2) fetch tests
        List<TestMaster> tests = testRepository.findUpcomingTestsForStudent(studentId);

        // 3) map to DTO
        DateTimeFormatter dayFmt   = DateTimeFormatter.ofPattern("d");
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM");
        DateTimeFormatter timeFmt  = DateTimeFormatter.ofPattern("h:mm a");

        return tests.stream()
                .map(t -> {
                    LocalDate     date = t.getTestDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    String day   = date.format(dayFmt);
                    String month = date.format(monthFmt);

                    String time  = t.getStartTime() != null
                            ? t.getStartTime().format(timeFmt)
                            : "N/A";

                    return new UpcomingEventDTO(day, month, t.getTestName(), time);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RecentActivityDTO> getRecentActivities(Long parentId) {

        if (parentId == null || parentId <= 0)
            throw new IllegalArgumentException("parentId must be positive");

        Long studentId = userRepository.getStudentIdByParentId(parentId);
        if (studentId == null)
            throw new Apierrorr("No student linked to parent " + parentId+" ","404");

        List<Object[]> rows = testRepository.findPastTestsForStudent(studentId);
        if (rows.isEmpty())
            throw new Apierrorr("No recent activities found","404");

        return rows.stream()
                .map(r -> {
                    String testName   = (String) r[1];
                    Date   testDate   = (Date)   r[2];
                    String subject    = (String) r[3];
                    String rawStatus  = (String) r[4]; // e.g. “Completed”

                    // map status → colour (customise as you wish)
                    String color ="";
                    switch (rawStatus) {
                        case "Completed"  :color="green"; break;
                        case "Graded"    :color= "blue"; break;
                        case "Excellent"  :color= "purple"; break;
                        default          :color= "gray";
                    };

                    LocalDateTime ldt = testDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    return new RecentActivityDTO(
                            testName,
                            subject,
                            ldt.format(iso),
                            rawStatus,
                            color,
                            "check-circle"
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public TestCountResponse getCounts(Long requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("User not found: " + requesterId));

        /* ───────── SUPER ADMIN ───────── */
        if (requester.isSuperAdmin()) {
            long teacher = testRepository.countByCreatedBy_Roles_Name(ERole.ROLE_TEACHER);
            long student = testRepository.countByCreatedBy_Roles_Name(ERole.ROLE_STUDENT);
            return new TestCountResponse(teacher, student);
        }

        /* ───────────── ADMIN ─────────── */
        if (requester.isAdmin()) {

            Set<Long> descendantIds = hierarchyService.findAllDescendants(requesterId);

            if (descendantIds.isEmpty()) {
                return new TestCountResponse(0, 0);
            }

            long teacher = testRepository
                    .countByCreatedBy_IdInAndCreatedBy_Roles_Name(descendantIds, ERole.ROLE_TEACHER);
            long student = testRepository
                    .countByCreatedBy_IdInAndCreatedBy_Roles_Name(descendantIds, ERole.ROLE_STUDENT);

            return new TestCountResponse(teacher, student);
        }

        /* ─────── anything else ─────── */
        throw new Apierrorr("Only SUPER_ADMIN or ADMIN can call this API","404");
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PaginatedResponse<TestQuestionsResponse> createTestFromTeacherSide(QuestionFilterRequest req) {

        Specification<QuestionMaster> spec = QuestionSpecifications.withFilters(req);


        Specification<QuestionMaster> usedFilter =
                TeacherUsedSpec.filter(req.getUsedStatus(), req.getTeacherId());
        if (usedFilter != null) spec = spec.and(usedFilter);

        int page = req.getCurrentPage() != null && req.getCurrentPage() > 0 ? req.getCurrentPage() - 1 : 0;
        int size = req.getPageSize() != null && req.getPageSize() > 0 ? req.getPageSize() : 10;

        PageRequest pageable = PageRequest.of(page, size);

        Page<QuestionMaster> pageResult = questionRepository.findAll(spec, pageable);

        List<TestQuestionsResponse> content = pageResult.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                pageResult.getNumber() + 1,
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.getSize()
        );
    }

    @Transactional
    public void resetUsed(Long teacherId) {
        usageRepo.resetFor(teacherId);
    }

//    @Override
//    @Transactional
//    public PaginatedResponse<TestQuestionsResponse>
//    createWeightageWiseTestFromTeacherSide(QuestionFilterRequest r) {
//
//        /* 1️⃣ build base spec from standard / subject / level / pyq … */
//        Specification<QuestionMaster> spec = QuestionSpecifications.withFilters(r);
//
//        /* 2️⃣ add USED / UNUSED if requested */
//        Specification<QuestionMaster> used = TeacherUsedSpec.filter(
//                r.getUsedStatus(), r.getTeacherId());
//        if (used != null) spec = spec.and(used);
//
//        /* 3️⃣ load weightage rows for given subjects (status = Active) */
//        List<ChapterWeightageMaster> cwRows =
//                    chapterWeightageRepository.findBySubjectIdInAndStatus(
//                            r.getSubjectIds(), "Active");
//
//
//        /* optional in-memory filter by chapterIds (if present in request) */
//        if (r.getChapterIds() != null && !r.getChapterIds().isEmpty()) {
//
//            cwRows = cwRows.stream()
//                    .filter(cw -> r.getChapterIds().contains(cw.getChapterId()))
//                    .collect(Collectors.toList());
//        }
//
//        if (cwRows.isEmpty()) {
//            return new PaginatedResponse<>(
//                    Collections.emptyList(),   // <── instead of List.of()
//                    1, 1, 0, 0);
//        }
//
//        List<TestQuestionsResponse> out = new ArrayList<>();
//
//        /* 4️⃣ loop chapter-by-chapter, pick `weightage` random questions */
//        for (ChapterWeightageMaster row : cwRows) {
//
//            int need = row.getWeightage() == null ? 0 : row.getWeightage().intValue();
//            if (need <= 0) continue;
//
//            Specification<QuestionMaster> chapSpec = spec.and(
//                    (root, q, cb) -> cb.equal(
//                            root.get("chapterMaster").get("chapterId"),
//                            row.getChapterId()));
//
//            List<QuestionMaster> pool = questionRepository.findAll(chapSpec);
//
//            if (pool.size() < need) continue;          // skip chapter if not enough
//
//            Collections.shuffle(pool);
//            pool.stream()
//                    .limit(need)
//                    .map(this::toDto)                      // ← your existing mapper
//                    .forEach(out::add);
//        }
//
//        /* 5️⃣ wrap result in a single-page PaginatedResponse */
//        return new PaginatedResponse<>(
//                out,
//                1,          // current page
//                1,          // total pages
//                out.size(), // total elements
//                out.size()  // page size (whole list)
//        );
//    }


    @Override
    @Transactional
    public PaginatedResponse<TestQuestionsResponse>
    createWeightageWiseTestFromTeacherSide(QuestionFilterRequest r) {

        Specification<QuestionMaster> spec = QuestionSpecifications.withFilters(r);
        Specification<QuestionMaster> used = TeacherUsedSpec.filter(
                r.getUsedStatus(), r.getTeacherId());
        if (used != null) spec = spec.and(used);

        List<ChapterWeightageMaster> cwRows =
                chapterWeightageRepository.findBySubjectIdInAndStatus(
                        r.getSubjectIds(), "Active");


//        if (r.getChapterIds() != null && !r.getChapterIds().isEmpty()) {
//            cwRows = cwRows.stream()
//                    .filter(cw -> r.getChapterIds().contains(cw.getChapterId()))
//                    .collect(Collectors.toList());
//        }
//        System.out.println("📌 SubjectIds: " + r.getSubjectIds());
//        System.out.println("📌 ChapterIds: " + r.getChapterIds());
//        System.out.println("📌 Loaded rows from chapter_weightage_master: " + cwRows.size());


        if (cwRows.isEmpty()) {
            return new PaginatedResponse<>(Collections.emptyList(), 1, 1, 0, 0);
        }

        List<TestQuestionsResponse> out = new ArrayList<>();

        for (ChapterWeightageMaster row : cwRows) {

            int need = row.getWeightage() == null ? 0 : row.getWeightage().intValue();
            if (need <= 0) continue;

//            System.out.println("▶️ Processing chapterId: " + row.getChapterId());
//            System.out.println("   SubjectId: " + row.getSubjectId());
//            System.out.println("   EntranceExamId: " + row.getEntranceExamId());
//            System.out.println("   Weightage: " + need);

            Specification<QuestionMaster> chapSpec = spec.and(
                    (root, q, cb) -> cb.equal(
                            root.get("chapterMaster").get("chapterId"),
                            row.getChapterId()));

            List<QuestionMaster> pool = questionRepository.findAll(chapSpec);

            System.out.println("   🔎 Available questions after all filters: " + pool.size());

            int take = Math.min(need, pool.size());   // ✅ NEW: take whatever is there
            if (take == 0) continue;                  // still skip if none at all

//            System.out.println("▶️ chapter " + row.getChapterId()
//                    + " need " + need + " | available " + pool.size()
//                    + " | taking " + take);

            Collections.shuffle(pool);

            pool.stream()
                    .limit(take)
                    .map(this::toDto)
                    .forEach(out::add);

//            System.out.println("   ✅ Selected " + take + " questions from chapter " + row.getChapterId());
        }

//        System.out.println("🟢 Final total questions selected: " + out.size());

        return new PaginatedResponse<>(
                out, 1, 1, out.size(), out.size());
    }

    @Override
    public ExamResponseDTO createExam(ExamRequestDTO request) {

        ExamResponseDTO response = new ExamResponseDTO();
        BeanUtils.copyProperties(request, response, "questions", "twoColumn");
        response.setTwoColumn(request.isTwoColumn());

        List<Long> orderedIds = new ArrayList<>();

        // 1) Group by subject name resolved from DB
        Map<String, List<QuestionDTO>> grouped =
                request.getQuestions().stream()
                        .collect(Collectors.groupingBy(qdto -> {
                            QuestionMaster qm = questionRepository.findById(
                                            Math.toIntExact(qdto.getQuestionId()))
                                    .orElseThrow(() ->
                                            new RuntimeException("Question not found: " + qdto.getQuestionId()));
                            return qm.getSubjectMaster().getSubjectName();
                        }));

        // 2) Build ExamSubjectDTO list using data FROM QuestionMaster
        List<ExamSubjectDTO> subjects = grouped.entrySet().stream()
                .map(entry -> {
                    String subjectName = entry.getKey();

                    List<QuestionDTO> questions = entry.getValue().stream()
                            .map(incoming -> {
                                // Re-fetch the same entity just once per loop
                                QuestionMaster qm = questionRepository.findById(
                                                Math.toIntExact(incoming.getQuestionId()))
                                        .orElseThrow(() -> new RuntimeException(
                                                "Question not found: " + incoming.getQuestionId()));

                                QuestionDTO dto = new QuestionDTO();
                                dto.setQuestionId(qm.getQuestionId().longValue());
                                dto.setQuestion(qm.getQuestion());
                                dto.setOption1(qm.getOption1());
                                dto.setOption2(qm.getOption2());
                                dto.setOption3(qm.getOption3());
                                dto.setOption4(qm.getOption4());

                                // Build answers-map → 1:A, 2:B …
                                HashMap<Integer,String> ans = new HashMap<>();
                                qm.getMultiAnswers().forEach(opt -> {
                                    switch (opt.toLowerCase()) {
                                        case "option1": ans.put(1, qm.getOption1()); break;
                                        case "option2": ans.put(2, qm.getOption2()); break;
                                        case "option3": ans.put(3, qm.getOption3()); break;
                                        case "option4": ans.put(4, qm.getOption4()); break;
                                    }
                                });
                                dto.setAnswers(ans);

                                dto.setExplanation(qm.getExplanation());
                                orderedIds.add(dto.getQuestionId());
                                return dto;
                            })
//                            .sorted(Comparator.comparing(QuestionDTO::getQuestionId))
                            .collect(Collectors.toList());

                    ExamSubjectDTO subj = new ExamSubjectDTO();
                        subj.setName(subjectName);
                    subj.setQuestions(questions);
                    return subj;
                })
                .collect(Collectors.toList());

        response.setSubjects(subjects);
    try {
        Optional<PDFSet> existingSet = pdfSetRepository
                .findByCreatorIdAndExamNameAndDateAndExamSetName(
                        request.getCreatorId(),
                        request.getExamName(),
                        request.getDate(),
                        request.getExamSetName());

        if (!existingSet.isPresent()) {
            String csv = orderedIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            PDFSet set = new PDFSet();
            set.setCreatorId(request.getCreatorId());
            set.setDate(request.getDate());
            set.setExamName(request.getExamName());
            set.setExamSetName(request.getExamSetName());
            set.setQuestionSequenceJson(csv);
            pdfSetRepository.save(set);
//            System.out.println("New PDFSet Created");
        } else {
//            System.out.println("PDFSet is Already Exist");
        }
    }catch (Exception e){
//        System.err.println("Error while processing PDFSet : "+e.getMessage());
        throw new RuntimeException("Failed to process PDFSet operation", e);
    }

//        Collections.sort(orderedIds);

//        System.out.println("question id sequence---------"+csv);
        return response;
    }



    @Override
    public PDFCrunchRes createPDF(PDFCrunchReq request) {
        // 1) Validate incoming list
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("PDFCrunchReq must include a non-null, non-empty 'questions' list.");
        }
        List<Long> orderedIds = new ArrayList<>();

        // 2) Extract all question IDs from the incoming DTOs
        List<Integer> questionIds = request.getQuestions().stream()
                .map(qdto -> Math.toIntExact(qdto.getQuestionId()))
                .collect(Collectors.toList());

        // 3) Fetch exactly those QuestionMaster entities whose IDs appear in questionIds
        List<QuestionMaster> questionEntities = questionRepository.findAllByQuestionIdIn(questionIds);

        // 4) Build a lookup map: questionId → QuestionMaster
        Map<Integer, QuestionMaster> lookup = questionEntities.stream()
                .collect(Collectors.toMap(QuestionMaster::getQuestionId, qm -> qm));

        // 5) Build a Map<String, List<QuestionMaster>> grouped by chapter name
        Map<String, List<QuestionMaster>> byChapter = questionIds.stream()
                .map(id -> {
                    QuestionMaster qm = lookup.get(id);
                    if (qm == null) {
                        throw new IllegalArgumentException("Question ID " + id + " not found or not accessible");
                    }
                    return qm;
                })
                .collect(Collectors.groupingBy(qm -> {
                    ChapterMaster chap = qm.getChapterMaster();
                    if (chap == null || chap.getChapterName() == null) {
                        throw new RuntimeException("Question " + qm.getQuestionId() + " has no chapter assigned.");
                    }
                    return chap.getChapterName();
                }));

        // 6) Convert each group into a ChapterDto (populating QuestionDTO from QuestionMaster)
        List<com.bezkoder.springjwt.dto.ChapterDto> chapterDtos = byChapter.entrySet().stream()
                .map(entry -> {
                    String chapterName = entry.getKey();
                    List<QuestionMaster> mastersInThisChapter = entry.getValue();

                    // Build a list of QuestionDTO for this chapter
                    List<QuestionDTO> dtoQuestions = mastersInThisChapter.stream()
                            .map(qm -> {
                                QuestionDTO qdto = new QuestionDTO();
                                // Copy primitive fields
                                qdto.setQuestionId(qm.getQuestionId().longValue());
                                qdto.setQuestion(qm.getQuestion());
                                qdto.setOption1(qm.getOption1());
                                qdto.setOption2(qm.getOption2());
                                qdto.setOption3(qm.getOption3());
                                qdto.setOption4(qm.getOption4());
                                qdto.setExplanation(qm.getExplanation());
                                // Build the answers‐map from multiAnswers
                                HashMap<Integer, String> ansMap = new HashMap<>();
                                for (String optStr : qm.getMultiAnswers()) {
                                    // optStr is e.g. "option1", "option2", etc.
                                    switch (optStr.toLowerCase()) {
                                        case "option1":
                                            ansMap.put(1, qm.getOption1());
                                            break;
                                        case "option2":
                                            ansMap.put(2, qm.getOption2());
                                            break;
                                        case "option3":
                                            ansMap.put(3, qm.getOption3());
                                            break;
                                        case "option4":
                                            ansMap.put(4, qm.getOption4());
                                            break;
                                        default:
                                            // In case you have an unexpected value in multiAnswers, skip or handle.
                                            break;
                                    }
                                }
                                orderedIds.add(qdto.getQuestionId());
                                qdto.setAnswers(ansMap);
                                return qdto;
                            })
                            // If you want a deterministic ordering inside each chapter, you can sort here.
//                            .sorted(Comparator.comparing(QuestionDTO::getQuestionId))
                            .collect(Collectors.toList());

                    // Build ChapterDto
                    com.bezkoder.springjwt.dto.ChapterDto chapterDto = new com.bezkoder.springjwt.dto.ChapterDto();
                    chapterDto.setName(chapterName);
                    chapterDto.setQuestions(dtoQuestions);
                    return chapterDto;
                })
                // If you want all chapters in a stable order (e.g. alphabetical), you could .sorted(...) here.
                .collect(Collectors.toList());

        // 7) Populate the PDFCrunchRes
        PDFCrunchRes response = new PDFCrunchRes();
        response.setInstituteName(request.getInstituteName());
        response.setOrganization(request.getOrganization());
        response.setExamName(request.getExamName());
        response.setStandard(request.getStandard());
        response.setTotalQuestions(request.getTotalQuestions());
        response.setTotalMarks(request.getTotalMarks());
        response.setTeacherKey(request.isTeacherKey());
        response.setWatermark(request.getWatermark());
        response.setAngle(request.getAngle());
        response.setOpacity(request.getOpacity());
        // If it’s a teacher’s key, always force single‐column
        if (request.isTeacherKey()) {
            response.setTwoColumn(false);
        } else {
            response.setTwoColumn(request.isTwoColumn());
        }
        response.setDate(request.getDate());
        response.setTime(request.getTime());
        response.setChapter(chapterDtos);

            String csv = orderedIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

        // Check if already exists
        /// create same logic
        Optional<PDFSet> existingSet1 = pdfSetRepository.findByCreatorIdAndExamNameAndDateAndExamSetName(
                request.getCreatorId(),
                request.getExamName(),
                request.getDate(),
                request.getExamSetName()
        );

        if (!existingSet1.isPresent()) {
            PDFSet set = new PDFSet();
            set.setCreatorId(request.getCreatorId());
            set.setDate(request.getDate());
            set.setExamName(request.getExamName());
            set.setExamSetName(request.getExamSetName());
            set.setQuestionSequenceJson(csv);
            pdfSetRepository.save(set);
        } else {
            // PDF already exists
//            System.err.println("PDFSet already exists, skipping creation.");
        }
        return response;
    }

    private List<Integer> parseQuestionSequence(String raw) throws JsonProcessingException {
        raw = raw.trim();

        // Case 1: already a JSON array -> delegate to Jackson
        if (raw.startsWith("[")) {
            return objectMapper.readValue(
                    raw, new TypeReference<List<Integer>>() {});
        }

        // Case 2: plain CSV -> split & parse
        if (!raw.isEmpty()) {
            return Arrays.stream(raw.split("\\s*,\\s*"))   // trim spaces
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();                    // nothing stored
    }


    @Override
    public AnswerKeyRes createAnswerKey(AnswerKeyReq answerKeyReq) throws JsonProcessingException {
        String examSetName = answerKeyReq.getExamSetName();
        PDFSet set = pdfSetRepository
                .findByCreatorIdAndExamNameAndDateAndExamSetName(
                        answerKeyReq.getCreatorId(), answerKeyReq.getExamName(),
                        answerKeyReq.getDate(),      answerKeyReq.getExamSetName())
                .orElseThrow(() -> new IllegalArgumentException("No PDF‐set found"));

//        List<Integer> questionIds = objectMapper.readValue(
//                set.getQuestionSequenceJson(),
//                new TypeReference<List<Integer>>() {});
        String seq = set.getQuestionSequenceJson();
        List<Integer> questionIds = parseQuestionSequence(seq);

//        List<Integer> questionIds = answerKeyReq.getQuestionIds();
        AnswerKeyRes dto = new AnswerKeyRes();
        BeanUtils.copyProperties(answerKeyReq,dto,"questionIds");

        // 1) Basic validation
        if (examSetName == null || examSetName.isEmpty()) {
            throw new IllegalArgumentException("examSetName must not be null or blank");
        }
        if (questionIds == null || questionIds.isEmpty()) {
            throw new IllegalArgumentException("questionIds must not be null or empty");
        }

        // 2) Fetch exactly those questions whose IDs appear in questionIds
        List<QuestionMaster> questions = questionRepository.findAllByQuestionIdIn(questionIds);

        // 3) Build a lookup map from questionId → QuestionMaster
        Map<Integer, QuestionMaster> lookup = questions.stream()
                .collect(Collectors.toMap(QuestionMaster::getQuestionId, q -> q));

        // 4) Build a LinkedHashMap<Integer,String> in the same order as questionIds:
        //    key 1 → answer for questionIds.get(0), key 2 → answer for questionIds.get(1), etc.
        LinkedHashMap<Integer, String> sequentialAnswers = new LinkedHashMap<>();

        for (int i = 0; i < questionIds.size(); i++) {
            Integer rawId = questionIds.get(i);
            QuestionMaster qm = lookup.get(rawId);

            // If the repository returned fewer entries than requested, it might be missing
            if (qm == null) {
                // Either skip or throw. Here we’ll throw an exception:
                throw new IllegalArgumentException("Question ID " + rawId + " not found or not accessible");
            }

            // Convert multiAnswers (Set<String> like {"option2", "option4"}) → "B,D"
            Set<String> multiAnswers = qm.getMultiAnswers();
            String joinedLetters = multiAnswers.stream()
                    .map(opt -> {
                        switch (opt.toLowerCase()) {
                            case "option1": return "A";
                            case "option2": return "B";
                            case "option3": return "C";
                            case "option4": return "D";
                            default:
                                // If your Set contains some unexpected value, you can choose to pass it through:
                                return opt;
                        }
                    })
                    .collect(Collectors.joining(","));

            // Place in the map using (i+1) as the “sequential question number”
            sequentialAnswers.put(i + 1, joinedLetters);
        }

        // 5) Build and return the DTO

        dto.setExamSetName(examSetName);
        dto.setAnswers(sequentialAnswers);
        return dto;
    }

    @Override
    public PDFChrunchAnsKeyRes createAnswerKeyCh(PDFChrunchAnsKeyReq answerKeyReq) throws JsonProcessingException {
        String examSetName = answerKeyReq.getExamSetName();
        PDFSet set = pdfSetRepository
                .findByCreatorIdAndExamNameAndDateAndExamSetName(
                        answerKeyReq.getCreatorId(), answerKeyReq.getExamName(),
                        answerKeyReq.getDate(),      answerKeyReq.getExamSetName())
                .orElseThrow(() -> new Apierrorr("PDF set not found","404"));
                        //IllegalArgumentException("No PDF‐set found"));

            String seq = set.getQuestionSequenceJson();
            List<Integer> questionIds = parseQuestionSequence(seq);

            PDFChrunchAnsKeyRes dto = new PDFChrunchAnsKeyRes();
            BeanUtils.copyProperties(answerKeyReq, dto, "questionIds");

            // 1) Basic validation
            if (examSetName == null || examSetName.isEmpty()) {
                throw new Apierrorr("Exam name must be not null","400");
                        //IllegalArgumentException("examSetName must not be null or blank");
            }
            if (questionIds == null || questionIds.isEmpty()) {
                throw new Apierrorr("Question Id must be not null","400");
                        //IllegalArgumentException("questionIds must not be null or empty");
            }

            // 2) Fetch exactly those questions whose IDs appear in questionIds
            List<QuestionMaster> questions = questionRepository.findAllByQuestionIdIn(questionIds);

            // 3) Build a lookup map from questionId → QuestionMaster
            Map<Integer, QuestionMaster> lookup = questions.stream()
                    .collect(Collectors.toMap(QuestionMaster::getQuestionId, q -> q));

            // 4) Build a LinkedHashMap<Integer,String> in the same order as questionIds:
            //    key 1 → answer for questionIds.get(0), key 2 → answer for questionIds.get(1), etc.
            LinkedHashMap<Integer, String> sequentialAnswers = new LinkedHashMap<>();

            for (int i = 0; i < questionIds.size(); i++) {
                Integer rawId = questionIds.get(i);
                QuestionMaster qm = lookup.get(rawId);

                // If the repository returned fewer entries than requested, it might be missing
                if (qm == null) {
                    // Either skip or throw. Here we’ll throw an exception:
                    throw new IllegalArgumentException("Question ID " + rawId + " not found or not accessible");
                }

                // Convert multiAnswers (Set<String> like {"option2", "option4"}) → "B,D"
                Set<String> multiAnswers = qm.getMultiAnswers();
                String joinedLetters = multiAnswers.stream()
                        .map(opt -> {
                            switch (opt.toLowerCase()) {
                                case "option1":
                                    return "A";
                                case "option2":
                                    return "B";
                                case "option3":
                                    return "C";
                                case "option4":
                                    return "D";
                                default:
                                    // If your Set contains some unexpected value, you can choose to pass it through:
                                    return opt;
                            }
                        })
                        .collect(Collectors.joining(","));

                // Place in the map using (i+1) as the “sequential question number”
                sequentialAnswers.put(i + 1, joinedLetters);
            }

            // 5) Build and return the DTO

        dto.setExamSetName(examSetName);
        dto.setAnswers(sequentialAnswers);
        return dto;    }

    @Override
    public List<ImportantQuestionDTO> findImportantQuestionsByStudent(Long studentId) {

        // 1) fetch every submission with its importantQuestions
        List<TestSubmission> submissions =
                submissionRepository.findWithImportantQuestionsByStudent(studentId);

        // 2) flatten to QuestionDTOs
        List<ImportantQuestionDTO> result = new ArrayList<>();

        submissions.forEach(ts -> {
            String testName = ts.getTest().getTestName();

            ts.getImportantQuestions().forEach(q -> {
                ImportantQuestionDTO dto = new ImportantQuestionDTO();

                // copy matching fields
                dto.setQuestionId(q.getQuestionId());
                dto.setMarks(q.getMarks());
                dto.setQuestion(q.getQuestion());
                dto.setOption1(q.getOption1());
                dto.setOption2(q.getOption2());
                dto.setOption3(q.getOption3());
                dto.setOption4(q.getOption4());
                dto.setDate(q.getDate());
                dto.setStatus(q.getStatus());
                dto.setMultiAnswers(q.getMultiAnswers());
                dto.setExplanation(q.getExplanation());
                dto.setSolution(q.getSolution());
                dto.setQuestionCategory(q.getQuestionCategory());

                // derived names (safe-null checks)
                dto.setTestName(testName);
                dto.setStandardName(
                        q.getStandardMaster()!=null
                                ? q.getStandardMaster().getStandardName() : null);
                dto.setChapterName(
                        q.getChapterMaster()!=null
                                ? q.getChapterMaster().getChapterName() : null);
                dto.setYearOfAppearance(
                        q.getYearOfAppearance()!=null
                                ? q.getYearOfAppearance().getYearOfAppearance() : null);

                result.add(dto);
            });
        });

        /* optional: sort by test name then questionId */
        result.sort(Comparator
                .comparing(ImportantQuestionDTO::getTestName, Comparator.nullsLast(String::compareTo))
                .thenComparing(ImportantQuestionDTO::getQuestionId));

        return result;
    }


    /* ---------- mapper QuestionMaster ➜ TestQuestionsResponse --------- */
    private TestQuestionsResponse toDto(QuestionMaster q) {

        /* pull multi-answer set safely (can be empty) */
        Set<String> multiAns = q.getMultiAnswers();

        return new TestQuestionsResponse(
                q.getQuestionId(),

                q.getEntranceExamMaster()==null ? null : q.getEntranceExamMaster().getEntranceExamId(),
                q.getEntranceExamMaster()==null ? null : q.getEntranceExamMaster().getEntranceExamName(),

                q.getStandardMaster()==null ? null : q.getStandardMaster().getStandardId(),
                q.getStandardMaster()==null ? null : q.getStandardMaster().getStandardName(),

                q.getSubjectMaster()==null ? null : q.getSubjectMaster().getSubjectId(),
                q.getSubjectMaster()==null ? null : q.getSubjectMaster().getSubjectName(),

                q.getChapterMaster()==null ? null : q.getChapterMaster().getChapterId(),
                q.getChapterMaster()==null ? null : q.getChapterMaster().getChapterName(),

                q.getTopicMaster()==null ? null : q.getTopicMaster().getTopicId(),
                q.getTopicMaster()==null ? null : q.getTopicMaster().getTopicName(),

                q.getSubTopicMaster()==null ? null : q.getSubTopicMaster().getSubTopicId(),
                q.getSubTopicMaster()==null ? null : q.getSubTopicMaster().getSubTopicName(),

                q.getYearOfAppearance()==null ? null : q.getYearOfAppearance().getYearOfAppearanceId(),
                q.getYearOfAppearance()==null ? null : q.getYearOfAppearance().getYearOfAppearance(),

                q.getQuestionType()==null ? null : q.getQuestionType().getQuestionTypeId(),
                q.getQuestionType()==null ? null : q.getQuestionType().getQuestionType(),

                q.getQuestionLevel()==null ? null : q.getQuestionLevel().getQuestionLevelId(),
                q.getQuestionLevel()==null ? null : q.getQuestionLevel().getQuestionLevel(),

                q.getPatternMaster()==null ? null : q.getPatternMaster().getPatternId(),
                q.getPatternMaster()==null ? null : q.getPatternMaster().getPatternName(),
                q.getPatternMaster()==null ? null : q.getPatternMaster().getPatternActualName(),

                q.getStatus(),

                q.getMarks(),
                q.getQuestion(),
                q.getOption1(),
                q.getOption2(),
                q.getOption3(),
                q.getOption4(),
                q.getDate(),
                q.getSolution(),
                q.getExplanation()          // final ctor field
//                multiAns
        );
    }

    /**
     * A helper method to count how many offline + online submissions
     * have isPass = true for a given testId.
     */
    private long computePassCount(Integer testId) {
        // fetch offline
        List<TestOfflineSubmission> offlineList = testOfflineSubmissionRepository.findOfflineSubsByTestId(testId);
        // fetch online
        List<TestSubmission> onlineList = submissionRepository.findOnlineSubsByTestId(testId);

        long offlinePass = offlineList.stream()
                .filter(off -> Boolean.TRUE.equals(off.getIsPass()))
                .count();
        long onlinePass = onlineList.stream()
                .filter(on -> Boolean.TRUE.equals(on.getIsPass()))
                .count();

        return offlinePass + onlinePass;
    }

    /**
     * This method will compute subject-level stats:
     * pass%, average score, highest & lowest for a specific subject in a test.
     * Implementation depends heavily on how you store subject-level info
     * for each submission (online + offline).
     */
    private SubjectStats computeSubjectStats(Integer testId, SubjectMaster subject) {
        // We'll define a small container class SubjectStats for convenience
        SubjectStats stats = new SubjectStats();

        // 1) fetch all offline + online submissions for this test
        List<TestOfflineSubmission> offSubs = testOfflineSubmissionRepository.findOfflineSubsByTestId(testId);
        List<TestSubmission> onSubs = submissionRepository.findOnlineSubsByTestId(testId);

        // 2) Filter or compute for the given subject
        // If you store subject-level marks in question-level data or in a subject-grades map,
        // you must parse that. We'll do a placeholder approach.

        int totalCount = 0;
        int passCount = 0;
        double sumScores = 0.0;
        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;

        // Example for offline:
        // If you store subject wise in "grades" map: e.g. off.getGrades().get(subject.getSubjectName())
        for (TestOfflineSubmission off : offSubs) {
            // does this offline submission have a grade for subject?
            Integer subjectMarks = off.getGrades().get(subject.getSubjectName());
            if (subjectMarks != null) {
                totalCount++;
                sumScores += subjectMarks;
                if (subjectMarks > highest) highest = subjectMarks;
                if (subjectMarks < lowest) lowest = subjectMarks;

                // pass if subjectMarks >= 40 (example)
                if (subjectMarks >= 40) passCount++;
            }
        }

        // Example for online:
        // If you have question-level data or a single testSubmission score for subject
        // We'll do a placeholder: if test has multiple subjects, it's tricky
        // We'll just compare sub.getScore() if there's only one subject.
        // If multiple subjects share sub, you might need question-level references.
        for (TestSubmission on : onSubs) {
            // If the test is for multiple subjects, you might have to separate out partial scoring
            // For demonstration, we'll skip or do a naive approach:
            // We'll assume the entire test is for one subject if there's only one subject.
            // Or if you store a single subject in sub's test.
            // We'll do a naive check:
            if (on.getTest().getSubjectMaster().contains(subject)) {
                // Then the entire on.getScore() belongs to this subject
                totalCount++;
                double score = on.getScore(); // e.g. 85 out of 100
                sumScores += score;
                if (score > highest) highest = (int)score;
                if (score < lowest) lowest = (int)score;

                // pass if score >= 40
                if (score >= 40) passCount++;
            }
        }

        // 3) fill the stats
        if (totalCount == 0) {
            stats.setPassPercentage(0.0);
            stats.setAvgScore(0.0);
            stats.setHighestScore(0);
            stats.setLowestScore(0);
        } else {
            double passPerc = (passCount / (double) totalCount) * 100.0;
            double avg = sumScores / totalCount;
            stats.setPassPercentage(passPerc);
            stats.setAvgScore(avg);
            stats.setHighestScore(highest == Integer.MIN_VALUE ? 0 : highest);
            stats.setLowestScore(lowest == Integer.MAX_VALUE ? 0 : lowest);
        }
        return stats;
    }

    // A small helper class for returning computed stats
    private static class SubjectStats {
        private double passPercentage;
        private double avgScore;
        private int highestScore;
        private int lowestScore;

        // getters & setters
        public double getPassPercentage() { return passPercentage; }
        public void setPassPercentage(double pp) { this.passPercentage = pp; }

        public double getAvgScore() { return avgScore; }
        public void setAvgScore(double as) { this.avgScore = as; }

        public int getHighestScore() { return highestScore; }
        public void setHighestScore(int hs) { this.highestScore = hs; }

        public int getLowestScore() { return lowestScore; }
        public void setLowestScore(int ls) { this.lowestScore = ls; }
    }

//    public List<StudentPerformanceDTO> getAllStudentsPerformance(Long teacherId) {
//        // 1) Fetch all student IDs for this teacher
//        List<Long> studentIds = userRepository.findStudentsByTeacherId(teacherId);
//        if (studentIds.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        // 2) Build each student’s DTO in parallel
//        //    getStudentPerformance(...) is your existing logic for a single student
//        //    that returns a StudentPerformanceDTO
//        return studentIds.parallelStream()
//                .map(this::getStudentPerformance)   // parallel map
//                .collect(Collectors.toList());
//    }


    public StudentPerformanceDTO getStudentPerformance(Long studentId) {
        // 1) Fetch the student (User)
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        // 2) Basic info
        StudentPerformanceDTO dto = new StudentPerformanceDTO();
        dto.setId(student.getId());
        // For rollNo, we might store it in `student.getStudentId()` or `className` or a separate field
        dto.setRollNo(student.getStudentId() != null ? String.valueOf(student.getStudentId()) : "001");
        String fullName = (student.getFirstName() != null ? student.getFirstName() : "")
                + " "
                + (student.getLastName() != null ? student.getLastName() : "");
        dto.setName(fullName.trim());
        dto.setClazz(student.getClassName() != null ? student.getClassName() : "X-A");

        // 3) Compute average across all tests
        //    We'll fetch all online + offline submissions for this student, compute average
        List<TestSubmission> onlineSubs = submissionRepository.findByUser_Id(studentId);
        List<TestOfflineSubmission> offlineSubs = testOfflineSubmissionRepository.findByStudent_Id(studentId);

        double totalScore = 0.0;
        int count = 0;

        for (TestSubmission sub : onlineSubs) {
            totalScore += sub.getScore(); // assume out of 100
            count++;
        }
        for (TestOfflineSubmission off : offlineSubs) {
            totalScore += off.getScore(); // assume out of 100
            count++;
        }
        double average = (count == 0) ? 0.0 : totalScore / count;
        dto.setAverage(average);

        // 4) Status, based on average
        String status = "Needs Improvement";
        if (average >= 90) status = "Excellent";
        else if (average >= 80) status = "Very Good";
        else if (average >= 70) status = "Good";
        dto.setStatus(status);

        // 5) Attendance (placeholder logic)
        // Suppose attendance is (# tests submitted / # tests assigned) * 100
        // If you store # assigned somewhere, or from teacher's test count, adapt as needed
        // We'll do a simple approach: the student had "count" submissions, plus we guess 20 total
        int totalTestsPossible = 20;
        double attendance = (double) count / totalTestsPossible * 100.0;
        dto.setAttendance(attendance);

        // 6) expand = false by default
        dto.setExpand(false);

        // 7) trend (month wise score)
        // For demonstration, let's say we group the student's submissions by month, average them
        Map<Integer, List<Double>> monthToScores = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            monthToScores.put(m, new ArrayList<>());
        }

        // Collect online monthly
        for (TestSubmission sub : onlineSubs) {
            int month = getSubmissionMonth(sub);
            monthToScores.get(month).add(sub.getScore());
        }
        // Collect offline monthly
        for (TestOfflineSubmission off : offlineSubs) {
            int month = getSubmissionMonth(off);
            monthToScores.get(month).add(off.getScore());
        }

        List<MonthScoreDTO> trend = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            List<Double> scores = monthToScores.get(m);
            double sum = 0.0;
            for (double sc : scores) sum += sc;
            double avgMonth = scores.isEmpty() ? 0.0 : sum / scores.size();

            Month monthEnum = Month.of(m); // e.g. 1 -> JAN, 2 -> FEB
            String shortName = monthEnum.name().substring(0,1) + monthEnum.name().substring(1,3).toLowerCase();
            // e.g. "Jan", "Feb"
            trend.add(new MonthScoreDTO(shortName, avgMonth));
        }
        dto.setTrend(trend);

        // 8) results -> subject wise in all test submissions
        // We'll build SubjectResultDTO from each test that the student took
        List<SubjectResultDTO> results = new ArrayList<>();

        // Online detail
        for (TestSubmission sub : onlineSubs) {
            // For demonstration, we treat each submission as a single subject test
            // If test can have multiple subjects, adapt accordingly
            // We'll also assume "marks" is sub.getScore()
            // We'll store total = 100 as a placeholder
            SubjectResultDTO r = new SubjectResultDTO();
            r.setId(sub.getTestSubmissionId());
            r.setSubject(extractSubjectName(sub.getTest()));
            r.setMarks(sub.getScore());
            r.setTotal(100.0);
            r.setGrade(calculateGrade(sub.getScore()));
            r.setDate(formatTestDateTime(sub.getSubmittedAt()));
            r.setImprovement(Math.random() * 10);

            results.add(r);
        }

        // Offline detail
        for (TestOfflineSubmission off : offlineSubs) {
            SubjectResultDTO r = new SubjectResultDTO();
            r.setId(off.getId());
            r.setSubject(extractSubjectName(off.getTest()));
            r.setMarks(off.getScore());
            r.setTotal(100.0);
            r.setGrade(calculateGrade(off.getScore()));
            // For offline, we may not have an exact submission time, so let's use test date
            if (off.getTest().getTestDate() != null) {
                r.setDate(formatDate(off.getTest().getTestDate()));
            } else {
                r.setDate("N/A");
            }
            r.setImprovement(Math.random() * 10);

            results.add(r);
        }

        dto.setResults(results);

        // 9) Return as a list of one item
        return dto;
    }

    /**
     * Suppose we get the subject name from test.getSubjectMaster().
     * If multiple subjects, you might join them with a comma or pick the first.
     */
    private String extractSubjectName(TestMaster test) {
        if (test.getSubjectMaster() != null && !test.getSubjectMaster().isEmpty()) {
            return test.getSubjectMaster().get(0).getSubjectName();
        }
        return "N/A";
    }


    // For offline or online, find the month from the submission or test date
    private int getSubmissionMonth(TestSubmission sub) {
        if (sub.getSubmittedAt() == null) return 1;
        return sub.getSubmittedAt().getMonthValue(); // 1..12
    }

    private int getSubmissionMonth(TestOfflineSubmission off) {
        // We might not store a submission date, so fallback to test date
        if (off.getTest().getTestDate() == null) return 1;
        return off.getTest().getTestDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .getMonthValue();
    }

    private String formatTestDateTime(java.time.LocalDateTime dt) {
        if (dt == null) return "N/A";
        return dt.toString(); // or format as needed
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "N/A";
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }

}
