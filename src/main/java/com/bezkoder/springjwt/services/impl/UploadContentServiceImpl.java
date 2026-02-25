package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.UploadContentRequest;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.UploadContentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UploadContentServiceImpl implements UploadContentService {

    @Autowired
    private UploadContentRepository uploadContentRepository;
    @Autowired
    private EntranceExamRepository entranceExamRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private SubTopicRepository subTopicRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserManagementMasterRepository userManagementMasterRepository;
    @Autowired
    private StudentManagementRepository studentManagementRepository;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public MainResponse upload(UploadContentRequest uploadContentRequest) {
        System.out.println("REQUEST = " + uploadContentRequest);
        MainResponse mainResponse = new MainResponse();
        UploadContentMaster uploadContentMaster = new UploadContentMaster();

        User user = this.userRepository.findById(uploadContentRequest.getUploaderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ------------------- FORMULAS -------------------
        if (uploadContentRequest.getContentType().equals("formulas")) {

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            SubjectMaster subjectMaster = this.subjectRepository.findById(uploadContentRequest.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ChapterMaster chapterMaster = this.chapterRepository.findById(uploadContentRequest.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
            uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            uploadContentMaster.setStandardId(standardMaster.getStandardId());
            uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
            uploadContentMaster.setChapterId(chapterMaster.getChapterId());
            uploadContentMaster.setUrl(uploadContentRequest.getUrl());
            uploadContentMaster.setType(uploadContentRequest.getType());
            uploadContentMaster.setTitle(uploadContentRequest.getTitle());
            uploadContentMaster.setDescription(uploadContentRequest.getDescription());
            uploadContentMaster.setDate(new Date());
            uploadContentMaster.setUploaderId(user.getId());

            try {
                this.uploadContentRepository.save(uploadContentMaster);
                mainResponse.setMessage("Chapter formulae content uploaded successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }

            // ------------------- NOTES -------------------
        } else if (uploadContentRequest.getContentType().equals("notes")) {

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            SubjectMaster subjectMaster = this.subjectRepository.findById(uploadContentRequest.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ChapterMaster chapterMaster = this.chapterRepository.findById(uploadContentRequest.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            Integer topicId = uploadContentRequest.getTopicId();

            if (topicId != null) {
                TopicMaster topicMaster = this.topicRepository.findById(topicId).orElse(null);

                BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
                uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
                uploadContentMaster.setStandardId(standardMaster.getStandardId());
                uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
                uploadContentMaster.setChapterId(chapterMaster.getChapterId());
                uploadContentMaster.setTopicId(topicMaster.getTopicId());
                uploadContentMaster.setUrl(uploadContentRequest.getUrl());
                uploadContentMaster.setType(uploadContentRequest.getType());
                uploadContentMaster.setTitle(uploadContentRequest.getTitle());
                uploadContentMaster.setDescription(uploadContentRequest.getDescription());
                uploadContentMaster.setUploaderId(user.getId());
                uploadContentMaster.setDate(new Date());

                try {
                    this.uploadContentRepository.save(uploadContentMaster);
                    mainResponse.setMessage("Topic wise notes content uploaded successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            } else {
                BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
                uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
                uploadContentMaster.setStandardId(standardMaster.getStandardId());
                uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
                uploadContentMaster.setChapterId(chapterMaster.getChapterId());
                uploadContentMaster.setUrl(uploadContentRequest.getUrl());
                uploadContentMaster.setType(uploadContentRequest.getType());
                uploadContentMaster.setTitle(uploadContentRequest.getTitle());
                uploadContentMaster.setDescription(uploadContentRequest.getDescription());
                uploadContentMaster.setUploaderId(user.getId());
                uploadContentMaster.setDate(new Date());

                try {
                    this.uploadContentRepository.save(uploadContentMaster);
                    mainResponse.setMessage("Chapter wise notes content uploaded successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            }

            // ------------------- QUESTION -------------------
        } else if (uploadContentRequest.getContentType().equals("question")) {

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));

            uploadContentMaster.setContentType(uploadContentRequest.getContentType());
            uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            uploadContentMaster.setUrl(uploadContentRequest.getUrl());
            uploadContentMaster.setUploaderId(user.getId());
            uploadContentMaster.setTitle(uploadContentRequest.getTitle());
            uploadContentMaster.setDate(new Date());
            uploadContentMaster.setExamYear(uploadContentRequest.getExamYear());

            try {
                this.uploadContentRepository.save(uploadContentMaster);
                mainResponse.setMessage("Question content uploaded successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }

            // ------------------- SUMMARY -------------------
        } else if (uploadContentRequest.getContentType().equals("summary")) {

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            SubjectMaster subjectMaster = this.subjectRepository.findById(uploadContentRequest.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ChapterMaster chapterMaster = this.chapterRepository.findById(uploadContentRequest.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            Integer topicId = uploadContentRequest.getTopicId();

            if (topicId != null) {
                TopicMaster topicMaster = this.topicRepository.findById(topicId).orElse(null);

                BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
                uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
                uploadContentMaster.setStandardId(standardMaster.getStandardId());
                uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
                uploadContentMaster.setChapterId(chapterMaster.getChapterId());
                uploadContentMaster.setTopicId(topicMaster.getTopicId());
                uploadContentMaster.setUrl(uploadContentRequest.getUrl());
                uploadContentMaster.setType(uploadContentRequest.getType());
                uploadContentMaster.setTitle(uploadContentRequest.getTitle());
                uploadContentMaster.setDescription(uploadContentRequest.getDescription());
                uploadContentMaster.setUploaderId(user.getId());
                uploadContentMaster.setDate(new Date());

                try {
                    this.uploadContentRepository.save(uploadContentMaster);
                    mainResponse.setMessage("Topic summary content uploaded successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            } else {
                BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
                uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
                uploadContentMaster.setStandardId(standardMaster.getStandardId());
                uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
                uploadContentMaster.setChapterId(chapterMaster.getChapterId());
                uploadContentMaster.setUrl(uploadContentRequest.getUrl());
                uploadContentMaster.setType(uploadContentRequest.getType());
                uploadContentMaster.setTitle(uploadContentRequest.getTitle());
                uploadContentMaster.setDescription(uploadContentRequest.getDescription());
                uploadContentMaster.setUploaderId(user.getId());
                uploadContentMaster.setDate(new Date());

                try {
                    this.uploadContentRepository.save(uploadContentMaster);
                    mainResponse.setMessage("Chapter summary content uploaded successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            }

            // ------------------- VIDEO -------------------
        } else if (uploadContentRequest.getContentType().equals("video")) {

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            SubjectMaster subjectMaster = this.subjectRepository.findById(uploadContentRequest.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ChapterMaster chapterMaster = this.chapterRepository.findById(uploadContentRequest.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
            uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            uploadContentMaster.setStandardId(standardMaster.getStandardId());
            uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
            uploadContentMaster.setChapterId(chapterMaster.getChapterId());
            uploadContentMaster.setUrl(uploadContentRequest.getUrl());
            uploadContentMaster.setType(uploadContentRequest.getType());
            uploadContentMaster.setTitle(uploadContentRequest.getTitle());
            uploadContentMaster.setDescription(uploadContentRequest.getDescription());
            uploadContentMaster.setUploaderId(user.getId());
            uploadContentMaster.setDate(new Date());

            try {
                this.uploadContentRepository.save(uploadContentMaster);
                mainResponse.setMessage("Chapter video content uploaded successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }

            // ------------------- SCHEDULE -------------------
        } else if (uploadContentRequest.getContentType().equals("schedule")) {

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));

            List<StudentManagementMaster> students = this.studentManagementRepository
                    .getStudentsByEntranceAndStandardIdWise(entranceExamMaster.getEntranceExamId(), standardMaster.getStandardId());

            BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
            uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            uploadContentMaster.setStandardId(standardMaster.getStandardId());
            uploadContentMaster.setUrl(uploadContentRequest.getUrl());
            uploadContentMaster.setType(uploadContentRequest.getType());
            uploadContentMaster.setTitle(uploadContentRequest.getTitle());
            uploadContentMaster.setDescription(uploadContentRequest.getDescription());
            uploadContentMaster.setUploaderId(user.getId());
            uploadContentMaster.setDate(new Date());

            for (StudentManagementMaster student : students) {
                User studentDetails = this.userRepository.findById(student.getStudentId()).get();
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setTo(studentDetails.getEmail());
                simpleMailMessage.setFrom("zplushrms@gmail.com");
                simpleMailMessage.setSubject(uploadContentRequest.getTitle());
                simpleMailMessage.setSentDate(new Date());
                simpleMailMessage.setText(uploadContentRequest.getDescription());
            }

            try {
                this.uploadContentRepository.save(uploadContentMaster);
                mainResponse.setMessage("Schedule uploaded successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        }

        return mainResponse;
    }

    @Override
    public MainResponse update(UploadContentRequest uploadContentRequest) {
        MainResponse mainResponse = new MainResponse();
        User user = this.userRepository.findById(uploadContentRequest.getUploaderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (uploadContentRequest.getContentType().equals("formulas")) {

            Optional<UploadContentMaster> uploadContentMaster = this.uploadContentRepository.findById(uploadContentRequest.getUploadContentId());

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            SubjectMaster subjectMaster = this.subjectRepository.findById(uploadContentRequest.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ChapterMaster chapterMaster = this.chapterRepository.findById(uploadContentRequest.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster.get());
            uploadContentMaster.get().setEntranceExamId(entranceExamMaster.getEntranceExamId());
            uploadContentMaster.get().setStandardId(standardMaster.getStandardId());
            uploadContentMaster.get().setSubjectId(subjectMaster.getSubjectId());
            uploadContentMaster.get().setChapterId(chapterMaster.getChapterId());
            uploadContentMaster.get().setUrl(uploadContentRequest.getUrl());
            uploadContentMaster.get().setType(uploadContentRequest.getType());
            uploadContentMaster.get().setTitle(uploadContentRequest.getTitle());
            uploadContentMaster.get().setDescription(uploadContentRequest.getDescription());
            uploadContentMaster.get().setUploaderId(user.getId());
            uploadContentMaster.get().setDate(new Date());

            try {
                this.uploadContentRepository.save(uploadContentMaster.get());
                mainResponse.setMessage("Chapter formulae content updated successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }

        } else if (uploadContentRequest.getContentType().equals("notes")) {

            Optional<UploadContentMaster> uploadContentMaster = this.uploadContentRepository.findById(uploadContentRequest.getUploadContentId());

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            SubjectMaster subjectMaster = this.subjectRepository.findById(uploadContentRequest.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ChapterMaster chapterMaster = this.chapterRepository.findById(uploadContentRequest.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            Integer topicId = uploadContentRequest.getTopicId();

            if (topicId != null) {
                TopicMaster topicMaster = this.topicRepository.findById(topicId).orElse(null);

                BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster.get());
                uploadContentMaster.get().setEntranceExamId(entranceExamMaster.getEntranceExamId());
                uploadContentMaster.get().setStandardId(standardMaster.getStandardId());
                uploadContentMaster.get().setSubjectId(subjectMaster.getSubjectId());
                uploadContentMaster.get().setChapterId(chapterMaster.getChapterId());
                uploadContentMaster.get().setTopicId(topicMaster.getTopicId());
                uploadContentMaster.get().setContentType(uploadContentRequest.getContentType());
                uploadContentMaster.get().setUrl(uploadContentRequest.getUrl());
                uploadContentMaster.get().setType(uploadContentRequest.getType());
                uploadContentMaster.get().setTitle(uploadContentRequest.getTitle());
                uploadContentMaster.get().setDescription(uploadContentRequest.getDescription());
                uploadContentMaster.get().setUploaderId(user.getId());
                uploadContentMaster.get().setDate(new Date());

                try {
                    this.uploadContentRepository.save(uploadContentMaster.get());
                    mainResponse.setMessage("Topic wise notes content updated successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }

            } else {
                BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster.get());
                uploadContentMaster.get().setEntranceExamId(entranceExamMaster.getEntranceExamId());
                uploadContentMaster.get().setStandardId(standardMaster.getStandardId());
                uploadContentMaster.get().setSubjectId(subjectMaster.getSubjectId());
                uploadContentMaster.get().setChapterId(chapterMaster.getChapterId());
                uploadContentMaster.get().setContentType(uploadContentRequest.getContentType());
                uploadContentMaster.get().setUrl(uploadContentRequest.getUrl());
                uploadContentMaster.get().setType(uploadContentRequest.getType());
                uploadContentMaster.get().setTitle(uploadContentRequest.getTitle());
                uploadContentMaster.get().setDescription(uploadContentRequest.getDescription());
                uploadContentMaster.get().setUploaderId(user.getId());
                uploadContentMaster.get().setDate(new Date());

                try {
                    this.uploadContentRepository.save(uploadContentMaster.get());
                    mainResponse.setMessage("Chapter wise notes content updated successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            }

        } else if (uploadContentRequest.getContentType().equals("summary")) {

            UploadContentMaster uploadContentMaster = this.uploadContentRepository.findById(uploadContentRequest.getUploadContentId()).get();

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            SubjectMaster subjectMaster = this.subjectRepository.findById(uploadContentRequest.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ChapterMaster chapterMaster = this.chapterRepository.findById(uploadContentRequest.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            Integer topicId = uploadContentRequest.getTopicId();

            if (topicId != null) {
                TopicMaster topicMaster = this.topicRepository.findById(topicId).orElse(null);

                BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
                uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
                uploadContentMaster.setStandardId(standardMaster.getStandardId());
                uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
                uploadContentMaster.setChapterId(chapterMaster.getChapterId());
                uploadContentMaster.setTopicId(topicMaster.getTopicId());
                uploadContentMaster.setContentType(uploadContentRequest.getContentType());
                uploadContentMaster.setUrl(uploadContentRequest.getUrl());
                uploadContentMaster.setType(uploadContentRequest.getType());
                uploadContentMaster.setTitle(uploadContentRequest.getTitle());
                uploadContentMaster.setDescription(uploadContentRequest.getDescription());
                uploadContentMaster.setUploaderId(user.getId());
                uploadContentMaster.setDate(new Date());

                try {
                    this.uploadContentRepository.save(uploadContentMaster);
                    mainResponse.setMessage("Topic summary content updated successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }

            } else {
                BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
                uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
                uploadContentMaster.setStandardId(standardMaster.getStandardId());
                uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
                uploadContentMaster.setChapterId(chapterMaster.getChapterId());
                uploadContentMaster.setContentType(uploadContentRequest.getContentType());
                uploadContentMaster.setUrl(uploadContentRequest.getUrl());
                uploadContentMaster.setType(uploadContentRequest.getType());
                uploadContentMaster.setTitle(uploadContentRequest.getTitle());
                uploadContentMaster.setDescription(uploadContentRequest.getDescription());
                uploadContentMaster.setUploaderId(user.getId());
                uploadContentMaster.setDate(new Date());

                try {
                    this.uploadContentRepository.save(uploadContentMaster);
                    mainResponse.setMessage("Chapter summary content updated successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            }

        } else if (uploadContentRequest.getContentType().equals("VideoContent")) {

            UploadContentMaster uploadContentMaster = this.uploadContentRepository.findById(uploadContentRequest.getUploadContentId()).get();

            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(uploadContentRequest.getEntranceExamId())
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found"));
            StandardMaster standardMaster = this.standardRepository.findById(uploadContentRequest.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found"));
            SubjectMaster subjectMaster = this.subjectRepository.findById(uploadContentRequest.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            ChapterMaster chapterMaster = this.chapterRepository.findById(uploadContentRequest.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            BeanUtils.copyProperties(uploadContentRequest, uploadContentMaster);
            uploadContentMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            uploadContentMaster.setStandardId(standardMaster.getStandardId());
            uploadContentMaster.setSubjectId(subjectMaster.getSubjectId());
            uploadContentMaster.setChapterId(chapterMaster.getChapterId());
            uploadContentMaster.setContentType(uploadContentRequest.getContentType());
            uploadContentMaster.setUrl(uploadContentRequest.getUrl());
            uploadContentMaster.setType(uploadContentRequest.getType());
            uploadContentMaster.setTitle(uploadContentRequest.getTitle());
            uploadContentMaster.setDescription(uploadContentRequest.getDescription());
            uploadContentMaster.setUploaderId(user.getId());
            uploadContentMaster.setDate(new Date());

            try {
                this.uploadContentRepository.save(uploadContentMaster);
                mainResponse.setMessage("Chapter video content updated successfully");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        }

        return mainResponse;
    }

    @Override
    public UploadContentMaster getById(Integer uploadContentId) {
        return this.uploadContentRepository.findById(uploadContentId).get();
    }

    @Override
    public List<UploadContentMasterResponses> getAll() {
        List<UploadContentMaster> uploadContentMasters = this.uploadContentRepository.findAll();
        List<UploadContentMasterResponses> uploadContentMasterResponses = new ArrayList<>();

        for (UploadContentMaster uploadContentMaster : uploadContentMasters) {
            if (uploadContentMaster == null) continue;

            UploadContentMasterResponses uploadContentMasterResponse = new UploadContentMasterResponses();

            EntranceExamMasterResponse entranceExamMasterResponse = null;
            if (uploadContentMaster.getEntranceExamId() != null) {
                entranceExamMasterResponse = this.entranceExamRepository
                        .getEntranceExamResponse(uploadContentMaster.getEntranceExamId())
                        .orElse(null);
            }
            uploadContentMasterResponse.setEntranceExamMaster(entranceExamMasterResponse);

            StandardMaster standardMaster = null;
            if (uploadContentMaster.getStandardId() != null) {
                standardMaster = this.standardRepository.findById(uploadContentMaster.getStandardId()).orElse(null);
            }
            uploadContentMasterResponse.setStandardMaster(standardMaster);

            SubjectMastersResponse subjectMastersResponse = null;
            if (uploadContentMaster.getSubjectId() != null) {
                subjectMastersResponse = this.subjectRepository
                        .getSubjectResponse(uploadContentMaster.getSubjectId())
                        .orElse(null);
            }
            uploadContentMasterResponse.setSubjectMaster(subjectMastersResponse);

            ChapterMasterResponse chapterMasterResponse = null;
            if (uploadContentMaster.getChapterId() != null) {
                chapterMasterResponse = this.chapterRepository
                        .getChapterMasterResponse(uploadContentMaster.getChapterId());
            }
            uploadContentMasterResponse.setChapterMaster(chapterMasterResponse);

            TopicMasterResponse topicMasterResponse = null;
            if (uploadContentMaster.getTopicId() != null) {
                topicMasterResponse = this.topicRepository
                        .getTopiMasterResponse(uploadContentMaster.getTopicId());
            }
            uploadContentMasterResponse.setTopicMaster(topicMasterResponse);

            uploadContentMasterResponse.setUploadContentId(uploadContentMaster.getUploadContentId());
            uploadContentMasterResponse.setContentType(uploadContentMaster.getContentType());
            uploadContentMasterResponse.setUrl(uploadContentMaster.getUrl());
            uploadContentMasterResponse.setType(uploadContentMaster.getType());
            uploadContentMasterResponse.setTitle(uploadContentMaster.getTitle());
            uploadContentMasterResponse.setDescription(uploadContentMaster.getDescription());
            uploadContentMasterResponse.setDate(uploadContentMaster.getDate());
            uploadContentMasterResponse.setStatus(uploadContentMaster.getStatus());
            uploadContentMasterResponse.setUploaderId(uploadContentMaster.getUploaderId());
            uploadContentMasterResponse.setExamYear(uploadContentMaster.getExamYear());

            uploadContentMasterResponses.add(uploadContentMasterResponse);
        }

        return uploadContentMasterResponses;
    }

    @Override
    public List<UploadContentMaster> getAllActive() {
        return this.uploadContentRepository.getAllActive();
    }

    @Override
    public UploadContentMappedResponse allMappedUploadContents(Integer entranceExamId, Integer standardId, Long id) {
        UploadContentMappedResponse uploadContentMappedResponse = new UploadContentMappedResponse();

        User student = this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student with ID " + id + " not found"));
        User teacher = student.getTeacher().stream()
                .findFirst()
                .map(t -> this.userRepository.findById(t.getId())
                        .orElseThrow(() -> new RuntimeException("Assigned teacher not found for student with ID " + id)))
                .orElseThrow(() -> new RuntimeException("No teacher assigned to student with ID " + id));

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(entranceExamId)
                .orElseThrow(() -> new RuntimeException("Entrance exam with ID " + entranceExamId + " not found"));
        StandardMaster standardMaster = this.standardRepository.findById(standardId)
                .orElseThrow(() -> new RuntimeException("Standard with ID " + standardId + " not found"));

        List<Integer> teacherAssignedStandards = this.userManagementMasterRepository
                .teacherAndEntranceExamIdWiseStandards(teacher.getId(), entranceExamMaster.getEntranceExamId());

        if (!teacherAssignedStandards.contains(standardMaster.getStandardId())) {
            throw new Apierrorr("Teacher with ID " + teacher.getId() +
                    " is not assigned to Standard ID " + standardId +
                    " for Entrance Exam ID " + entranceExamId, "400");
        }

        List<Integer> teacherAssignedSubjects = this.userManagementMasterRepository
                .teacherAndEntranceAndStandardIdWiseSubjects(teacher.getId(), entranceExamMaster.getEntranceExamId(), standardMaster.getStandardId());

        List<UploadContentSubjectMasterResponse> subjectResponses = new ArrayList<>();
        List<SubjectMaster> standardSubjects = this.subjectRepository.standardWiseSubjects(standardMaster.getStandardId());

        for (SubjectMaster subjectMaster : standardSubjects) {
            if (teacherAssignedSubjects.contains(subjectMaster.getSubjectId())) {
                UploadContentSubjectMasterResponse subjectResponse = new UploadContentSubjectMasterResponse();
                subjectResponse.setId(subjectMaster.getSubjectId());
                subjectResponse.setName(subjectMaster.getSubjectName());

                List<ChapterMaster> chapters = this.chapterRepository.subjectWiseChapter(subjectMaster.getSubjectId());
                List<UploadContentChapterMasterResponse> chapterResponses = new ArrayList<>();

                for (ChapterMaster chapterMaster : chapters) {
                    UploadContentChapterMasterResponse chapterResponse = new UploadContentChapterMasterResponse();
                    chapterResponse.setId(chapterMaster.getChapterId());
                    chapterResponse.setName(chapterMaster.getChapterName());

                    List<TopicResponse> topics = this.topicRepository.chapterWiseTopics(chapterMaster.getChapterId());
                    List<UploadContentTopicMasterResponse> topicResponses = new ArrayList<>();

                    for (TopicResponse topic : topics) {
                        UploadContentTopicMasterResponse topicResponse = new UploadContentTopicMasterResponse();
                        topicResponse.setId(topic.getTopicId());
                        topicResponse.setName(topic.getTopicName());

                        List<UploadContents> topicContents = this.uploadContentRepository.findByTopicId(topic.getTopicId());
                        topicResponse.setContent(topicContents);
                        topicResponses.add(topicResponse);
                    }

                    List<UploadContents> chapterContents = this.uploadContentRepository.findByChapterId(chapterMaster.getChapterId());
                    chapterResponse.setContent(chapterContents);
                    chapterResponse.setTopics(topicResponses);
                    chapterResponses.add(chapterResponse);
                }

                subjectResponse.setChapters(chapterResponses);
                subjectResponses.add(subjectResponse);
            }
        }

        uploadContentMappedResponse.setSubjects(subjectResponses);
        return uploadContentMappedResponse;
    }

    @Override
    public List<UploadContentMasterResponses> getAllById(Long uploaderId) {
        List<UploadContentMaster> uploadContentMasters = uploadContentRepository.getAllByUploaderId(uploaderId);
        List<UploadContentMasterResponses> uploadContentMasterResponses = new ArrayList<>();

        for (UploadContentMaster uploadContentMaster : uploadContentMasters) {
            if (uploadContentMaster == null) continue;

            UploadContentMasterResponses uploadContentMasterResponse = new UploadContentMasterResponses();

            EntranceExamMasterResponse entranceExamMasterResponse = null;
            if (uploadContentMaster.getEntranceExamId() != null) {
                entranceExamMasterResponse = this.entranceExamRepository
                        .getEntranceExamResponse(uploadContentMaster.getEntranceExamId())
                        .orElse(null);
            }
            uploadContentMasterResponse.setEntranceExamMaster(entranceExamMasterResponse);

            StandardMaster standardMaster = null;
            if (uploadContentMaster.getStandardId() != null) {
                standardMaster = this.standardRepository.findById(uploadContentMaster.getStandardId()).orElse(null);
            }
            uploadContentMasterResponse.setStandardMaster(standardMaster);

            SubjectMastersResponse subjectMastersResponse = null;
            if (uploadContentMaster.getSubjectId() != null) {
                subjectMastersResponse = this.subjectRepository
                        .getSubjectResponse(uploadContentMaster.getSubjectId())
                        .orElse(null);
            }
            uploadContentMasterResponse.setSubjectMaster(subjectMastersResponse);

            ChapterMasterResponse chapterMasterResponse = null;
            if (uploadContentMaster.getChapterId() != null) {
                chapterMasterResponse = this.chapterRepository
                        .getChapterMasterResponse(uploadContentMaster.getChapterId());
            }
            uploadContentMasterResponse.setChapterMaster(chapterMasterResponse);

            TopicMasterResponse topicMasterResponse = null;
            if (uploadContentMaster.getTopicId() != null) {
                topicMasterResponse = this.topicRepository
                        .getTopiMasterResponse(uploadContentMaster.getTopicId());
            }
            uploadContentMasterResponse.setTopicMaster(topicMasterResponse);

            uploadContentMasterResponse.setUploadContentId(uploadContentMaster.getUploadContentId());
            uploadContentMasterResponse.setContentType(uploadContentMaster.getContentType());
            uploadContentMasterResponse.setUrl(uploadContentMaster.getUrl());
            uploadContentMasterResponse.setType(uploadContentMaster.getType());
            uploadContentMasterResponse.setTitle(uploadContentMaster.getTitle());
            uploadContentMasterResponse.setDescription(uploadContentMaster.getDescription());
            uploadContentMasterResponse.setDate(uploadContentMaster.getDate());
            uploadContentMasterResponse.setStatus(uploadContentMaster.getStatus());
            uploadContentMasterResponse.setUploaderId(uploadContentMaster.getUploaderId());
            uploadContentMasterResponse.setExamYear(uploadContentMaster.getExamYear());

            uploadContentMasterResponses.add(uploadContentMasterResponse);
        }

        return uploadContentMasterResponses;
    }

    @Override
    public List<UploadContentQueRes> getAllBystudentIdAndType(Long studId) {
        User student = userRepository.findById(studId).orElseThrow(() -> new RuntimeException("student not found for this id"));
        Long teacherId = userRepository.findTeacherIdByStudentId(studId);
        if (teacherId == null) {
            throw new RuntimeException("teacher not found for this id");
        }
        List<UploadContentMaster> uploadContentMasters = uploadContentRepository.getAllByUploaderIdAndContentTypeIsQuestion(teacherId);
        return uploadContentMasters.stream()
                .map(content -> new UploadContentQueRes(
                        content.getExamYear(),
                        content.getTitle(),
                        content.getUrl()
                )).collect(Collectors.toList());
    }

    @Override
    public void deleteUploadContentById(Integer id) {
        UploadContentMaster contentMaster = uploadContentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UploadContent not found with ID: " + id));
        uploadContentRepository.delete(contentMaster);
    }
}
