package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.DuplicateQuestionRequest;
import com.bezkoder.springjwt.payload.request.QuestionFilterDTO;
import com.bezkoder.springjwt.payload.request.QuestionRequest;
import com.bezkoder.springjwt.payload.request.ShuffleQuestionReq;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.QuestionResponse;
import com.bezkoder.springjwt.payload.response.QuestionResponse1;
import com.bezkoder.springjwt.payload.response.ShuffleQuestionsResponse;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.QuestionService;
import com.bezkoder.springjwt.spec.QuestionMasterSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private ChapterRepository chapterRepository;
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
    private EntranceExamRepository entranceExamRepository;
    @Autowired
    private PatternRepository patternRepository;

    @Override
    public MainResponse create(QuestionRequest questionRequest) {
        System.out.println("QR = " + questionRequest);
        System.out.println("Pattern Id = " + questionRequest.getPatternId());
        MainResponse mainResponse = new MainResponse();
        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(this.entranceExamRepository.findById(questionRequest.getEntranceExamId()).orElseThrow(() -> new RuntimeException("Entrance Exam not found.")));
        Optional<User> teacher = Optional.ofNullable(this.userRepository.findById(questionRequest.getId()).orElseThrow(() -> new RuntimeException("Teacher not found")));
        Optional<StandardMaster> standardMaster = Optional.ofNullable(this.standardRepository.findById(questionRequest.getStandardId()).orElseThrow(() -> new RuntimeException("Standard not found")));
        Optional<SubjectMaster> subjectMaster = Optional.ofNullable(this.subjectRepository.findById(questionRequest.getSubjectId()).orElseThrow(() -> new RuntimeException("Subject not found")));
        Optional<ChapterMaster> chapterMaster = Optional.ofNullable(this.chapterRepository.findById(questionRequest.getChapterId()).orElseThrow(() -> new RuntimeException("Chapter not found")));

        Optional<QuestionLevel> questionLevel = Optional.empty();
        if (questionRequest.getQuestionLevelId() != null) {
            questionLevel = Optional.ofNullable(this.questionLevelRepository.findById(questionRequest.getQuestionLevelId()).orElseThrow(() -> new RuntimeException("Question level not found")));
        }

        Optional<QuestionType> questionType = Optional.ofNullable(this.questionTypeRepository.findById(questionRequest.getQuestionTypeId()).orElseThrow(() -> new RuntimeException("Question type not found")));
        Optional<PatternMaster> patternMaster = this.patternRepository.findById(questionRequest.getPatternId());

        QuestionMaster questionMaster = new QuestionMaster();
        try {
            BeanUtils.copyProperties(questionRequest, questionMaster);
            questionMaster.setDate(new Date());
            questionMaster.setEntranceExamMaster(entranceExamMaster.get());
            questionMaster.setUser(teacher.get());
            questionMaster.setSubjectMaster(subjectMaster.get());
            questionMaster.setStandardMaster(standardMaster.get());
            questionMaster.setChapterMaster(chapterMaster.get());

            if (questionRequest.getTopicId()!=null){
                Optional<TopicMaster> topicMaster = this.topicRepository.findById(questionRequest.getTopicId());
                questionMaster.setTopicMaster(topicMaster.get());
            }else {
                questionMaster.setTopicMaster(null);
            }

            if (questionRequest.getSubTopicId()!=null){
                Optional<SubTopicMaster> subTopicMaster = this.subTopicRepository.findById(questionRequest.getSubTopicId());
                questionMaster.setSubTopicMaster(subTopicMaster.get());
            }else {
                questionMaster.setSubTopicMaster(null);
            }

            if (questionRequest.getYearOfAppearanceId()!=null){
                Optional<YearOfAppearance> yearOfAppearance = this.yearOfAppearanceRepository.findById(questionRequest.getYearOfAppearanceId());
                questionMaster.setAsked(true);
                questionMaster.setYearOfAppearance(yearOfAppearance.get());
            }else {
                questionMaster.setYearOfAppearance(null);
                questionMaster.setAsked(false);
            }

            if (questionLevel.isPresent()) {
                questionMaster.setQuestionLevel(questionLevel.get());
            } else {
                questionMaster.setQuestionLevel(null);
            }

            questionMaster.setQuestionType(questionType.get());
            questionMaster.setMultiAnswers(questionRequest.getMultiAnswers());

            if (patternMaster.isPresent()) {
                questionMaster.setPatternMaster(patternMaster.get());
            } else {
                questionMaster.setPatternMaster(null);
            }

            questionMaster.setSolution(questionRequest.getSolution());
            questionMaster.setQuestionCategory(questionRequest.getQuestionCategory());

            this.questionRepository.save(questionMaster);
            mainResponse.setMessage("Question save successfully");
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
    public MainResponse update(QuestionRequest questionRequest) {
        MainResponse mainResponse = new MainResponse();

        Optional<EntranceExamMaster> entranceExamMaster = Optional.ofNullable(
                this.entranceExamRepository.findById(questionRequest.getEntranceExamId())
                        .orElseThrow(() -> new RuntimeException("Entrance Exam not found."))
        );

        Optional<User> teacher = Optional.ofNullable(
                this.userRepository.findById(questionRequest.getId())
                        .orElseThrow(() -> new RuntimeException("Teacher not found"))
        );

        Optional<StandardMaster> standardMaster = Optional.ofNullable(
                this.standardRepository.findById(questionRequest.getStandardId())
                        .orElseThrow(() -> new RuntimeException("Standard not found"))
        );

        Optional<SubjectMaster> subjectMaster = Optional.ofNullable(
                this.subjectRepository.findById(questionRequest.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("Subject not found"))
        );

        Optional<ChapterMaster> chapterMaster = Optional.ofNullable(
                this.chapterRepository.findById(questionRequest.getChapterId())
                        .orElseThrow(() -> new RuntimeException("Chapter not found"))
        );

        // FIXED: Handle null questionLevelId properly
        Optional<QuestionLevel> questionLevel = Optional.empty();
        if (questionRequest.getQuestionLevelId() != null) {
            questionLevel = Optional.ofNullable(
                    this.questionLevelRepository.findById(questionRequest.getQuestionLevelId())
                            .orElseThrow(() -> new RuntimeException("Question level not found"))
            );
        }

        Optional<QuestionType> questionType = Optional.ofNullable(
                this.questionTypeRepository.findById(questionRequest.getQuestionTypeId())
                        .orElseThrow(() -> new RuntimeException("Question type not found"))
        );

        // FIXED: Remove .get() without checking
        Optional<PatternMaster> patternMaster = this.patternRepository.findById(questionRequest.getPatternId());

        QuestionMaster questionMaster = this.questionRepository.findById(questionRequest.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionRequest.getQuestionId()));

        try {
            BeanUtils.copyProperties(questionRequest, questionMaster);
            questionMaster.setDate(new Date());
            questionMaster.setEntranceExamMaster(entranceExamMaster.get());
            questionMaster.setUser(teacher.get());
            questionMaster.setSubjectMaster(subjectMaster.get());
            questionMaster.setStandardMaster(standardMaster.get());
            questionMaster.setChapterMaster(chapterMaster.get());

            if (questionRequest.getTopicId() != null) {
                Optional<TopicMaster> topicMaster = this.topicRepository.findById(questionRequest.getTopicId());
                if (topicMaster.isPresent()) {
                    questionMaster.setTopicMaster(topicMaster.get());
                } else {
                    questionMaster.setTopicMaster(null);
                }
            } else {
                questionMaster.setTopicMaster(null);
            }

            if (questionRequest.getSubTopicId() != null) {
                Optional<SubTopicMaster> subTopicMaster = this.subTopicRepository.findById(questionRequest.getSubTopicId());
                if (subTopicMaster.isPresent()) {
                    questionMaster.setSubTopicMaster(subTopicMaster.get());
                } else {
                    questionMaster.setSubTopicMaster(null);
                }
            } else {
                questionMaster.setSubTopicMaster(null);
            }

            if (questionRequest.getYearOfAppearanceId() != null) {
                Optional<YearOfAppearance> yearOfAppearance = this.yearOfAppearanceRepository.findById(questionRequest.getYearOfAppearanceId());
                if (yearOfAppearance.isPresent()) {
                    questionMaster.setYearOfAppearance(yearOfAppearance.get());
                    questionMaster.setAsked(true);
                } else {
                    questionMaster.setYearOfAppearance(null);
                    questionMaster.setAsked(false);
                }
            } else {
                questionMaster.setYearOfAppearance(null);
                questionMaster.setAsked(false);
            }

            // FIXED: Only set questionLevel if it exists
            if (questionLevel.isPresent()) {
                questionMaster.setQuestionLevel(questionLevel.get());
            } else {
                questionMaster.setQuestionLevel(null);
            }

            questionMaster.setQuestionType(questionType.get());
            questionMaster.setMultiAnswers(questionRequest.getMultiAnswers());

            // FIXED: Handle patternMaster properly
            if (patternMaster.isPresent()) {
                questionMaster.setPatternMaster(patternMaster.get());
            } else {
                questionMaster.setPatternMaster(null);
            }

            questionMaster.setSolution(questionRequest.getSolution());
            questionMaster.setQuestionCategory(questionRequest.getQuestionCategory());
            questionMaster.setAsked(questionRequest.getAsked());

            this.questionRepository.save(questionMaster);
            mainResponse.setMessage("Question updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            mainResponse.setMessage("Something went wrong: " + e.getMessage());
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public QuestionResponse questionById1(Integer questionId) {
        QuestionResponse questionResponse = new QuestionResponse();
        QuestionMaster questionMaster = questionRepository.findById(questionId).orElse(null);

        if (questionMaster != null) {
            BeanUtils.copyProperties(questionMaster, questionResponse);
            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }
            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }
            questionResponse.setQuestionCategory(questionMaster.getQuestionCategory());
        }

        return questionResponse;
    }

    @Override
    public List<QuestionResponse> getAllActiveQuestions() {
        List<QuestionResponse> questionResponses = new ArrayList<>();

        List<QuestionMaster> questionMaster = this.questionRepository.getAllActiveQuestions();
        for (QuestionMaster master : questionMaster) {
            System.out.println("Master = " + master);
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(master.getQuestionId());

            if (master.getStandardMaster() != null) {
                questionResponse.setStandardName(master.getStandardMaster().getStandardName());
            }

            if (master.getChapterMaster() != null) {
                questionResponse.setChapterName(master.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(master.getMarks());
            questionResponse.setQuestion(master.getQuestion());

            if (master.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(master.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setMultiAnswers(master.getMultiAnswers());
            questionResponse.setOption1(master.getOption1());
            questionResponse.setOption2(master.getOption2());
            questionResponse.setOption3(master.getOption3());
            questionResponse.setOption4(master.getOption4());
            questionResponse.setDate(master.getDate());
            questionResponse.setStatus(master.getStatus());
            questionResponse.setSolution(master.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> chapterWiseQuestions(Integer chapterId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();

        List<QuestionMaster> questionMaster = this.questionRepository.questionByChapterId(chapterId);
        for (QuestionMaster master : questionMaster) {
            System.out.println("Master = " + master);
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(master.getQuestionId());

            if (master.getStandardMaster() != null) {
                questionResponse.setStandardName(master.getStandardMaster().getStandardName());
            }

            if (master.getChapterMaster() != null) {
                questionResponse.setChapterName(master.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(master.getMarks());
            questionResponse.setQuestion(master.getQuestion());

            if (master.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(master.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setMultiAnswers(master.getMultiAnswers());
            questionResponse.setOption1(master.getOption1());
            questionResponse.setOption2(master.getOption2());
            questionResponse.setOption3(master.getOption3());
            questionResponse.setOption4(master.getOption4());
            questionResponse.setDate(master.getDate());
            questionResponse.setStatus(master.getStatus());
            questionResponse.setSolution(master.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public Integer allQuestionsCount() {
        Integer count = this.questionRepository.allQuestionsCount();
        return count;
    }

    @Override
    public List<QuestionResponse1> getAllByStatus(String status) {
        List<QuestionResponse1> questionResponses = new ArrayList<>();

        List<QuestionMaster> questionMasters = this.questionRepository.getAllByStatus(status);

        for (QuestionMaster master : questionMasters) {
            System.out.println("Master = " + master);

            QuestionResponse1 questionResponse1 = new QuestionResponse1();

            if (master.getUser() != null) {
                questionResponse1.setId(master.getUser().getId());
            }

            questionResponse1.setQuestionId(master.getQuestionId());

            if (master.getSubjectMaster() != null) {
                questionResponse1.setSubjectId(master.getSubjectMaster().getSubjectId());
                questionResponse1.setSubjectName(master.getSubjectMaster().getSubjectName());
            }

            if (master.getEntranceExamMaster() != null) {
                questionResponse1.setEntranceExamId(master.getEntranceExamMaster().getEntranceExamId());
                questionResponse1.setEntranceExamName(master.getEntranceExamMaster().getEntranceExamName());
            }

            if (master.getStandardMaster() != null) {
                questionResponse1.setStandardId(master.getStandardMaster().getStandardId());
                questionResponse1.setStandardName(master.getStandardMaster().getStandardName());
            }

            if (master.getChapterMaster() != null) {
                questionResponse1.setChapterId(master.getChapterMaster().getChapterId());
                questionResponse1.setChapterName(master.getChapterMaster().getChapterName());
            }

            if (master.getTopicMaster() != null) {
                questionResponse1.setTopicId(master.getTopicMaster().getTopicId());
                questionResponse1.setTopicName(master.getTopicMaster().getTopicName());
            }

            if (master.getSubTopicMaster() != null) {
                questionResponse1.setSubTopicId(master.getSubTopicMaster().getSubTopicId());
                questionResponse1.setSubTopicName(master.getSubTopicMaster().getSubTopicName());
            }

            if (master.getQuestionType() != null) {
                questionResponse1.setQuestionTypeId(master.getQuestionType().getQuestionTypeId());
                questionResponse1.setQuestionType(master.getQuestionType().getQuestionType());
            }

            // FIXED: Handle null QuestionLevel
            QuestionLevel questionLevel = master.getQuestionLevel();
            if (questionLevel != null) {
                questionResponse1.setQuestionLevelId(questionLevel.getQuestionLevelId());
                questionResponse1.setQuestionLevel(questionLevel.getQuestionLevel());
            }

            questionResponse1.setMarks(master.getMarks());
            questionResponse1.setQuestion(master.getQuestion());
            questionResponse1.setAnswer(master.getAnswer());
            questionResponse1.setExplanation(master.getExplanation());

            if (master.getPatternMaster() != null) {
                questionResponse1.setPatternId(master.getPatternMaster().getPatternId());
                questionResponse1.setPatternName(master.getPatternMaster().getPatternName());
            }

            if (master.getYearOfAppearance() != null) {
                questionResponse1.setYearOfAppearanceId(master.getYearOfAppearance().getYearOfAppearanceId());
                questionResponse1.setYearOfAppearance(master.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse1.setMultiAnswers(master.getMultiAnswers());
            questionResponse1.setOption1(master.getOption1());
            questionResponse1.setOption2(master.getOption2());
            questionResponse1.setOption3(master.getOption3());
            questionResponse1.setOption4(master.getOption4());
            questionResponse1.setStatus(master.getStatus());
            questionResponse1.setSolution(master.getSolution());

            questionResponses.add(questionResponse1);
        }

        return questionResponses;
    }

    @Override
    public List<QuestionResponse1> getByUserIdAndStatus(Long id, String status) {
        List<QuestionResponse1> questionResponses = new ArrayList<>();

        List<QuestionMaster> questionMaster = this.questionRepository.getByUserIdAndStatus(id, status);
        for (QuestionMaster master : questionMaster) {
            QuestionResponse1 questionResponse1 = new QuestionResponse1();

            // Handle User
            if (master.getUser() != null) {
                questionResponse1.setId(master.getUser().getId());
            }

            questionResponse1.setQuestionId(master.getQuestionId());

            // Handle Subject
            if (master.getSubjectMaster() != null) {
                questionResponse1.setSubjectId(master.getSubjectMaster().getSubjectId());
                questionResponse1.setSubjectName(master.getSubjectMaster().getSubjectName());
            }

            // Handle Entrance Exam
            if (master.getEntranceExamMaster() != null) {
                questionResponse1.setEntranceExamId(master.getEntranceExamMaster().getEntranceExamId());
                questionResponse1.setEntranceExamName(master.getEntranceExamMaster().getEntranceExamName());
            }

            // Handle Standard
            if (master.getStandardMaster() != null) {
                questionResponse1.setStandardId(master.getStandardMaster().getStandardId());
                questionResponse1.setStandardName(master.getStandardMaster().getStandardName());
            }

            // Handle Chapter
            if (master.getChapterMaster() != null) {
                questionResponse1.setChapterId(master.getChapterMaster().getChapterId());
                questionResponse1.setChapterName(master.getChapterMaster().getChapterName());
            }

            // Handle Topic
            TopicMaster topicMasterEntity = master.getTopicMaster();
            if (topicMasterEntity != null){
                questionResponse1.setTopicId(topicMasterEntity.getTopicId());
                questionResponse1.setTopicName(topicMasterEntity.getTopicName());
            }

            // Handle SubTopic
            SubTopicMaster subTopicMasterEntity = master.getSubTopicMaster();
            if (subTopicMasterEntity != null){
                questionResponse1.setSubTopicId(subTopicMasterEntity.getSubTopicId());
                questionResponse1.setSubTopicName(subTopicMasterEntity.getSubTopicName());
            }

            // Handle QuestionType
            if (master.getQuestionType() != null) {
                questionResponse1.setQuestionTypeId(master.getQuestionType().getQuestionTypeId());
                questionResponse1.setQuestionType(master.getQuestionType().getQuestionType());
            }

            // FIXED: Handle null QuestionLevel
            QuestionLevel questionLevel = master.getQuestionLevel();
            if (questionLevel != null) {
                questionResponse1.setQuestionLevelId(questionLevel.getQuestionLevelId());
                questionResponse1.setQuestionLevel(questionLevel.getQuestionLevel());
            } else {
                questionResponse1.setQuestionLevelId(null);
                questionResponse1.setQuestionLevel(null);
            }

            questionResponse1.setMarks(master.getMarks());
            questionResponse1.setQuestion(master.getQuestion());
            questionResponse1.setAnswer(master.getAnswer());
            questionResponse1.setExplanation(master.getExplanation());

            // Handle PatternMaster
            if (master.getPatternMaster() != null) {
                questionResponse1.setPatternId(master.getPatternMaster().getPatternId());
                questionResponse1.setPatternName(master.getPatternMaster().getPatternName());
            }

            questionResponse1.setSolution(master.getSolution());

            // Handle YearOfAppearance
            YearOfAppearance yearOfAppearance = master.getYearOfAppearance();
            if (yearOfAppearance != null){
                questionResponse1.setYearOfAppearanceId(yearOfAppearance.getYearOfAppearanceId());
                questionResponse1.setYearOfAppearance(yearOfAppearance.getYearOfAppearance());
            }

            questionResponse1.setMultiAnswers(master.getMultiAnswers());
            questionResponse1.setOption1(master.getOption1());
            questionResponse1.setOption2(master.getOption2());
            questionResponse1.setOption3(master.getOption3());
            questionResponse1.setOption4(master.getOption4());
            questionResponse1.setStatus(master.getStatus());
            questionResponse1.setSolution(master.getSolution());

            questionResponses.add(questionResponse1);
        }
        return questionResponses;
    }

    @Override
    public MainResponse acceptOrRejectQuestion(Integer questionId, String status) {
        MainResponse mainResponse = new MainResponse();
        QuestionMaster questionMaster = this.questionRepository.findById(questionId).orElse(null);
        System.out.println("QM = " + questionMaster);
        if (questionMaster != null) {
            if (status.equals("Accept")) {
                questionMaster.setStatus("Accepted");
                this.questionRepository.save(questionMaster);
                mainResponse.setMessage("Question Accepted");
                mainResponse.setFlag(true);
                mainResponse.setResponseCode(HttpStatus.OK.value());
            } else if (status.equals("Reject")) {
                questionMaster.setStatus("Rejected");
                this.questionRepository.save(questionMaster);
                mainResponse.setMessage("Question Rejected");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } else {
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        } else {
            mainResponse.setMessage("Question not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public MainResponse delete(Integer questionId) {
        MainResponse mainResponse = new MainResponse();
        Optional<QuestionMaster> questionMaster = this.questionRepository.findById(questionId);
        if (questionMaster.isPresent()) {
            this.questionRepository.deleteById(questionId);
            mainResponse.setMessage("Question Deleted Successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        } else {
            mainResponse.setMessage("Question not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    // For delete all questions
    @Override
    @Transactional
    public MainResponse deleteAllQuestions() {
        try {
            questionRepository.deleteAllQuestions();
            return new MainResponse("All questions deleted successfully", 200, true);
        } catch (Exception e) {
            e.printStackTrace();
            return new MainResponse("Something went wrong: " + e.getMessage(), 500, false);
        }
    }

    @Override
    public Integer chapterWiseQuestionCount(Integer chapterId) {
        Integer questionCount = this.questionRepository.chapterWiseQuestionCount(chapterId);
        return questionCount;
    }

    @Override
    public Integer subjectWiseQuestionCount(Integer subjectId) {
        Integer questionCount = this.questionRepository.subjectWiseQuestionCount(subjectId);
        return questionCount;
    }

    @Override
    public Integer entranceExamWiseQuestionCount(Integer entranceExamId) {
        Integer questionCount = this.questionRepository.entranceExamWiseQuestionCount(entranceExamId);
        return questionCount;
    }

    @Override
    public List<QuestionResponse> entranceExamWiseQuestions(Integer entranceExamId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.getByEntranceExamWiseQuestions(entranceExamId);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> subjectWiseQuestions(Integer subjectId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.getBySubjectWiseQuestions(subjectId);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> topicWiseQuestions(Integer topicId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.topicWiseQuestions(topicId);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> subTopicWiseQuestions(Integer subTopicId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.subTopicWiseQuestions(subTopicId);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> questionTypeWiseQuestions(Integer questionTypeId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.questionTypeWiseQuestions(questionTypeId);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> questionLevelWiseQuestions(Integer questionLevelId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.questionLevelWiseQuestions(questionLevelId);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> patternWiseQuestion(Integer patternId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.patternWiseQuestion(patternId);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> yearOfAppearanceWiseQuestions(Integer yearOfAppearanceId) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.yearOfAppearanceWiseQuestions(yearOfAppearanceId);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public List<QuestionResponse> questionCategoryWiseQuestions(Integer questionCategory) {
        List<QuestionResponse> questionResponses = new ArrayList<>();
        List<QuestionMaster> questionMasters = this.questionRepository.questionCategoryWiseQuestions(questionCategory);
        System.out.println("SIZE = " + questionMasters.size());
        for (QuestionMaster questionMaster : questionMasters) {
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setQuestionId(questionMaster.getQuestionId());

            if (questionMaster.getStandardMaster() != null) {
                questionResponse.setStandardName(questionMaster.getStandardMaster().getStandardName());
            }

            if (questionMaster.getChapterMaster() != null) {
                questionResponse.setChapterName(questionMaster.getChapterMaster().getChapterName());
            }

            questionResponse.setMarks(questionMaster.getMarks());
            questionResponse.setQuestion(questionMaster.getQuestion());
            questionResponse.setOption1(questionMaster.getOption1());
            questionResponse.setOption2(questionMaster.getOption2());
            questionResponse.setOption3(questionMaster.getOption3());
            questionResponse.setOption4(questionMaster.getOption4());

            if (questionMaster.getYearOfAppearance() != null) {
                questionResponse.setYearOfAppearance(questionMaster.getYearOfAppearance().getYearOfAppearance());
            }

            questionResponse.setDate(questionMaster.getDate());
            questionResponse.setStatus(questionMaster.getStatus());
            questionResponse.setMultiAnswers(questionMaster.getMultiAnswers());
            questionResponse.setSolution(questionMaster.getSolution());
            questionResponses.add(questionResponse);
        }
        return questionResponses;
    }

    @Override
    public MainResponse findDuplicateQuestions(String question) {
        MainResponse mainResponse = new MainResponse();

        QuestionMaster questionMaster = questionRepository.findByQuestion(question);

        if (questionMaster!=null){
            mainResponse.setMessage("Match has been found");
            mainResponse.setResponseCode(HttpStatus.IM_USED.value());
            mainResponse.setFlag(true);
        }else {
            mainResponse.setMessage("No match found");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }
        return mainResponse;
    }

    @Override
    public List asked() {
        List<QuestionMaster> questionMasterList = questionRepository.getAllByAsk(true);
        return questionMasterList;
    }

    @Override
    public List<QuestionMaster> entranceAndStandardWiseQuestions(Integer entranceExamId, Integer standardId) {
        System.out.println("ENT = "+entranceExamId);
        System.out.println("STa = "+standardId);
        List<QuestionMaster> questionResponseList = this.questionRepository.entranceAndStandardWiseQuestions(entranceExamId,standardId);
        System.out.println("SIZE = "+questionResponseList.size());
        return questionResponseList;
    }

    @Override
    public List<ShuffleQuestionsResponse> shuffleQuestions(ShuffleQuestionReq shuffleQuestionReq) {
        List<Integer> questionList = shuffleQuestionReq.getQuestionsId();
        Random random = new Random();
        Collections.shuffle(questionList, random);

        List<ShuffleQuestionsResponse> response = new ArrayList<>();
        for(Integer queID : questionList ){
            QuestionMaster question = questionRepository.findById(queID)
                    .orElseThrow(() -> new RuntimeException("Question not found for ID: " + queID));

            ShuffleQuestionsResponse shuffleResponse = new ShuffleQuestionsResponse(
                    question.getQuestionId(),
                    question.getQuestion(),
                    question.getOption1(),
                    question.getOption2(),
                    question.getOption3(),
                    question.getOption4(),
                    question.getAnswer(),
                    question.getExplanation()
            );

            response.add(shuffleResponse);
        }

        return response;
    }

    @Override
    public Page<QuestionResponse1> getQuestionsByFilter(QuestionFilterDTO filterDTO, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<QuestionMaster> responseFromDao = questionRepository.findAll(
                QuestionMasterSpecification.getQuestionsByFilter(filterDTO),
                pageable
        );

        List<QuestionResponse1> questionResponses = responseFromDao.getContent().stream()
                .map(master -> {
                    if (master == null) return null;

                    QuestionResponse1 questionResponse = new QuestionResponse1();
                    questionResponse.setQuestionId(master.getQuestionId());
                    questionResponse.setQuestion(master.getQuestion());
                    questionResponse.setMarks(master.getMarks());
                    questionResponse.setStatus(master.getStatus());
                    questionResponse.setSolution(master.getSolution());
                    questionResponse.setExplanation(master.getExplanation());
                    questionResponse.setAnswer(master.getAnswer());
                    questionResponse.setMultiAnswers(master.getMultiAnswers());

                    if (master.getUser() != null) {
                        questionResponse.setId(master.getUser().getId());
                    }

                    if (master.getEntranceExamMaster() != null) {
                        questionResponse.setEntranceExamId(master.getEntranceExamMaster().getEntranceExamId());
                        questionResponse.setEntranceExamName(master.getEntranceExamMaster().getEntranceExamName());
                    }

                    if (master.getStandardMaster() != null) {
                        questionResponse.setStandardId(master.getStandardMaster().getStandardId());
                        questionResponse.setStandardName(master.getStandardMaster().getStandardName());
                    }

                    if (master.getSubjectMaster() != null) {
                        questionResponse.setSubjectId(master.getSubjectMaster().getSubjectId());
                        questionResponse.setSubjectName(master.getSubjectMaster().getSubjectName());
                    }

                    if (master.getChapterMaster() != null) {
                        questionResponse.setChapterId(master.getChapterMaster().getChapterId());
                        questionResponse.setChapterName(master.getChapterMaster().getChapterName());
                    }

                    if (master.getTopicMaster() != null) {
                        questionResponse.setTopicId(master.getTopicMaster().getTopicId());
                        questionResponse.setTopicName(master.getTopicMaster().getTopicName());
                    }

                    if (master.getSubTopicMaster() != null) {
                        questionResponse.setSubTopicId(master.getSubTopicMaster().getSubTopicId());
                        questionResponse.setSubTopicName(master.getSubTopicMaster().getSubTopicName());
                    }

                    if (master.getYearOfAppearance() != null) {
                        questionResponse.setYearOfAppearanceId(master.getYearOfAppearance().getYearOfAppearanceId());
                        questionResponse.setYearOfAppearance(master.getYearOfAppearance().getYearOfAppearance());
                    }

                    if (master.getQuestionType() != null) {
                        questionResponse.setQuestionTypeId(master.getQuestionType().getQuestionTypeId());
                        questionResponse.setQuestionType(master.getQuestionType().getQuestionType());
                    }

                    if (master.getQuestionLevel() != null) {
                        questionResponse.setQuestionLevel(master.getQuestionLevel().getQuestionLevel());
                    }

                    if (master.getPatternMaster() != null) {
                        questionResponse.setPatternId(master.getPatternMaster().getPatternId());
                        questionResponse.setPatternName(master.getPatternMaster().getPatternName());
                    }

                    if(master.getStatus() != null){
                        questionResponse.setStatus(master.getStatus());
                    }

                    questionResponse.setOption1(master.getOption1());
                    questionResponse.setOption2(master.getOption2());
                    questionResponse.setOption3(master.getOption3());
                    questionResponse.setOption4(master.getOption4());

                    return questionResponse;
                })
                .filter(q -> q != null)
                .collect(Collectors.toList());

        return new PageImpl<>(questionResponses, pageable, responseFromDao.getTotalElements());
    }
}