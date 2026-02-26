package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import com.bezkoder.springjwt.config.OtpGenerator;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.*;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.services.UserService;
import com.bezkoder.springjwt.spec.HierarchyService;
import com.bezkoder.springjwt.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private static final int BATCH_SIZE = 100;
    private static final ExecutorService EMAIL_EXECUTOR = Executors.newFixedThreadPool(10);
    private static final Map<String, Role> roleCache = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HierarchyService hierarchyService;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private EntranceExamRepository entranceExamRepository;
    @Autowired
    private StandardRepository standardRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private PatternRepository patternRepository;
    @Autowired
    private UserManagementMasterRepository userManagementMasterRepository;
    @Autowired
    private StudentManagementRepository studentManagementRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TestSubmissionRepository submissionRepository;
    @Autowired
    private InstituteRepository instituteRepository;


    @Override
    public MainResponse updateStudent(UpdateStudentRequest updateStudentRequest) {
        logger.info("Updating student with ID: {}", updateStudentRequest.getId());
        logger.info("Teacher ID from request: {}", updateStudentRequest.getTeacherId());

        MainResponse mainResponse = new MainResponse();
        Optional<User> optionalUser = this.userRepository.findById(updateStudentRequest.getId());

        if (optionalUser.isPresent()) {
            User student = optionalUser.get();

            try {
                // 1. Update basic properties
                BeanUtils.copyProperties(updateStudentRequest, student);
                student.setAddress(updateStudentRequest.getAddress());

                // 2. SIMPLE TEACHER ASSIGNMENT LOGIC
                if (updateStudentRequest.getTeacherId() != null) {
                    logger.info("Assigning teacher ID: {}", updateStudentRequest.getTeacherId());

                    // Find the teacher
                    Optional<User> optionalTeacher = userRepository.findById(
                            updateStudentRequest.getTeacherId().longValue()
                    );

                    if (optionalTeacher.isPresent()) {
                        User teacher = optionalTeacher.get();

                        // Clear existing teachers (if you want only one teacher)
                        student.getTeacher().clear();

                        // Add the new teacher
                        student.getTeacher().add(teacher);
                        logger.info("Teacher assigned successfully");

                    } else {
                        logger.warn("Teacher not found with ID: {}", updateStudentRequest.getTeacherId());
                        // Option: clear teachers if invalid teacher ID
                        // student.getTeacher().clear();
                    }
                } else {
                    // If teacherId is null, clear teachers
                    logger.info("No teacher ID provided, clearing teachers");
                    student.getTeacher().clear();
                }

                // 3. Save the student
                User savedUser = this.userRepository.save(student);

                // 4. Verify
                logger.info("Saved successfully. Teacher count: {}", savedUser.getTeacher().size());

                mainResponse.setMessage("Student updated successfully.");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);

            } catch (Exception e) {
                logger.error("Error: {}", e.getMessage(), e);
                mainResponse.setMessage("Error: " + e.getMessage());
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        } else {
            mainResponse.setMessage("Student Not Found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public UserDetails1 getById(Long id) {
        logger.info("Fetching user details for ID: {}", id);
        UserDetails1 userDetails = new UserDetails1();
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            BeanUtils.copyProperties(user.get(), userDetails);
            logger.info("User found for ID: {}", id);
        } else {
            logger.warn("User not found for ID: {}", id);
            return null;
        }
        return userDetails;
    }
    @Override
    @Transactional
    public void assignTeacherToStudent(Long studentId, Long teacherId) {
        logger.info("Assigning teacher {} to student {}", teacherId, studentId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Verify teacher has ROLE_TEACHER
        boolean isTeacher = teacher.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_TEACHER);

        if (!isTeacher) {
            throw new RuntimeException("User with ID " + teacherId + " is not a teacher");
        }

        // Add teacher to student's teacher set
        student.getTeacher().add(teacher);
        userRepository.save(student);

        logger.info("Successfully assigned teacher {} to student {}",
                teacherId, studentId);
    }
    @Override
    public List<UserDetails> getAll() {
        logger.info("Fetching all users");
        List<UserDetails> userDetailsList = this.userRepository.getAll();
        Collections.reverse(userDetailsList);
        logger.info("Total users found: {}", userDetailsList.size());
        return userDetailsList;
    }

    @Override
    public void deleteAll() {
        // Implementation if needed
    }

    @Override
    public List<UserDetails> getRoleWiseList(String role) {
        logger.info("Fetching role wise list for role: {}", role);
        ERole roleEnum;
        try {
            roleEnum = ERole.valueOf(role);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role: {}", role);
            throw new RuntimeException("Invalid role: " + role);
        }
        return this.userRepository.getRoleWiseList(roleEnum);
    }

    @Override
    public List<UserDetails> getAllActiveRoleWiseList(String role) {
        logger.info("Fetching all active role wise list for role: {}", role);
        ERole roleEnum = ERole.valueOf(role);
        List<UserDetails> roleWiseList = this.userRepository.getAllActiveRoleWiseList(roleEnum);
        Collections.reverse(roleWiseList);
        logger.info("Active users found for role {}: {}", role, roleWiseList.size());
        return roleWiseList;
    }

    @Override
    public MainResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        logger.info("Processing forgot password for email: {}", forgotPasswordRequest.getEmail());
        MainResponse mainResponse = new MainResponse();
        Integer otp;
        Optional<User> user = this.userRepository.findByEmail(forgotPasswordRequest.getEmail());
        if (user.isPresent()) {
            OtpGenerator otpGenerator = new OtpGenerator();
            otp = otpGenerator.generateOtp();
            try {
                SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                simpleMailMessage.setTo(user.get().getEmail());
                simpleMailMessage.setFrom("zplushrms@gmail.com");
                simpleMailMessage.setSubject("OTP");
                simpleMailMessage.setSentDate(new Date());
                simpleMailMessage.setText("Dear " + user.get().getFirstName() + ",\n"
                        + "Your One Time Password (OTP) is :\n"
                        + "OTP: " + otp + "\n"
                        + "Please use this OTP to reset your password. If you did not request this OTP, please ignore this email.\n"
                        + "Thank you.");
                mailSender.send(simpleMailMessage);
                user.get().setOtp(otp);
                this.userRepository.save(user.get());
                logger.info("OTP sent to email: {}", forgotPasswordRequest.getEmail());
                mainResponse.setMessage("OTP has been sent on your email");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
                mainResponse.setId(user.get().getId());
            } catch (Exception e) {
                logger.error("Error sending OTP to email {}: {}", forgotPasswordRequest.getEmail(), e.getMessage(), e);
                mainResponse.setMessage("Something went wrong");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        } else {
            logger.warn("Invalid email provided: {}", forgotPasswordRequest.getEmail());
            mainResponse.setMessage("Invalid email. Please provide the valid email");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public MainResponse newPassword(NewPasswordRequest newPasswordRequest) {
        logger.info("Setting new password for user ID: {}", newPasswordRequest.getId());
        MainResponse mainResponse = new MainResponse();
        Optional<User> user = this.userRepository.findById(newPasswordRequest.getId());
        if (user.isPresent()) {
            if (newPasswordRequest.getNewPassword().equals(newPasswordRequest.getConfirmPassword())) {
                try {
                    user.get().setConfirmPassword(newPasswordRequest.getConfirmPassword());
                    user.get().setPassword(encoder.encode(newPasswordRequest.getConfirmPassword()));
                    this.userRepository.save(user.get());
                    mainResponse.setMessage("Password change successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                    logger.info("Password changed successfully for user ID: {}", newPasswordRequest.getId());
                } catch (Exception e) {
                    logger.error("Error changing password for user {}: {}", newPasswordRequest.getId(), e.getMessage(), e);
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            } else {
                logger.warn("Password mismatch for user ID: {}", newPasswordRequest.getId());
                mainResponse.setMessage("Password doesn't matched");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        } else {
            logger.warn("User not found for password reset: {}", newPasswordRequest.getId());
            mainResponse.setMessage("User not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public MainResponse changePasswordRequest(ChangePasswordRequest changePasswordRequest) {
        logger.info("Changing password for user ID: {}", changePasswordRequest.getId());
        MainResponse mainResponse = new MainResponse();
        Optional<User> user = this.userRepository.findById(changePasswordRequest.getId());
        if (user.isPresent()) {
            if (encoder.matches(changePasswordRequest.getOldPassword(), user.get().getPassword())) {
                try {
                    user.get().setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
                    this.userRepository.save(user.get());
                    mainResponse.setMessage("Password changed successfully");
                    mainResponse.setResponseCode(HttpStatus.OK.value());
                    mainResponse.setFlag(true);
                    logger.info("Password changed successfully for user ID: {}", changePasswordRequest.getId());
                } catch (Exception e) {
                    logger.error("Error changing password for user {}: {}", changePasswordRequest.getId(), e.getMessage(), e);
                    mainResponse.setMessage("Something went wrong");
                    mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    mainResponse.setFlag(false);
                }
            } else {
                logger.warn("Old password doesn't match for user ID: {}", changePasswordRequest.getId());
                mainResponse.setMessage("Password doesn't matched");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        } else {
            logger.warn("User not found for password change: {}", changePasswordRequest.getId());
            mainResponse.setMessage("User not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public User userAllDetails(Long id) {
        logger.info("Fetching all details for user ID: {}", id);
        Optional<User> user = this.userRepository.findById(id);
        return user.orElse(null);
    }

    @Override
    public List<TeacherResponse> getSubjectWiseTeachers(Integer subjectId) {
        logger.info("Fetching subject wise teachers for subject ID: {}", subjectId);
        List<TeacherResponse> teachers = this.userRepository.getSubjectWiseTeachers(subjectId);
        Collections.reverse(teachers);
        logger.info("Found {} teachers for subject ID: {}", teachers.size(), subjectId);
        return teachers;
    }

    @Override
    public Integer studentCount() {
        Integer count = this.userRepository.studentCount();
        logger.info("Student count: {}", count);
        return count;
    }

    @Override
    public Integer teacherCount() {
        Integer count = this.userRepository.teacherCount();
        logger.info("Teacher count: {}", count);
        return count;
    }

    @Override
    public MainResponse updateTeacher(UpdateTeacherRequest updateTeacherRequest) {
        logger.info("Updating teacher with ID: {}", updateTeacherRequest.getId());
        MainResponse mainResponse = new MainResponse();
        Optional<User> user = Optional.ofNullable(this.userRepository.findById(updateTeacherRequest.getId()).orElseThrow(() -> new RuntimeException("User not found exception")));
        Optional<User> creator = Optional.ofNullable(this.userRepository.findById(updateTeacherRequest.getCreatorId()).orElseThrow(() -> new RuntimeException("Creator not found")));

        BeanUtils.copyProperties(updateTeacherRequest, user.get());
        user.get().setAddress(updateTeacherRequest.getAddress());
        user.get().setCreatorId(creator.get().getId());
        user.get().setStatus(updateTeacherRequest.getStatus());
        user.get().setColorTheme(updateTeacherRequest.getColorTheme());
        user.get().setStudentKeys(updateTeacherRequest.getStudentKeys());
        user.get().setExpiryDate(updateTeacherRequest.getExpiryDate());

        if (updateTeacherRequest.getEditAccess() != null) {
            user.get().setEditAccess(updateTeacherRequest.getEditAccess());
        }
        if (updateTeacherRequest.getOrmSheetAccess() != null) {
            user.get().setOrmSheetAccess(updateTeacherRequest.getOrmSheetAccess());
        }
        if (updateTeacherRequest.getPrintAccess() != null) {
            user.get().setPrintAccess(updateTeacherRequest.getPrintAccess());
        }
        if (updateTeacherRequest.getEditAccess() != null) {
            user.get().setEditAccess(updateTeacherRequest.getEditAccess());
        }
        user.get().setDate(new Date());
        user.get().setFirstName(updateTeacherRequest.getFirstName());
        user.get().setLastName(updateTeacherRequest.getLastName());

        try {
            if (updateTeacherRequest.getPatternIds() != null && !updateTeacherRequest.getPatternIds().isEmpty()) {
                Set<PatternMaster> patternMasters = updateTeacherRequest.getPatternIds().stream()
                        .map(patternId -> patternRepository.findById(patternId)
                                .orElseThrow(() -> new RuntimeException("PatternMaster not found with id: " + patternId)))
                        .collect(Collectors.toSet());
                user.get().setAssignedPatterns(patternMasters);
            } else {
                user.get().setAssignedPatterns(new HashSet<>());
            }

            this.userRepository.save(user.get());
            mainResponse.setMessage("Teacher updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
            logger.info("Teacher updated successfully: {}", updateTeacherRequest.getId());
        } catch (Exception e) {
            logger.error("Error updating teacher {}: {}", updateTeacherRequest.getId(), e.getMessage(), e);
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public StudentDetailsResponse studentDetailsById(Long id) {
        logger.info("Fetching student details for ID: {}", id);

        try {
            // 1. Validate input
            if (id == null || id <= 0) {
                logger.error("Invalid student ID provided: {}", id);
                throw new IllegalArgumentException("Valid student ID is required");
            }

            // 2. Find student WITH teacher relationship
            Optional<User> userOpt = this.userRepository.findByIdWithTeacher(id);
            if (!userOpt.isPresent()) {
                logger.error("Student not found with ID: {}", id);
                throw new NoSuchElementException("Student not found with ID: " + id);
            }

            User user = userOpt.get();

            // 3. Verify it's actually a student
            boolean isStudent = user.getRoles() != null && user.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_STUDENT);

            if (!isStudent) {
                logger.error("User with ID {} is not a student", id);
                throw new IllegalArgumentException("User with ID " + id + " is not a student");
            }

            // 4. Create response
            StudentDetailsResponse studentDetailsResponse = new StudentDetailsResponse();
            BeanUtils.copyProperties(user, studentDetailsResponse);

            // 5. Handle teacher mapping
            if (user.getTeacher() != null && !user.getTeacher().isEmpty()) {
                // Get the first teacher from the set
                User teacherUser = user.getTeacher().iterator().next();

                TeacherResponse teacherResponse = new TeacherResponse();
                teacherResponse.setId(teacherUser.getId());
                teacherResponse.setFirstName(teacherUser.getFirstName() != null ? teacherUser.getFirstName() : "");
                teacherResponse.setLastName(teacherUser.getLastName() != null ? teacherUser.getLastName() : "");
                teacherResponse.setEmail(teacherUser.getEmail() != null ? teacherUser.getEmail() : "");
                teacherResponse.setMobile(teacherUser.getMobile() != null ? teacherUser.getMobile() : "");

                studentDetailsResponse.setTeacher(teacherResponse);

                logger.info("Teacher mapped: {} {} for student {}",
                        teacherUser.getFirstName(), teacherUser.getLastName(), id);
            } else {
                logger.info("No teacher assigned to student {}", id);
                studentDetailsResponse.setTeacher(null);
            }

            // 6. Set collections with null checks
            studentDetailsResponse.setSubjectMasters(user.getSubjectMasters() != null ? user.getSubjectMasters() : new HashSet<>());
            studentDetailsResponse.setEntranceExamMasters(user.getEntranceExamMasters() != null ? user.getEntranceExamMasters() : new HashSet<>());
            studentDetailsResponse.setStandardMasters(user.getStandardMasters() != null ? user.getStandardMasters() : new HashSet<>());

            // 7. Set additional properties
            studentDetailsResponse.setColorTheme(user.getColorTheme() != null ? user.getColorTheme() : "");
            studentDetailsResponse.setAddress(user.getAddress() != null ? user.getAddress() : "");

            logger.info("Successfully fetched student details for ID: {}", id);
            return studentDetailsResponse;

        } catch (NoSuchElementException e) {
            logger.error("Student not found with ID {}: {}", id, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for student ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error fetching student details for ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch student details: " + e.getMessage(), e);
        }
    }
    @Override
    public List<ProfilePatternResponse> allTeachers() {
        logger.info("Fetching all teachers");
        List<ProfileResponse> allTeachers = this.userRepository.allTeachers();
        List<ProfilePatternResponse> responses = new ArrayList<>();

        for (ProfileResponse teacher : allTeachers) {
            teacher.setQuestionCount(this.questionRepository.teacherWiseQuestionCount(teacher.getId()));
            teacher.setPendingQuestionCount(this.questionRepository.teacherWisePendingQuestionCount(teacher.getId()));
            teacher.setAcceptedQuestionCount(this.questionRepository.teacherWiseAcceptedQuestionCount(teacher.getId()));
            teacher.setRejectedQuestionCount(this.questionRepository.teacherWiseRejectedQuestionCount(teacher.getId()));
            teacher.setPrintAccess(teacher.getPrintAccess());
            teacher.setOrmSheetAccess(teacher.getOrmSheetAccess());
            teacher.setEditAccess(teacher.getEditAccess());

            User userEntity = this.userRepository.findById(teacher.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Set<PatternMaster> assignedPatterns = userEntity.getAssignedPatterns();
            Set<PatternMasterResponse> patternMasterResponses = new HashSet<>();
            for (PatternMaster pattern : assignedPatterns) {
                PatternMasterResponse pmr = new PatternMasterResponse();
                BeanUtils.copyProperties(pattern, pmr);
                patternMasterResponses.add(pmr);
            }

            ProfilePatternResponse profilePatternResponse = new ProfilePatternResponse();
            BeanUtils.copyProperties(teacher, profilePatternResponse);
            profilePatternResponse.setAssignedPatterns(patternMasterResponses);
            responses.add(profilePatternResponse);
        }

        logger.info("Total teachers found: {}", responses.size());
        return responses;
    }

    @Override
    public List<UserDetails> allStudents() {
        logger.info("Fetching all students");
        List<UserDetails> allStudents = userRepository.allStudents();
        if (allStudents == null) {
            logger.warn("No students found");
            return Collections.emptyList();
        }
        List<UserDetails> filtered = allStudents.stream()
                .filter(Objects::nonNull)
                .filter(u -> u.getStatus() != null && !"deleted".equalsIgnoreCase(u.getStatus()))
                .collect(Collectors.toList());
        logger.info("Total active students found: {}", filtered.size());
        return filtered;
    }

    @Override
    public CountOfStudentTeacherQuestionResponse countOfStudentTeacherQuestion() {
        logger.info("Fetching counts of students, teachers, and questions");
        Integer studentCount = this.userRepository.studentCount();
        Integer teacherCount = this.userRepository.teacherCount();
        Integer questionCount = this.questionRepository.allQuestionsCount();

        CountOfStudentTeacherQuestionResponse response = new CountOfStudentTeacherQuestionResponse();
        response.setStudentCount(studentCount);
        response.setTeacherCount(teacherCount);
        response.setQuestionCount(questionCount);

        logger.info("Counts - Students: {}, Teachers: {}, Questions: {}", studentCount, teacherCount, questionCount);
        return response;
    }

    @Override
    public MainResponse teacherAssignments(TeacherRequestAssignment teacherRequestAssignment) {
        logger.info("Processing teacher assignments");
        return new MainResponse();
    }

    @Override
    public TeacherAssessmentDetails teacherAssessmentDetails(Long id) {
        logger.info("Fetching teacher assessment details for ID: {}", id);
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        TeacherAssessmentDetails teacherAssessmentDetails = new TeacherAssessmentDetails();
        teacherAssessmentDetails.setId(user.getId());
        teacherAssessmentDetails.setFirstName(user.getFirstName());
        teacherAssessmentDetails.setLastName(user.getLastName());
        teacherAssessmentDetails.setMobile(user.getMobile());
        teacherAssessmentDetails.setStatus(user.getStatus());
        teacherAssessmentDetails.setProfilePicture(user.getProfilePicture());
        teacherAssessmentDetails.setDate(user.getDate());
        teacherAssessmentDetails.setEmail(user.getEmail());
        teacherAssessmentDetails.setClassName(user.getClassName());
        teacherAssessmentDetails.setRoles(user.getRoles());
        teacherAssessmentDetails.setCreatorId(user.getCreatorId());
        teacherAssessmentDetails.setLogoImage(user.getLogoImage());
        teacherAssessmentDetails.setWatermarkImage(user.getWatermarkImage());
        teacherAssessmentDetails.setStudentKeys(user.getStudentKeys());

        List<Integer> entranceExamList = userRepository.getEntraceExamByUserId(id);
        List<TeacherAssessmentEntranceExams> teacherAssessmentEntranceExamsList = new ArrayList<>();

        for (Integer entranceExamId : entranceExamList) {
            EntranceExamMaster entranceExamMaster = entranceExamRepository.findById(entranceExamId)
                    .orElseThrow(() -> new RuntimeException("Entrance exam not found with ID: " + entranceExamId));

            TeacherAssessmentEntranceExams teacherAssessmentEntranceExams = new TeacherAssessmentEntranceExams();
            teacherAssessmentEntranceExams.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            teacherAssessmentEntranceExams.setEntranceExamName(entranceExamMaster.getEntranceExamName());
            teacherAssessmentEntranceExams.setEntranceStatus(entranceExamMaster.getStatus());

            List<UserManagementMaster> userManagementMasters = this.userManagementMasterRepository.getAllByTeacherId(id);
            List<TeacherAssessmentStandards> teacherAssessmentStandardsList = new ArrayList<>();

            for (UserManagementMaster userManagementMaster : userManagementMasters) {
                if (userManagementMaster.getEntranceExamId().equals(entranceExamId)) {
                    StandardMaster standardMaster = standardRepository.findById(userManagementMaster.getStandardId())
                            .orElseThrow(() -> new RuntimeException("Standard not found with ID: " + userManagementMaster.getStandardId()));

                    SubjectMaster subjectMaster = subjectRepository.findById(userManagementMaster.getSubjectId())
                            .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + userManagementMaster.getSubjectId()));

                    TeacherAssessmentSubjects teacherAssessmentSubjects = new TeacherAssessmentSubjects();
                    teacherAssessmentSubjects.setUserManagementId(userManagementMaster.getUserManagementId());
                    teacherAssessmentSubjects.setSubjectId(subjectMaster.getSubjectId());
                    teacherAssessmentSubjects.setSubjectName(subjectMaster.getSubjectName());
                    teacherAssessmentSubjects.setSubStatus(subjectMaster.getStatus());

                    TeacherAssessmentStandards teacherAssessmentStandards = teacherAssessmentStandardsList.stream()
                            .filter(tas -> tas.getStandardId().equals(standardMaster.getStandardId()))
                            .findFirst()
                            .orElse(null);

                    if (teacherAssessmentStandards == null) {
                        teacherAssessmentStandards = new TeacherAssessmentStandards();
                        teacherAssessmentStandards.setStandardId(standardMaster.getStandardId());
                        teacherAssessmentStandards.setStandardName(standardMaster.getStandardName());
                        teacherAssessmentStandards.setStandardStatus(standardMaster.getStatus());
                        teacherAssessmentStandards.setTeacherAssessmentSubjects(new ArrayList<>());
                        teacherAssessmentStandardsList.add(teacherAssessmentStandards);
                    }

                    teacherAssessmentStandards.getTeacherAssessmentSubjects().add(teacherAssessmentSubjects);
                }
            }

            teacherAssessmentEntranceExams.setTeacherAssessmentStandards(teacherAssessmentStandardsList);
            teacherAssessmentEntranceExamsList.add(teacherAssessmentEntranceExams);
        }

        teacherAssessmentDetails.setTeacherAssessmentEntranceExams(teacherAssessmentEntranceExamsList);
        logger.info("Teacher assessment details fetched for ID: {}", id);
        return teacherAssessmentDetails;
    }

    @Override
    public TeacherMappedData teacherMappedData(Long id) {
        logger.info("Fetching teacher mapped data for ID: {}", id);
        User user = this.userRepository.findById(id).orElse(null);
        if (user == null) {
            logger.warn("Teacher not found for ID: {}", id);
            return null;
        }

        TeacherMappedData teacherMappedData = new TeacherMappedData();
        teacherMappedData.setId(user.getId());

        List<UserManagementMaster> userManagementMaster = this.userManagementMasterRepository.getAllByTeacherId(id);
        List<TeacherAssessmentEntranceExams> teacherAssessmentEntranceExams = new ArrayList<>();

        for (UserManagementMaster managementMaster : userManagementMaster) {
            Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(managementMaster.getEntranceExamId());
            if (entranceExamMaster.isPresent()) {
                TeacherAssessmentEntranceExams teacherAssessmentEntranceExams1 = new TeacherAssessmentEntranceExams();
                teacherAssessmentEntranceExams1.setEntranceExamId(entranceExamMaster.get().getEntranceExamId());
                teacherAssessmentEntranceExams1.setEntranceExamName(entranceExamMaster.get().getEntranceExamName());
                teacherAssessmentEntranceExams1.setEntranceStatus(entranceExamMaster.get().getStatus());

                List<TeacherAssessmentStandards> teacherAssessmentStandards = new ArrayList<>();

                Optional<StandardMaster> standardMaster = this.standardRepository.findById(managementMaster.getStandardId());
                if (standardMaster.isPresent()) {
                    TeacherAssessmentStandards teacherAssessmentStandards1 = new TeacherAssessmentStandards();
                    teacherAssessmentStandards1.setStandardName(standardMaster.get().getStandardName());
                    teacherAssessmentStandards1.setStandardId(standardMaster.get().getStandardId());
                    teacherAssessmentStandards1.setStandardStatus(standardMaster.get().getStatus());

                    List<TeacherAssessmentSubjects> teacherAssessmentSubjects = new ArrayList<>();

                    Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(managementMaster.getSubjectId());
                    if (subjectMaster.isPresent()) {
                        TeacherAssessmentSubjects teacherAssessmentSubjects1 = new TeacherAssessmentSubjects();
                        teacherAssessmentSubjects1.setSubjectId(subjectMaster.get().getSubjectId());
                        teacherAssessmentSubjects1.setSubjectName(subjectMaster.get().getSubjectName());
                        teacherAssessmentSubjects1.setSubStatus(subjectMaster.get().getStatus());
                        teacherAssessmentSubjects1.setUserManagementId(managementMaster.getUserManagementId());
                        teacherAssessmentSubjects.add(teacherAssessmentSubjects1);
                    }

                    teacherAssessmentStandards1.setTeacherAssessmentSubjects(teacherAssessmentSubjects);
                    teacherAssessmentStandards.add(teacherAssessmentStandards1);
                }

                teacherAssessmentEntranceExams1.setTeacherAssessmentStandards(teacherAssessmentStandards);
                teacherAssessmentEntranceExams.add(teacherAssessmentEntranceExams1);
            }
        }

        teacherMappedData.setTeacherAssessmentEntranceExams(teacherAssessmentEntranceExams);
        logger.info("Teacher mapped data fetched for ID: {}", id);
        return teacherMappedData;
    }

    @Override
    public VerificationOTPResponse VerificationOtp(VerificationOTPRequest verificationOtpReq) {
        logger.info("Verifying OTP for user ID: {}", verificationOtpReq.getId());
        VerificationOTPResponse verificationOtpResponse = new VerificationOTPResponse();
        Optional<User> user = userRepository.findById(verificationOtpReq.getId());
        try {
            if (user.isPresent() && userRepository.existsById(user.get().getId())) {
                if (verificationOtpReq.getOtp().equals(user.get().getOtp())) {
                    logger.info("OTP verified successfully for user ID: {}", verificationOtpReq.getId());
                    verificationOtpResponse.setMessage("OTP Verified");
                    verificationOtpResponse.setId(verificationOtpReq.getId());
                    verificationOtpResponse.setFlag(true);
                    return verificationOtpResponse;
                } else {
                    logger.warn("Invalid OTP for user ID: {}", verificationOtpReq.getId());
                    verificationOtpResponse.setMessage("otp is not valid.Please enter valid otp");
                    verificationOtpResponse.setFlag(false);
                    return verificationOtpResponse;
                }
            } else {
                logger.warn("User not found for OTP verification: {}", verificationOtpReq.getId());
                verificationOtpResponse.setMessage("Please enter valid id");
                verificationOtpResponse.setFlag(false);
                return verificationOtpResponse;
            }
        } catch (Exception e) {
            logger.error("Error verifying OTP for user {}: {}", verificationOtpReq.getId(), e.getMessage(), e);
            verificationOtpResponse.setMessage("Please enter valid id");
            verificationOtpResponse.setFlag(false);
            return verificationOtpResponse;
        }
    }

    @Override
    @Transactional
    public MainResponse deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        Optional<User> opt = userRepository.findById(id);
        if (!opt.isPresent()) {
            logger.warn("User not found for deletion: {}", id);
            return new MainResponse("User not found", 404, false);
        }

        User user = opt.get();

        try {
            // Check if user has SUPER_ADMIN role
            boolean isSuperAdmin = user.getRoles() != null && user.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_SUPER_ADMIN);

            if (isSuperAdmin) {
                logger.warn("Attempt to delete SUPER_ADMIN with ID: {}", id);
                return new MainResponse("SUPER_ADMIN cannot be deleted", 400, false);
            }

            userRepository.deleteUserRoles(id);
            userRepository.deleteUserSubjects(id);
            userRepository.deleteUserStandards(id);
            userRepository.deleteUserEntranceExams(id);
            userRepository.deleteTeacherStudentMapping(id);
            userRepository.deleteTeacherPatterns(id);

            if (user.getRoles() != null && user.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_INSTITUTE) &&
                    user.getInstituteName() != null &&
                    !user.getInstituteName().isEmpty()) {

                InstituteMaster inst = instituteRepository.findByInstituteNameIgnoreCase(user.getInstituteName());
                if (inst != null) {
                    instituteRepository.delete(inst);
                }
            }

            user.setStatus("Deleted");
            userRepository.save(user);

            logger.info("User deleted successfully: {}", id);
            return new MainResponse("User deleted successfully", 200, true);

        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage(), e);
            return new MainResponse("Something went wrong: " + e.getMessage(), 500, false);
        }
    }

    @Override
    public MainResponse updateUserPassword(NewPasswordRequest newPasswordRequest) {
        logger.info("Updating user password for ID: {}", newPasswordRequest.getId());
        MainResponse mainResponse = new MainResponse();
        Optional<User> userOpt = this.userRepository.findById(newPasswordRequest.getId());
        if (!userOpt.isPresent()) {
            logger.warn("User not found for password update: {}", newPasswordRequest.getId());
            mainResponse.setMessage("User not found");
            mainResponse.setResponseCode(HttpStatus.NOT_FOUND.value());
            mainResponse.setFlag(false);
            return mainResponse;
        }

        User user = userOpt.get();
        String password = newPasswordRequest.getConfirmPassword();

        try {
            user.setConfirmPassword(password);
            user.setPassword(encoder.encode(password));
            this.userRepository.save(user);

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(user.getEmail());
            simpleMailMessage.setFrom("zplushrms@gmail.com");
            simpleMailMessage.setSubject("Password Update Confirmation");
            simpleMailMessage.setSentDate(new Date());

            String emailContent = "Dear " + user.getFirstName() + ",\n\n"
                    + "We are writing to confirm that your password has been successfully updated.\n\n"
                    + "Your updated password is: " + password + "\n\n"
                    + "If you have not requested this password change, please contact our support team immediately to ensure the security of your account.\n\n"
                    + "Thank you for choosing Elite Codo.\n\n";

            simpleMailMessage.setText(emailContent);
            mailSender.send(simpleMailMessage);

            mainResponse.setMessage("User password updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
            logger.info("User password updated successfully: {}", newPasswordRequest.getId());
        } catch (Exception e) {
            logger.error("Error updating user password for {}: {}", newPasswordRequest.getId(), e.getMessage(), e);
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    @Transactional
    public MainResponse addStudentAssignments(StudentAssignmentsRequest studentAssignmentsRequest) {
        logger.info("Adding student assignments for teacher: {}, student: {}",
                studentAssignmentsRequest.getTeacherId(), studentAssignmentsRequest.getStudentId());
        MainResponse mainResponse = new MainResponse();

        Optional<User> teacherOpt = this.userRepository.findById(studentAssignmentsRequest.getTeacherId());
        Optional<User> studentOpt = this.userRepository.findById(studentAssignmentsRequest.getStudentId());
        if (!teacherOpt.isPresent() || !studentOpt.isPresent()) {
            logger.warn("Teacher or student not found. Teacher: {}, Student: {}",
                    studentAssignmentsRequest.getTeacherId(), studentAssignmentsRequest.getStudentId());
            mainResponse.setMessage("Teacher or student not found");
            mainResponse.setResponseCode(HttpStatus.NOT_FOUND.value());
            mainResponse.setFlag(false);
            return mainResponse;
        }

        User teacher = teacherOpt.get();
        User student = studentOpt.get();

        Set<StandardMaster> standardMasters = new HashSet<>();
        Set<SubjectMaster> subjectMasters = new HashSet<>();
        Set<EntranceExamMaster> entranceExamMasters = new HashSet<>();

        for (StudentEntranceExamRequest studentEntranceExamRequest : studentAssignmentsRequest.getEntranceExamRequests()) {
            Integer entranceExamId = studentEntranceExamRequest.getEntranceExamId();
            this.entranceExamRepository.findById(entranceExamId).ifPresent(entranceExamMasters::add);

            for (TeacherStandardRequest teacherStandardRequest : studentEntranceExamRequest.getStandards()) {
                Integer standardId = teacherStandardRequest.getStandardId();
                this.standardRepository.findById(standardId).ifPresent(standardMasters::add);

                for (Integer subjectId : teacherStandardRequest.getSubjectIds()) {
                    this.subjectRepository.findById(subjectId).ifPresent(subjectMasters::add);

                    StudentManagementMaster studentManagementMaster = new StudentManagementMaster();
                    studentManagementMaster.setStudentId(student.getId());
                    studentManagementMaster.setTeacherId(teacher.getId());
                    studentManagementMaster.setEntranceExamId(entranceExamId);
                    studentManagementMaster.setStandardId(standardId);
                    studentManagementMaster.setSubjectId(subjectId);
                    studentManagementMaster.setCreatedBy(teacher.getId());
                    studentManagementMaster.setStatus("ACTIVE");

                    this.studentManagementRepository.save(studentManagementMaster);
                }
            }
        }

        student.setStandardMasters(standardMasters);
        student.setSubjectMasters(subjectMasters);
        student.setEntranceExamMasters(entranceExamMasters);
        student.setTeacher(new HashSet<>(Collections.singletonList(teacher)));
        this.userRepository.save(student);

        mainResponse.setMessage("Student assignments successfully added.");
        mainResponse.setResponseCode(HttpStatus.OK.value());
        mainResponse.setFlag(true);
        logger.info("Student assignments added successfully for student: {}", student.getId());
        return mainResponse;
    }

    @Override
    public MainResponse addTeacherAssignments(TeacherRequestAssignment teacherRequestAssignment) {
        logger.info("Adding teacher assignments for teacher ID: {}", teacherRequestAssignment.getTeacherId());
        MainResponse mainResponse = new MainResponse();

        User teacher = this.userRepository.findById(teacherRequestAssignment.getTeacherId()).orElseThrow(() -> new RuntimeException("Teacher not found"));
        Set<StandardMaster> standardMastersList = new HashSet<>();
        Set<SubjectMaster> subjectMastersList = new HashSet<>();
        Set<EntranceExamMaster> entranceExamMastersList = new HashSet<>();

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(teacherRequestAssignment.getEntranceExamId()).orElseThrow(() -> new RuntimeException("Entrance exam not found"));
        entranceExamMastersList.add(entranceExamMaster);

        boolean isDuplicateFound = false;

        for (TeacherStandardRequest standard : teacherRequestAssignment.getStandards()) {
            StandardMaster standardMaster = this.standardRepository.findById(standard.getStandardId()).orElseThrow(() -> new RuntimeException("Standard not found"));
            standardMastersList.add(standardMaster);

            for (Integer subjectId : standard.getSubjectIds()) {
                SubjectMaster subjectMaster = this.subjectRepository.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject not found"));
                subjectMastersList.add(subjectMaster);

                UserManagementMaster userManagementMaster = new UserManagementMaster();
                userManagementMaster.setTeacherId(teacher.getId());
                userManagementMaster.setStatus("Active");
                userManagementMaster.setSubjectId(subjectId);
                userManagementMaster.setStandardId(standard.getStandardId());
                userManagementMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());

                if (userManagementMasterRepository.existsByTeacherIdAndEntranceExamIdAndStandardIdAndSubjectId(
                        teacher.getId(),
                        entranceExamMaster.getEntranceExamId(),
                        standard.getStandardId(),
                        subjectId)) {
                    logger.info("Assignment already exists for Teacher ID: {}, Standard ID: {}, Subject ID: {}",
                            teacher.getId(), standard.getStandardId(), subjectId);
                    isDuplicateFound = true;
                } else {
                    this.userManagementMasterRepository.save(userManagementMaster);
                }
            }
        }

        Set<EntranceExamMaster> entranceExamMasters = this.userRepository.findEntranceExamMastersByUserId(teacher.getId());
        entranceExamMastersList.addAll(entranceExamMasters);

        Set<StandardMaster> standardMasters1 = this.userRepository.findStandardMasterByUserId(teacher.getId());
        standardMastersList.addAll(standardMasters1);

        Set<SubjectMaster> subjectMasters1 = this.userRepository.findSubjectMasterByUserId(teacher.getId());
        subjectMastersList.addAll(subjectMasters1);

        teacher.setSubjectMasters(subjectMastersList);
        teacher.setEntranceExamMasters(entranceExamMastersList);
        teacher.setStandardMasters(standardMastersList);

        this.userRepository.save(teacher);

        if (isDuplicateFound) {
            logger.info("Some duplicates found, new records added for teacher: {}", teacher.getId());
            return new MainResponse("Some records already existed, new ones were added.", HttpStatus.OK.value(), true);
        } else {
            logger.info("Teacher assignments added successfully for teacher: {}", teacher.getId());
            return new MainResponse("Teacher assignments successfully added.", HttpStatus.OK.value(), true);
        }
    }

    @Override
    public MainResponse updateTeacherAssignments(TeacherRequestAssignment teacherRequestAssignment) {
        logger.info("Updating teacher assignments for teacher ID: {}", teacherRequestAssignment.getTeacherId());
        MainResponse mainResponse = new MainResponse();
        boolean flagPresent = false;

        User teacher = this.userRepository.findById(teacherRequestAssignment.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherRequestAssignment.getTeacherId()));

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(teacherRequestAssignment.getEntranceExamId())
                .orElseThrow(() -> new RuntimeException("Entrance exam not found with ID: " + teacherRequestAssignment.getEntranceExamId()));

        Set<StandardMaster> standardMastersList = new HashSet<>();
        Set<SubjectMaster> subjectMastersList = new HashSet<>();
        Set<EntranceExamMaster> entranceExamMastersList = new HashSet<>();
        entranceExamMastersList.add(entranceExamMaster);

        for (TeacherStandardRequest standard : teacherRequestAssignment.getStandards()) {
            StandardMaster standardMaster = this.standardRepository.findById(standard.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found with ID: " + standard.getStandardId()));
            standardMastersList.add(standardMaster);

            List<UserManagementMaster> userManagementMastersDeleteList = this.userManagementMasterRepository
                    .findByTeacherIdAndEntranceExamIdAndStandardId(
                            teacher.getId(), entranceExamMaster.getEntranceExamId(), standard.getStandardId());

            List<UserManagementMaster> userManagementMastersList = new ArrayList<>();

            for (Integer subjectId : standard.getSubjectIds()) {
                SubjectMaster subjectMaster = this.subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));
                subjectMastersList.add(subjectMaster);

                UserManagementMaster userManagementMaster = new UserManagementMaster();
                userManagementMaster.setTeacherId(teacher.getId());
                userManagementMaster.setStatus("Active");
                userManagementMaster.setSubjectId(subjectId);
                userManagementMaster.setStandardId(standard.getStandardId());
                userManagementMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());

                if (userManagementMasterRepository.existsByTeacherIdAndEntranceExamIdAndStandardIdAndSubjectId(
                        teacher.getId(), entranceExamMaster.getEntranceExamId(), standard.getStandardId(), subjectId)) {
                    flagPresent = true;
                    userManagementMastersList.add(userManagementMasterRepository.findByTeacherIdAndEntranceExamIdAndStandardIdAndSubjectId(
                            teacher.getId(), entranceExamMaster.getEntranceExamId(), standard.getStandardId(), subjectId));
                } else {
                    userManagementMastersList.add(this.userManagementMasterRepository.save(userManagementMaster));
                }
            }

            userManagementMastersDeleteList.removeAll(userManagementMastersList);
            this.userManagementMasterRepository.deleteAll(userManagementMastersDeleteList);

            userManagementMastersDeleteList.forEach(userManagementMaster ->
                    removeSubject(userManagementMaster.getTeacherId(), userManagementMaster.getSubjectId())
            );
        }

        Set<EntranceExamMaster> entranceExamMasters = this.userRepository.findEntranceExamMastersByUserId(teacher.getId());
        entranceExamMastersList.addAll(entranceExamMasters);

        Set<StandardMaster> standardMasters1 = this.userRepository.findStandardMasterByUserId(teacher.getId());
        standardMastersList.addAll(standardMasters1);

        Set<SubjectMaster> subjectMasters1 = this.userRepository.findSubjectMasterByUserId(teacher.getId());
        subjectMastersList.addAll(subjectMasters1);

        teacher.setSubjectMasters(subjectMastersList);
        teacher.setEntranceExamMasters(entranceExamMastersList);
        teacher.setStandardMasters(standardMastersList);

        this.userRepository.save(teacher);

        if (flagPresent) {
            logger.info("Teacher assignments updated with duplicates for teacher: {}", teacher.getId());
            return new MainResponse("Some records already existed, new ones were added.", HttpStatus.OK.value(), true);
        } else {
            logger.info("Teacher assignments updated successfully for teacher: {}", teacher.getId());
            return new MainResponse("Teacher assignments successfully updated.", HttpStatus.OK.value(), true);
        }
    }

    @Transactional
    public void removeSubject(Long userId, Integer subjectId) {
        logger.info("Removing subject {} for user {}", subjectId, userId);
        userRepository.deleteSubject(userId, subjectId);
    }

    @Override
    public MainResponse deleteMappedDataOfTeacher(Long id, Integer entranceExamId, Integer standardId, String role) {
        logger.info("Deleting mapped data for {} ID: {}, entrance exam: {}, standard: {}", role, id, entranceExamId, standardId);
        if (role.equalsIgnoreCase("teacher")) {
            if (id == null || id <= 0 || entranceExamId == null || standardId == null) {
                logger.error("Invalid parameters for deleting teacher mapped data");
                return new MainResponse("Invalid input parameters", HttpStatus.BAD_REQUEST.value(), false);
            }

            List<UserManagementMaster> userManagementMasters = userManagementMasterRepository.deleteMappedDataOfTeacher(id, entranceExamId, standardId);
            if (userManagementMasters == null || userManagementMasters.isEmpty()) {
                logger.warn("No data found for teacher {}, entrance exam {}, standard {}", id, entranceExamId, standardId);
                return new MainResponse("No data found for the given inputs", HttpStatus.NOT_FOUND.value(), false);
            }

            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                logger.warn("User not found: {}", id);
                return new MainResponse("User not found", HttpStatus.NOT_FOUND.value(), false);
            }

            User user = userOpt.get();

            List<Integer> subjectIds = new ArrayList<>();
            boolean flag = false;

            for (UserManagementMaster managementMaster : userManagementMasters) {
                if (managementMaster != null) {
                    subjectIds.add(managementMaster.getSubjectId());
                    try {
                        userManagementMasterRepository.deleteById(managementMaster.getUserManagementId());
                        flag = true;
                    } catch (Exception e) {
                        logger.error("Error deleting user management master {}: {}", managementMaster.getUserManagementId(), e.getMessage(), e);
                        flag = false;
                    }
                }
            }

            if (user.getSubjectMasters() != null) {
                List<Integer> existedSubjects = new ArrayList<>();
                for (SubjectMaster subjectMaster : user.getSubjectMasters()) {
                    if (subjectMaster != null) {
                        existedSubjects.add(subjectMaster.getSubjectId());
                    }
                }

                for (Integer existedSubject : existedSubjects) {
                    if (subjectIds.contains(existedSubject)) {
                        userRepository.deleteSubjectBySubjectId(existedSubject);
                    }
                }
            }

            if (Boolean.TRUE.equals(flag)) {
                logger.info("Teacher mapped data deleted successfully for ID: {}", id);
                return new MainResponse("User management data deleted successfully", HttpStatus.OK.value(), true);
            } else {
                logger.error("Failed to delete teacher mapped data for ID: {}", id);
                return new MainResponse("Something went wrong while deleting data", HttpStatus.BAD_REQUEST.value(), false);
            }

        } else {
            if (id == null || id <= 0 || entranceExamId == null || standardId == null) {
                logger.error("Invalid parameters for deleting student mapped data");
                return new MainResponse("Invalid input parameters", HttpStatus.BAD_REQUEST.value(), false);
            }

            List<StudentManagementMaster> studentManagementMaster = userManagementMasterRepository.deleteMappedDataOfStudent(id, entranceExamId, standardId);
            if (studentManagementMaster == null || studentManagementMaster.isEmpty()) {
                logger.warn("No data found for student {}, entrance exam {}, standard {}", id, entranceExamId, standardId);
                return new MainResponse("No data found for the given inputs", HttpStatus.NOT_FOUND.value(), false);
            }

            Optional<User> userOpt = userRepository.findById(id);
            if (!userOpt.isPresent()) {
                logger.warn("User not found: {}", id);
                return new MainResponse("User not found", HttpStatus.NOT_FOUND.value(), false);
            }

            User user = userOpt.get();

            List<Integer> subjectIds = new ArrayList<>();
            boolean flag = false;

            for (StudentManagementMaster managementMaster : studentManagementMaster) {
                if (managementMaster != null) {
                    subjectIds.add(managementMaster.getSubjectId());
                    try {
                        studentManagementRepository.deleteById(managementMaster.getStudentManagementId());
                        flag = true;
                    } catch (Exception e) {
                        logger.error("Error deleting student management master {}: {}", managementMaster.getStudentManagementId(), e.getMessage(), e);
                        flag = false;
                    }
                }
            }

            if (user.getSubjectMasters() != null) {
                List<Integer> existedSubjects = new ArrayList<>();
                for (SubjectMaster subjectMaster : user.getSubjectMasters()) {
                    if (subjectMaster != null) {
                        existedSubjects.add(subjectMaster.getSubjectId());
                    }
                }

                for (Integer existedSubject : existedSubjects) {
                    if (subjectIds.contains(existedSubject)) {
                        userRepository.deleteSubjectBySubjectId(existedSubject);
                    }
                }
            }

            if (Boolean.TRUE.equals(flag)) {
                logger.info("Student mapped data deleted successfully for ID: {}", id);
                return new MainResponse("User management data deleted successfully", HttpStatus.OK.value(), true);
            } else {
                logger.error("Failed to delete student mapped data for ID: {}", id);
                return new MainResponse("Something went wrong while deleting data", HttpStatus.BAD_REQUEST.value(), false);
            }
        }
    }

    @Override
    public StudentMappedData studentMappedData(Long id) {
        logger.info("Fetching student mapped data for ID: {}", id);
        StudentMappedData studentMappedData = new StudentMappedData();
        studentMappedData.setEntranceExamRequests(new ArrayList<>());

        List<StudentManagementMaster> studentManagementMasters =
                this.studentManagementRepository.getStudentManagementsByStudentId(id);

        if (!studentManagementMasters.isEmpty()) {
            studentMappedData.setStudentId(studentManagementMasters.get(0).getStudentId());
            studentMappedData.setTeacherId(studentManagementMasters.get(0).getTeacherId());
        }

        Map<Integer, Map<Integer, List<Integer>>> groupedData = new HashMap<>();

        for (StudentManagementMaster master : studentManagementMasters) {
            groupedData
                    .computeIfAbsent(master.getEntranceExamId(), examId -> new HashMap<>())
                    .computeIfAbsent(master.getStandardId(), standardId -> new ArrayList<>())
                    .add(master.getSubjectId());
        }

        for (Map.Entry<Integer, Map<Integer, List<Integer>>> examEntry : groupedData.entrySet()) {
            Integer entranceExamId = examEntry.getKey();
            Map<Integer, List<Integer>> standards = examEntry.getValue();

            StudentEntranceExamRequest examRequest = new StudentEntranceExamRequest();
            examRequest.setEntranceExamId(entranceExamId);
            examRequest.setStandards(new ArrayList<>());

            for (Map.Entry<Integer, List<Integer>> standardEntry : standards.entrySet()) {
                Integer standardId = standardEntry.getKey();
                List<Integer> subjectIds = standardEntry.getValue();

                TeacherStandardRequest standardRequest = new TeacherStandardRequest();
                standardRequest.setStandardId(standardId);
                standardRequest.setSubjectIds(subjectIds);

                examRequest.getStandards().add(standardRequest);
            }
            studentMappedData.getEntranceExamRequests().add(examRequest);
        }

        logger.info("Student mapped data fetched for ID: {}", id);
        return studentMappedData;
    }

    @Override
    public List<PendingRolesList> pendingTeacherList() {
        logger.info("Fetching pending teacher list");
        List<PendingRolesList> pendingTeacherList = this.userRepository.pendingTeacherList();
        for (PendingRolesList prl : pendingTeacherList) {
            Optional<User> teacherOpt = userRepository.findById(prl.getCreatorId());
            if (!teacherOpt.isPresent()) {
                logger.warn("Teacher not found for ID: {}", prl.getCreatorId());
                continue;
            }

            User teacher = teacherOpt.get();

            TeacherResponse teacherResponse = new TeacherResponse();
            StringBuilder rolesStr = new StringBuilder();
            for (Role role : teacher.getRoles()) {
                if (role != null) {
                    rolesStr.append(role.getName()).append(", ");
                }
            }
            String rolesOutput = rolesStr.length() > 0
                    ? rolesStr.substring(0, rolesStr.length() - 2)
                    : "No roles found";

            if (rolesOutput.equalsIgnoreCase("ROLE_INSTITUTE")) {
                teacherResponse.setFirstName(teacher.getInstituteName());
            } else {
                teacherResponse.setFirstName(teacher.getFirstName());
                teacherResponse.setLastName(teacher.getLastName());
            }

            teacherResponse.setId(teacher.getId());
            teacherResponse.setEmail(teacher.getEmail());
            teacherResponse.setMobile(teacher.getMobile());
            prl.setCreator(teacherResponse);
        }

        logger.info("Pending teachers found: {}", pendingTeacherList.size());
        return pendingTeacherList;
    }

    public List<PendingRolesList> pendingStudentList() {
        logger.info("Fetching pending student list");
        List<PendingRolesList> pendingStudentList = this.userRepository.pendingStudentList();
        for (PendingRolesList prl : pendingStudentList) {
            Optional<User> teacherOpt = userRepository.findById(prl.getCreatorId());
            if (!teacherOpt.isPresent()) {
                logger.warn("Teacher not found for ID: {}", prl.getCreatorId());
                continue;
            }

            User teacher = teacherOpt.get();

            TeacherResponse teacherResponse = new TeacherResponse();
            StringBuilder rolesStr = new StringBuilder();
            for (Role role : teacher.getRoles()) {
                if (role != null) {
                    rolesStr.append(role.getName()).append(", ");
                }
            }
            String rolesOutput = rolesStr.length() > 0
                    ? rolesStr.substring(0, rolesStr.length() - 2)
                    : "No roles found";

            if (rolesOutput.equalsIgnoreCase("ROLE_INSTITUTE")) {
                teacherResponse.setFirstName(teacher.getInstituteName());
            } else {
                teacherResponse.setFirstName(teacher.getFirstName());
                teacherResponse.setLastName(teacher.getLastName());
            }
            teacherResponse.setId(teacher.getId());
            teacherResponse.setEmail(teacher.getEmail());
            teacherResponse.setMobile(teacher.getMobile());
            prl.setCreator(teacherResponse);
        }

        logger.info("Pending students found: {}", pendingStudentList.size());
        return pendingStudentList;
    }

    @Override
    public List<PendingRolesList> pendingParentList() {
        logger.info("Fetching pending parent list");
        List<PendingRolesList> pendingParentList = this.userRepository.pendingParentList();
        for (PendingRolesList prl : pendingParentList) {
            Optional<User> teacherOpt = userRepository.findById(prl.getCreatorId());
            if (!teacherOpt.isPresent()) {
                logger.warn("Teacher not found for ID: {}", prl.getCreatorId());
                continue;
            }

            User teacher = teacherOpt.get();
            TeacherResponse teacherResponse = new TeacherResponse();
            teacherResponse.setId(teacher.getId());
            teacherResponse.setFirstName(teacher.getFirstName());
            teacherResponse.setLastName(teacher.getLastName());
            teacherResponse.setEmail(teacher.getEmail());
            teacherResponse.setMobile(teacher.getMobile());
            prl.setCreator(teacherResponse);
        }

        logger.info("Pending parents found: {}", pendingParentList.size());
        return pendingParentList;
    }

    @Override
    public List<PendingRolesList> pendingAdminList() {
        logger.info("Fetching pending admin list");
        List<PendingRolesList> pendingAdminList = userRepository.pendingAdminList();

        for (PendingRolesList prl : pendingAdminList) {
            if (prl.getCreatorId() != null) {
                userRepository.findById(prl.getCreatorId()).ifPresent(creator -> {
                    TeacherResponse teacherResponse = new TeacherResponse();
                    teacherResponse.setId(creator.getId());
                    teacherResponse.setFirstName(creator.getFirstName());
                    teacherResponse.setLastName(creator.getLastName());
                    teacherResponse.setEmail(creator.getEmail());
                    teacherResponse.setMobile(creator.getMobile());
                    prl.setCreator(teacherResponse);
                });
            } else {
                prl.setCreator(null);
            }
        }

        logger.info("Pending admins found: {}", pendingAdminList.size());
        return pendingAdminList;
    }

    @Override
    public List<PendingRolesList> pendingInstituteList() {
        logger.info("Fetching pending institute list");
        List<PendingRolesList> pendingInstituteList = this.userRepository.pendingInstituteList();

        for (PendingRolesList prl : pendingInstituteList) {
            Optional<User> teacherOpt = userRepository.findById(prl.getCreatorId());
            if (!teacherOpt.isPresent()) {
                logger.warn("Teacher not found for ID: {}", prl.getCreatorId());
                continue;
            }

            User teacher = teacherOpt.get();
            TeacherResponse teacherResponse = new TeacherResponse();
            teacherResponse.setId(teacher.getId());
            teacherResponse.setFirstName(teacher.getFirstName());
            teacherResponse.setLastName(teacher.getLastName());
            teacherResponse.setEmail(teacher.getEmail());
            teacherResponse.setMobile(teacher.getMobile());
            prl.setCreator(teacherResponse);
        }

        logger.info("Pending institutes found: {}", pendingInstituteList.size());
        return pendingInstituteList;
    }

    @Override
    @Transactional
    public MainResponse acceptUser(Long id, Long acceptby) {
        logger.info("Accepting user ID: {} by acceptor ID: {}", id, acceptby);
        MainResponse mainResponse = new MainResponse();

        Optional<User> userOpt = this.userRepository.findById(id);
        Optional<User> acceptedByOpt = this.userRepository.findById(acceptby);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Check if user has SUPER_ADMIN role
            boolean isSuperAdmin = user.getRoles() != null && user.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_SUPER_ADMIN);

            if (isSuperAdmin) {
                logger.warn("Attempt to modify SUPER_ADMIN role for user: {}", id);
                mainResponse.setMessage("SUPER_ADMIN role cannot be modified");
                mainResponse.setResponseCode(HttpStatus.FORBIDDEN.value());
                mainResponse.setFlag(false);
                return mainResponse;
            }

            if (acceptedByOpt.isPresent()) {
                user.setStatus("Active");
                user.setAcceptBy(acceptedByOpt.get().getId());
                this.userRepository.save(user);

                mainResponse.setMessage("User accepted");
                mainResponse.setResponseCode(HttpStatus.OK.value());
                mainResponse.setFlag(true);
                logger.info("User accepted: {}", id);
            } else {
                logger.warn("Acceptor not found: {}", acceptby);
                mainResponse.setMessage("Admin User not found");
                mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
                mainResponse.setFlag(false);
            }
        } else {
            logger.warn("User not found for acceptance: {}", id);
            mainResponse.setMessage("User not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }

        return mainResponse;
    }

    @Override
    public List<DashBoardDetails> dashBoardDetails() {
        logger.info("Fetching dashboard details");
        List<DashBoardDetails> dashBoardDetails = new ArrayList<>();
        List<Role> roles = this.roleRepository.findAll();

        for (Role role : roles) {
            if (role.getName().name().equals("ROLE_ADMIN")) {
                DashBoardDetails dashBoardDetails1 = new DashBoardDetails();
                Integer totalPendingCount = this.userRepository.roleWisePendingCount(role.getName());
                Integer total = this.userRepository.adminCount();
                dashBoardDetails1.setTotal(total);
                dashBoardDetails1.setTotalPendingCount(totalPendingCount);
                dashBoardDetails1.setRole("Admin");
                dashBoardDetails1.setIcon("fas fa-users");
                dashBoardDetails1.setNzIcon("fas fa-users");
                dashBoardDetails1.setColor("info-card");
                dashBoardDetails1.setMessage("Marketing tasks pending");
                dashBoardDetails1.setTrend(-2);
                List actions = new ArrayList();
                actions.add("View Details");
                dashBoardDetails1.setActions(actions);
                dashBoardDetails.add(dashBoardDetails1);

            } else if (role.getName().name().equals("ROLE_TEACHER")) {
                DashBoardDetails dashBoardDetails1 = new DashBoardDetails();
                Integer totalPendingCount = this.userRepository.roleWisePendingCount(role.getName());
                dashBoardDetails1.setTotalPendingCount(totalPendingCount);
                dashBoardDetails1.setRole("Teacher");
                Integer total = this.userRepository.teacherCount();
                dashBoardDetails1.setTotal(total);
                dashBoardDetails1.setIcon("user");
                dashBoardDetails1.setNzIcon("fas fa-user-graduate");
                dashBoardDetails1.setColor("primary-card");
                dashBoardDetails1.setMessage("Teachers pending approval");
                dashBoardDetails1.setTrend(5);
                List actions = new ArrayList();
                actions.add("View Details");
                actions.add("Export Data");
                actions.add("Send Reminder");
                dashBoardDetails1.setActions(actions);
                dashBoardDetails.add(dashBoardDetails1);

            } else if (role.getName().name().equals("ROLE_STUDENT")) {
                DashBoardDetails dashBoardDetails1 = new DashBoardDetails();
                Integer totalPendingCount = this.userRepository.roleWisePendingCount(role.getName());
                dashBoardDetails1.setTotalPendingCount(totalPendingCount);
                dashBoardDetails1.setRole("Student");
                Integer total = this.userRepository.studentCount();
                dashBoardDetails1.setTotal(total);
                dashBoardDetails1.setIcon("graduation");
                dashBoardDetails1.setNzIcon("fas fa-users");
                dashBoardDetails1.setColor("success-card");
                dashBoardDetails1.setMessage("Students pending registration");
                dashBoardDetails1.setTrend(-5);
                List actions = new ArrayList();
                actions.add("View Details");
                actions.add("Export Data");
                actions.add("Send Reminder");
                dashBoardDetails1.setActions(actions);
                dashBoardDetails.add(dashBoardDetails1);

            } else if (role.getName().name().equals("ROLE_PARENT")) {
                DashBoardDetails dashBoardDetails1 = new DashBoardDetails();
                Integer totalPendingCount = this.userRepository.roleWisePendingCount(role.getName());
                dashBoardDetails1.setTotalPendingCount(totalPendingCount);
                dashBoardDetails1.setRole("Parent");
                Integer total = this.userRepository.parentCount();
                dashBoardDetails1.setTotal(total);
                dashBoardDetails1.setIcon("team");
                dashBoardDetails1.setNzIcon("fas fa-users");
                dashBoardDetails1.setColor("warning-card");
                dashBoardDetails1.setMessage("All parents verified");
                dashBoardDetails1.setTrend(8);
                List actions = new ArrayList();
                actions.add("View Details");
                actions.add("Export Data");
                actions.add("Send Reminder");
                dashBoardDetails1.setActions(actions);
                dashBoardDetails.add(dashBoardDetails1);

            } else if (role.getName().name().equals("ROLE_INSTITUTE")) {
                DashBoardDetails dashBoardDetails1 = new DashBoardDetails();
                Integer totalPendingCount = this.userRepository.roleWisePendingCount(role.getName());
                dashBoardDetails1.setTotalPendingCount(totalPendingCount);
                dashBoardDetails1.setRole("Institute");
                Integer total = this.userRepository.totalInstituteCount();
                dashBoardDetails1.setTotal(total);
                dashBoardDetails1.setIcon("fas fa-school");
                dashBoardDetails1.setNzIcon("fas fa-chalkboard-teacher");
                dashBoardDetails1.setColor("danger-card");
                dashBoardDetails1.setMessage("All institutions verified");
                dashBoardDetails1.setTrend(15);
                List actions = new ArrayList();
                actions.add("View Details");
                dashBoardDetails1.setActions(actions);
                dashBoardDetails.add(dashBoardDetails1);
            }
        }

        logger.info("Dashboard details fetched successfully");
        return dashBoardDetails;
    }

    @Override
    public UserManagementResponse teacherIdWiseAccessManagements(Long teacherId) {
        logger.info("Fetching teacher access management for ID: {}", teacherId);
        UserManagementResponse userManagementResponse = new UserManagementResponse();
        List<UserManagementMaster> userManagementMaster = this.userManagementMasterRepository.getAllByTeacherId(teacherId);
        Map<Integer, TeacherAssessmentEntranceExams> entranceExamsMap = new HashMap<>();

        for (UserManagementMaster managementMaster : userManagementMaster) {
            Integer entranceExamId = managementMaster.getEntranceExamId();

            TeacherAssessmentEntranceExams teacherAssessmentEntranceExams = entranceExamsMap.getOrDefault(entranceExamId, new TeacherAssessmentEntranceExams());
            if (teacherAssessmentEntranceExams.getEntranceExamId() == null) {
                Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(entranceExamId);
                if (entranceExamMaster.isPresent()) {
                    teacherAssessmentEntranceExams.setEntranceExamId(entranceExamId);
                    teacherAssessmentEntranceExams.setEntranceExamName(entranceExamMaster.get().getEntranceExamName());
                    teacherAssessmentEntranceExams.setEntranceStatus(entranceExamMaster.get().getStatus());
                    teacherAssessmentEntranceExams.setTeacherAssessmentStandards(new ArrayList<>());
                    entranceExamsMap.put(entranceExamId, teacherAssessmentEntranceExams);
                }
            }

            Optional<StandardMaster> standardMaster = this.standardRepository.findById(managementMaster.getStandardId());
            if (standardMaster.isPresent()) {
                int standardId = standardMaster.get().getStandardId();
                List<TeacherAssessmentStandards> standardsList = teacherAssessmentEntranceExams.getTeacherAssessmentStandards();

                TeacherAssessmentStandards teacherAssessmentStandards = standardsList.stream()
                        .filter(s -> s.getStandardId() == standardId)
                        .findFirst()
                        .orElseGet(() -> {
                            TeacherAssessmentStandards newStandard = new TeacherAssessmentStandards();
                            newStandard.setStandardId(standardId);
                            newStandard.setStandardName(standardMaster.get().getStandardName());
                            newStandard.setStandardStatus(standardMaster.get().getStatus());
                            newStandard.setTeacherAssessmentSubjects(new ArrayList<>());
                            standardsList.add(newStandard);
                            return newStandard;
                        });

                Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(managementMaster.getSubjectId());
                if (subjectMaster.isPresent()) {
                    TeacherAssessmentSubjects teacherAssessmentSubjects = new TeacherAssessmentSubjects();
                    teacherAssessmentSubjects.setUserManagementId(managementMaster.getUserManagementId());
                    teacherAssessmentSubjects.setSubjectId(subjectMaster.get().getSubjectId());
                    teacherAssessmentSubjects.setSubjectName(subjectMaster.get().getSubjectName());
                    teacherAssessmentSubjects.setSubStatus(subjectMaster.get().getStatus());
                    teacherAssessmentStandards.getTeacherAssessmentSubjects().add(teacherAssessmentSubjects);
                }
            }
        }

        userManagementResponse.setTeacherAssessmentEntranceExams(new ArrayList<>(entranceExamsMap.values()));
        logger.info("Teacher access management fetched for ID: {}", teacherId);
        return userManagementResponse;
    }

    @Override
    public List<UsersCountResponse> usersCounts() {
        logger.info("Fetching user counts");
        List<UsersCountResponse> usersCountResponses = new ArrayList<>();
        List<Role> roles = this.roleRepository.findAll();

        for (Role role : roles) {
            UsersCountResponse usersCountResponse = new UsersCountResponse();
            ERole userRole = role.getName();

            Integer totalActiveCount = 0;
            Integer totalInactiveCount = 0;

            try {
                totalActiveCount = this.userRepository.totalActiveUserCount(userRole);
                totalInactiveCount = this.userRepository.totalInActiveUserCount(userRole);
            } catch (Exception e) {
                logger.error("Error fetching counts for role {}: {}", userRole, e.getMessage());
                continue;
            }

            switch (userRole) {
                case ROLE_STUDENT:
                    Integer totalCount = this.userRepository.studentCount();
                    usersCountResponse.setTitle("Total Students");
                    usersCountResponse.setBgClass("gradient-blue");
                    usersCountResponse.setTrend(12.5);
                    usersCountResponse.setIcon("fas fa-users");
                    usersCountResponse.setTotal(totalCount);
                    break;

                case ROLE_TEACHER:
                    totalCount = this.userRepository.teacherCount();
                    usersCountResponse.setTitle("Total Teachers");
                    usersCountResponse.setBgClass("gradient-purple");
                    usersCountResponse.setTrend(-5.2);
                    usersCountResponse.setIcon("fas fa-user-graduate");
                    usersCountResponse.setTotal(totalCount);
                    break;

                case ROLE_INSTITUTE:
                    usersCountResponse.setTitle("Total Institutions");
                    totalCount = this.userRepository.instituteCount();
                    usersCountResponse.setBgClass("gradient-green");
                    usersCountResponse.setTrend(8.7);
                    usersCountResponse.setIcon("fas fa-chalkboard-teacher");
                    usersCountResponse.setTotal(totalCount);
                    break;
                case ROLE_ADMIN:
                    totalCount = this.userRepository.adminCount();
                    usersCountResponse.setTitle("Total admin");
                    usersCountResponse.setBgClass("gradient-purple");
                    usersCountResponse.setTrend(-5.2);
                    usersCountResponse.setIcon("fas fa-user-graduate");
                    usersCountResponse.setTotal(totalCount);
                    break;
                case ROLE_PARENT:
                    totalCount = this.userRepository.parentCount();
                    usersCountResponse.setTitle("Total parent");
                    usersCountResponse.setBgClass("gradient-purple");
                    usersCountResponse.setTrend(-5.2);
                    usersCountResponse.setIcon("fas fa-user-graduate");
                    usersCountResponse.setTotal(totalCount);
                    break;

                default:
                    continue;
            }

            usersCountResponse.setActive(totalActiveCount != null ? totalActiveCount : 0);
            usersCountResponse.setInactive(totalInactiveCount != null ? totalInactiveCount : 0);
            usersCountResponses.add(usersCountResponse);
        }

        logger.info("User counts fetched successfully");
        return usersCountResponses;
    }

    @Override
    public List<ActivationCountResponse> activationCounts() {
        logger.info("Fetching activation counts");
        Map<ERole, ActivationCountResponse> roleDto = new EnumMap<>(ERole.class);
        addRole(roleDto, ERole.ROLE_TEACHER, "Teachers Activations",
                "fas fa-chalkboard-teacher", "bg-blue-light", "text-blue");
        addRole(roleDto, ERole.ROLE_STUDENT, "Students Activations",
                "fas fa-user-graduate", "bg-green-light", "text-green");
        addRole(roleDto, ERole.ROLE_PARENT, "Parents Activations",
                "fas fa-users", "bg-orange-light", "text-orange");
        addRole(roleDto, ERole.ROLE_INSTITUTE, "Institutions Activations",
                "fas fa-school", "bg-purple-light", "text-purple");

        List<Long> creatorIds = userRepository.getAllCreatorIds().stream()
                .filter(id -> id != null && id != 0)
                .collect(Collectors.toList());

        for (Long creatorId : creatorIds) {
            User creator = userRepository.findById(creatorId).orElse(null);
            if (creator == null) continue;

            boolean isAdmin = creator.getRoles().stream()
                    .map(Role::getName)
                    .anyMatch(er -> er == ERole.ROLE_ADMIN);
            if (!isAdmin) continue;

            for (ERole role : roleDto.keySet()) {
                Integer count = userRepository.getRoleWiseCounts(creatorId, role);
                ActivationCountResponse dto = roleDto.get(role);
                dto.setCount(dto.getCount() + (count != null ? count : 0));
            }
        }

        logger.info("Activation counts fetched successfully");
        return new ArrayList<>(roleDto.values());
    }

    private void addRole(Map<ERole, ActivationCountResponse> map,
                         ERole role, String title,
                         String icon, String bgClass, String colorClass) {

        ActivationCountResponse dto = new ActivationCountResponse();
        dto.setTitle(title);
        dto.setIcon(icon);
        dto.setBgClass(bgClass);
        dto.setColorClass(colorClass);
        dto.setCount(0);
        map.put(role, dto);
    }

    @Override
    public MainResponse bulkUserChangeStatus(BulkUserChangeStatusRequest bulkUserChangeStatusRequest) {
        logger.info("Bulk changing user status for {} users", bulkUserChangeStatusRequest.getUserIds().size());
        MainResponse mainResponse = new MainResponse();
        Boolean flag = false;
        for (Long userId : bulkUserChangeStatusRequest.getUserIds()) {
            User user = this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            user.setStatus(bulkUserChangeStatusRequest.getStatus());
            user.setAcceptBy(bulkUserChangeStatusRequest.getAcceptBy());
            try {
                this.userRepository.save(user);
                flag = true;
            } catch (Exception e) {
                logger.error("Error updating status for user {}: {}", userId, e.getMessage(), e);
                flag = false;
            }
        }
        if (Boolean.TRUE.equals(flag)) {
            mainResponse.setMessage("Users status updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
            logger.info("Bulk user status update completed successfully");
        } else {
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public UserManagementResponse userIdAndEntranceExamIdWiseData(Long id, Integer entranceExamId1) {
        logger.info("Fetching user and entrance exam data for user ID: {}, entrance exam ID: {}", id, entranceExamId1);
        UserManagementResponse userManagementResponse = new UserManagementResponse();
        User teacher = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        List<UserManagementMaster> userManagementMaster = this.userManagementMasterRepository.getAllByTeacherIdAndEntranceExamId(teacher.getId(), entranceExamId1);

        Map<Integer, TeacherAssessmentEntranceExams> entranceExamsMap = new HashMap<>();

        for (UserManagementMaster managementMaster : userManagementMaster) {
            Integer entranceExamId = managementMaster.getEntranceExamId();

            TeacherAssessmentEntranceExams teacherAssessmentEntranceExams = entranceExamsMap.getOrDefault(entranceExamId, new TeacherAssessmentEntranceExams());
            if (teacherAssessmentEntranceExams.getEntranceExamId() == null) {
                Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(entranceExamId);
                if (entranceExamMaster.isPresent()) {
                    teacherAssessmentEntranceExams.setEntranceExamId(entranceExamId);
                    teacherAssessmentEntranceExams.setEntranceExamName(entranceExamMaster.get().getEntranceExamName());
                    teacherAssessmentEntranceExams.setEntranceStatus(entranceExamMaster.get().getStatus());
                    teacherAssessmentEntranceExams.setTeacherAssessmentStandards(new ArrayList<>());
                    entranceExamsMap.put(entranceExamId, teacherAssessmentEntranceExams);
                }
            }

            Optional<StandardMaster> standardMaster = this.standardRepository.findById(managementMaster.getStandardId());
            if (standardMaster.isPresent()) {
                int standardId = standardMaster.get().getStandardId();
                List<TeacherAssessmentStandards> standardsList = teacherAssessmentEntranceExams.getTeacherAssessmentStandards();

                TeacherAssessmentStandards teacherAssessmentStandards = standardsList.stream()
                        .filter(s -> s.getStandardId() == standardId)
                        .findFirst()
                        .orElseGet(() -> {
                            TeacherAssessmentStandards newStandard = new TeacherAssessmentStandards();
                            newStandard.setStandardId(standardId);
                            newStandard.setStandardName(standardMaster.get().getStandardName());
                            newStandard.setStandardStatus(standardMaster.get().getStatus());
                            newStandard.setTeacherAssessmentSubjects(new ArrayList<>());
                            standardsList.add(newStandard);
                            return newStandard;
                        });

                Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(managementMaster.getSubjectId());
                if (subjectMaster.isPresent()) {
                    TeacherAssessmentSubjects teacherAssessmentSubjects = new TeacherAssessmentSubjects();
                    teacherAssessmentSubjects.setUserManagementId(managementMaster.getUserManagementId());
                    teacherAssessmentSubjects.setSubjectId(subjectMaster.get().getSubjectId());
                    teacherAssessmentSubjects.setSubjectName(subjectMaster.get().getSubjectName());
                    teacherAssessmentSubjects.setSubStatus(subjectMaster.get().getStatus());
                    teacherAssessmentStandards.getTeacherAssessmentSubjects().add(teacherAssessmentSubjects);
                }
            }
        }

        userManagementResponse.setTeacherAssessmentEntranceExams(new ArrayList<>(entranceExamsMap.values()));
        logger.info("User and entrance exam data fetched successfully");
        return userManagementResponse;
    }

    @Override
    public MainResponse addStudentAssignment(TeacherRequestAssignment teacherRequestAssignment) {
        logger.info("Adding student assignment for student ID: {}", teacherRequestAssignment.getTeacherId());
        User student = this.userRepository.findById(teacherRequestAssignment.getTeacherId()).orElseThrow(() -> new RuntimeException("Student not found"));
        Set<StandardMaster> standardMastersList = new HashSet<>();
        Set<SubjectMaster> subjectMastersList = new HashSet<>();
        Set<EntranceExamMaster> entranceExamMastersList = new HashSet<>();

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(teacherRequestAssignment.getEntranceExamId()).orElseThrow(() -> new RuntimeException("Entrance exam not found"));
        entranceExamMastersList.add(entranceExamMaster);

        boolean isDuplicateFound = false;

        // Get teacher ID safely
        Long teacherId = null;
        if (student.getTeacher() != null && !student.getTeacher().isEmpty()) {
            teacherId = student.getTeacher().iterator().next().getId();
            logger.debug("Found teacher ID: {} for student: {}", teacherId, student.getId());
        } else {
            logger.error("Student ID {} has no assigned teacher!", student.getId());
            return new MainResponse("Student must have an assigned teacher before adding assignments",
                    HttpStatus.BAD_REQUEST.value(), false);
        }

        for (TeacherStandardRequest standard : teacherRequestAssignment.getStandards()) {
            StandardMaster standardMaster = this.standardRepository.findById(standard.getStandardId()).orElseThrow(() -> new RuntimeException("Standard not found"));
            standardMastersList.add(standardMaster);

            for (Integer subjectId : standard.getSubjectIds()) {
                SubjectMaster subjectMaster = this.subjectRepository.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject not found"));
                subjectMastersList.add(subjectMaster);

                StudentManagementMaster studentManagementMaster = new StudentManagementMaster();
                studentManagementMaster.setStudentId(student.getId());
                studentManagementMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
                studentManagementMaster.setStandardId(standard.getStandardId());
                studentManagementMaster.setSubjectId(subjectId);
                studentManagementMaster.setStatus("Active");

                // Use the teacherId variable
                studentManagementMaster.setTeacherId(teacherId);

                studentManagementMaster.setCreatedBy(student.getCreatorId());

                // Check in the correct repository
                if (studentManagementRepository.existsByStudentIdAndEntranceExamIdAndStandardIdAndSubjectId(
                        student.getId(),
                        entranceExamMaster.getEntranceExamId(),
                        standard.getStandardId(),
                        subjectId)) {
                    logger.info("Already present for Student ID: {}, Standard ID: {}, Subject ID: {}",
                            student.getId(), standard.getStandardId(), subjectId);
                    isDuplicateFound = true;
                } else {
                    logger.debug("Saving new assignment for student: {}", student.getId());
                    this.studentManagementRepository.save(studentManagementMaster);
                }
            }
        }

        Set<EntranceExamMaster> entranceExamMasters = this.userRepository.findEntranceExamMastersByUserId(student.getId());
        entranceExamMastersList.addAll(entranceExamMasters);

        Set<StandardMaster> standardMasters1 = this.userRepository.findStandardMasterByUserId(student.getId());
        standardMastersList.addAll(standardMasters1);

        Set<SubjectMaster> subjectMasters1 = this.userRepository.findSubjectMasterByUserId(student.getId());
        subjectMastersList.addAll(subjectMasters1);

        student.setSubjectMasters(subjectMastersList);
        student.setEntranceExamMasters(entranceExamMastersList);
        student.setStandardMasters(standardMastersList);

        this.userRepository.save(student);

        if (isDuplicateFound) {
            logger.info("Some duplicates found for student: {}", student.getId());
            return new MainResponse("Some records already existed, new ones were added.", HttpStatus.OK.value(), true);
        } else {
            logger.info("Student assignments added successfully for student: {}", student.getId());
            return new MainResponse("Student assignments successfully added.", HttpStatus.OK.value(), true);
        }
    }

    @Override
    public UserManagementResponse studentIdWiseAccessManagements(Long studentId) {
        logger.info("Fetching student access management for ID: {}", studentId);
        UserManagementResponse userManagementResponse = new UserManagementResponse();
        List<StudentManagementMaster> studentManagementMasters = this.userManagementMasterRepository.getAllByStudentId(studentId);
        Map<Integer, TeacherAssessmentEntranceExams> entranceExamsMap = new HashMap<>();

        for (StudentManagementMaster studentManagementMaster : studentManagementMasters) {
            Integer entranceExamId = studentManagementMaster.getEntranceExamId();

            TeacherAssessmentEntranceExams teacherAssessmentEntranceExams = entranceExamsMap.getOrDefault(entranceExamId, new TeacherAssessmentEntranceExams());
            if (teacherAssessmentEntranceExams.getEntranceExamId() == null) {
                Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(entranceExamId);
                if (entranceExamMaster.isPresent()) {
                    teacherAssessmentEntranceExams.setEntranceExamId(entranceExamId);
                    teacherAssessmentEntranceExams.setEntranceExamName(entranceExamMaster.get().getEntranceExamName());
                    teacherAssessmentEntranceExams.setEntranceStatus(entranceExamMaster.get().getStatus());
                    teacherAssessmentEntranceExams.setTeacherAssessmentStandards(new ArrayList<>());
                    entranceExamsMap.put(entranceExamId, teacherAssessmentEntranceExams);
                }
            }

            Optional<StandardMaster> standardMaster = this.standardRepository.findById(studentManagementMaster.getStandardId());
            if (standardMaster.isPresent()) {
                int standardId = standardMaster.get().getStandardId();
                List<TeacherAssessmentStandards> standardsList = teacherAssessmentEntranceExams.getTeacherAssessmentStandards();

                TeacherAssessmentStandards teacherAssessmentStandards = standardsList.stream()
                        .filter(s -> s.getStandardId() == standardId)
                        .findFirst()
                        .orElseGet(() -> {
                            TeacherAssessmentStandards newStandard = new TeacherAssessmentStandards();
                            newStandard.setStandardId(standardId);
                            newStandard.setStandardName(standardMaster.get().getStandardName());
                            newStandard.setStandardStatus(standardMaster.get().getStatus());
                            newStandard.setTeacherAssessmentSubjects(new ArrayList<>());
                            standardsList.add(newStandard);
                            return newStandard;
                        });

                Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(studentManagementMaster.getSubjectId());
                if (subjectMaster.isPresent()) {
                    TeacherAssessmentSubjects teacherAssessmentSubjects = new TeacherAssessmentSubjects();
                    teacherAssessmentSubjects.setUserManagementId(studentManagementMaster.getStudentManagementId());
                    teacherAssessmentSubjects.setSubjectId(subjectMaster.get().getSubjectId());
                    teacherAssessmentSubjects.setSubjectName(subjectMaster.get().getSubjectName());
                    teacherAssessmentSubjects.setSubStatus(subjectMaster.get().getStatus());
                    teacherAssessmentStandards.getTeacherAssessmentSubjects().add(teacherAssessmentSubjects);
                }
            }
        }

        userManagementResponse.setTeacherAssessmentEntranceExams(new ArrayList<>(entranceExamsMap.values()));
        logger.info("Student access management fetched for ID: {}", studentId);
        return userManagementResponse;
    }

    @Override
    public MainResponse updateStudentAssignments(TeacherRequestAssignment teacherRequestAssignment) {
        logger.info("Updating student assignments for student ID: {}", teacherRequestAssignment.getTeacherId());
        MainResponse mainResponse = new MainResponse();
        boolean flagPresent = false;

        User student = this.userRepository.findById(teacherRequestAssignment.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherRequestAssignment.getTeacherId()));

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(teacherRequestAssignment.getEntranceExamId())
                .orElseThrow(() -> new RuntimeException("Entrance exam not found with ID: " + teacherRequestAssignment.getEntranceExamId()));

        Set<StandardMaster> standardMastersList = new HashSet<>();
        Set<SubjectMaster> subjectMastersList = new HashSet<>();
        Set<EntranceExamMaster> entranceExamMastersList = new HashSet<>();
        entranceExamMastersList.add(entranceExamMaster);

        // Get teacher ID safely
        Long teacherId = null;
        if (student.getTeacher() != null && !student.getTeacher().isEmpty()) {
            teacherId = student.getTeacher().iterator().next().getId();
        } else {
            logger.error("Student must have an assigned teacher before updating assignments");
            return new MainResponse("Student must have an assigned teacher before updating assignments",
                    HttpStatus.BAD_REQUEST.value(), false);
        }

        for (TeacherStandardRequest standard : teacherRequestAssignment.getStandards()) {
            StandardMaster standardMaster = this.standardRepository.findById(standard.getStandardId())
                    .orElseThrow(() -> new RuntimeException("Standard not found with ID: " + standard.getStandardId()));
            standardMastersList.add(standardMaster);

            List<StudentManagementMaster> studentManagementMastersList = this.studentManagementRepository
                    .findAllByUserIdAndEntranceExamIdAndStandardId(
                            student.getId(), entranceExamMaster.getEntranceExamId(), standard.getStandardId());

            List<StudentManagementMaster> studentManagementMasterList = new ArrayList<>();

            for (Integer subjectId : standard.getSubjectIds()) {
                SubjectMaster subjectMaster = this.subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));
                subjectMastersList.add(subjectMaster);

                StudentManagementMaster studentManagementMaster = new StudentManagementMaster();
                studentManagementMaster.setStudentId(student.getId());
                studentManagementMaster.setStatus("Active");
                studentManagementMaster.setEntranceExamId(entranceExamMaster.getEntranceExamId());
                studentManagementMaster.setStandardId(standard.getStandardId());
                studentManagementMaster.setSubjectId(subjectId);
                studentManagementMaster.setTeacherId(teacherId); // Use the teacherId variable
                studentManagementMaster.setCreatedBy(student.getCreatorId());

                Long studentId = student.getId();
                if (studentManagementRepository.existsByStudentIdAndEntranceExamIdAndStandardIdAndSubjectId(
                        studentId, entranceExamMaster.getEntranceExamId(), standard.getStandardId(), subjectId)) {
                    flagPresent = true;
                    studentManagementMasterList.add(studentManagementRepository.findByUserIdAndEntranceExamIdAndStandardIdAndSubjectId(
                            student.getId(), entranceExamMaster.getEntranceExamId(), standard.getStandardId(), subjectId));
                } else {
                    studentManagementMasterList.add(this.studentManagementRepository.save(studentManagementMaster));
                }
            }

            studentManagementMastersList.removeAll(studentManagementMasterList);
            this.studentManagementRepository.deleteAll(studentManagementMastersList);

            studentManagementMastersList.forEach(studentManagementMaster ->
                    removeSubject(studentManagementMaster.getStudentId(), studentManagementMaster.getSubjectId())
            );
        }

        Set<EntranceExamMaster> entranceExamMasters = this.userRepository.findEntranceExamMastersByUserId(student.getId());
        entranceExamMastersList.addAll(entranceExamMasters);

        Set<StandardMaster> standardMasters1 = this.userRepository.findStandardMasterByUserId(student.getId());
        standardMastersList.addAll(standardMasters1);

        Set<SubjectMaster> subjectMasters1 = this.userRepository.findSubjectMasterByUserId(student.getId());
        subjectMastersList.addAll(subjectMasters1);

        student.setSubjectMasters(subjectMastersList);
        student.setEntranceExamMasters(entranceExamMastersList);
        student.setStandardMasters(standardMastersList);

        this.userRepository.save(student);

        if (flagPresent) {
            logger.info("Student assignments updated with duplicates for student: {}", student.getId());
            return new MainResponse("Some records already existed, new ones were added.", HttpStatus.OK.value(), true);
        } else {
            logger.info("Student assignments updated successfully for student: {}", student.getId());
            return new MainResponse("Student assignments successfully updated.", HttpStatus.OK.value(), true);
        }
    }

    @Override
    public UserManagementResponse studentIdAndEntranceExamIdWiseData(Long id, Integer entranceExamId1) {
        logger.info("Fetching student and entrance exam data for student ID: {}, entrance exam ID: {}", id, entranceExamId1);
        UserManagementResponse userManagementResponse = new UserManagementResponse();
        User student = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));

        List<StudentManagementMaster> studentManagementMasters = this.studentManagementRepository.getAllByStudentIdAndEntranceExamIdWiseData(student.getId(), entranceExamId1);

        Map<Integer, TeacherAssessmentEntranceExams> entranceExamsMap = new HashMap<>();

        for (StudentManagementMaster managementMaster : studentManagementMasters) {
            Integer entranceExamId = managementMaster.getEntranceExamId();

            TeacherAssessmentEntranceExams teacherAssessmentEntranceExams = entranceExamsMap.getOrDefault(entranceExamId, new TeacherAssessmentEntranceExams());
            if (teacherAssessmentEntranceExams.getEntranceExamId() == null) {
                Optional<EntranceExamMaster> entranceExamMaster = this.entranceExamRepository.findById(entranceExamId);
                if (entranceExamMaster.isPresent()) {
                    teacherAssessmentEntranceExams.setEntranceExamId(entranceExamId);
                    teacherAssessmentEntranceExams.setEntranceExamName(entranceExamMaster.get().getEntranceExamName());
                    teacherAssessmentEntranceExams.setEntranceStatus(entranceExamMaster.get().getStatus());
                    teacherAssessmentEntranceExams.setTeacherAssessmentStandards(new ArrayList<>());
                    entranceExamsMap.put(entranceExamId, teacherAssessmentEntranceExams);
                }
            }

            Optional<StandardMaster> standardMaster = this.standardRepository.findById(managementMaster.getStandardId());
            if (standardMaster.isPresent()) {
                int standardId = standardMaster.get().getStandardId();
                List<TeacherAssessmentStandards> standardsList = teacherAssessmentEntranceExams.getTeacherAssessmentStandards();

                TeacherAssessmentStandards teacherAssessmentStandards = standardsList.stream()
                        .filter(s -> s.getStandardId() == standardId)
                        .findFirst()
                        .orElseGet(() -> {
                            TeacherAssessmentStandards newStandard = new TeacherAssessmentStandards();
                            newStandard.setStandardId(standardId);
                            newStandard.setStandardName(standardMaster.get().getStandardName());
                            newStandard.setStandardStatus(standardMaster.get().getStatus());
                            newStandard.setTeacherAssessmentSubjects(new ArrayList<>());
                            standardsList.add(newStandard);
                            return newStandard;
                        });

                Optional<SubjectMaster> subjectMaster = this.subjectRepository.findById(managementMaster.getSubjectId());
                if (subjectMaster.isPresent()) {
                    TeacherAssessmentSubjects teacherAssessmentSubjects = new TeacherAssessmentSubjects();
                    teacherAssessmentSubjects.setUserManagementId(managementMaster.getStudentManagementId());
                    teacherAssessmentSubjects.setSubjectId(subjectMaster.get().getSubjectId());
                    teacherAssessmentSubjects.setSubjectName(subjectMaster.get().getSubjectName());
                    teacherAssessmentSubjects.setSubStatus(subjectMaster.get().getStatus());
                    teacherAssessmentStandards.getTeacherAssessmentSubjects().add(teacherAssessmentSubjects);
                }
            }
        }

        userManagementResponse.setTeacherAssessmentEntranceExams(new ArrayList<>(entranceExamsMap.values()));
        logger.info("Student and entrance exam data fetched successfully");
        return userManagementResponse;
    }

    @Override
    public ParentDetails studentWiseParent(Long id) {
        logger.info("Fetching parent for student ID: {}", id);
        User user = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
        ParentDetails parentDetails = this.userRepository.studentWiseParent(user.getId());
        logger.info("Parent details fetched for student ID: {}", id);
        return parentDetails;
    }

    @Override
    public MainResponse acceptParent(Long id, Long acceptby) {
        logger.info("Accepting parent ID: {} by acceptor ID: {}", id, acceptby);
        MainResponse mainResponse = new MainResponse();
        User parent = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Parent not found"));
        User acceptBy = this.userRepository.findById(acceptby).orElseThrow(() -> new RuntimeException("Acceptor not found"));
        User student = this.userRepository.findById(parent.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
        try {
            parent.setStatus("Active");
            parent.setAcceptBy(acceptBy.getId());
            student.setParentStatus("Completed");
            this.userRepository.save(parent);
            this.userRepository.save(student);
            mainResponse.setMessage("Parent Accepted");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
            logger.info("Parent accepted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Error accepting parent {}: {}", id, e.getMessage(), e);
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public MainResponse updateParent(ParentInformationUpdateRequest parentInformationUpdateRequest) {
        logger.info("Updating parent with ID: {}", parentInformationUpdateRequest.getId());
        MainResponse mainResponse = new MainResponse();
        Optional<User> parent = this.userRepository.findById(parentInformationUpdateRequest.getId());
        if (parent.isPresent()) {
            parent.get().setFirstName(parentInformationUpdateRequest.getFirstName());
            parent.get().setLastName(parentInformationUpdateRequest.getLastName());
            parent.get().setMobile(parentInformationUpdateRequest.getMobile());
            parent.get().setEmail(parentInformationUpdateRequest.getEmail());
            parent.get().setColorTheme(parentInformationUpdateRequest.getColorTheme());
            parent.get().setAddress(parentInformationUpdateRequest.getAddress());
            parent.get().setParentStatus("Completed");
            this.userRepository.save(parent.get());
            mainResponse.setMessage("Parent updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
            logger.info("Parent updated successfully: {}", parentInformationUpdateRequest.getId());
        } else {
            logger.warn("Parent not found: {}", parentInformationUpdateRequest.getId());
            mainResponse.setMessage("Parent not found");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public MainResponse bulkParentStatusChange(BulkParentStatusChangeRequest bulkParentStatusChangeRequest) {
        logger.info("Bulk changing parent status for {} parents", bulkParentStatusChangeRequest.getUserIds().size());
        MainResponse mainResponse = new MainResponse();
        Boolean flag = false;

        User acceptedBy = this.userRepository.findById(bulkParentStatusChangeRequest.getAcceptBy()).orElseThrow(() -> new RuntimeException("Acceptor not found"));
        for (Long userId : bulkParentStatusChangeRequest.getUserIds()) {
            User parent = this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Parent not found"));
            User student = this.userRepository.findById(parent.getStudentId()).orElseThrow(() -> new RuntimeException("Student not found"));
            try {
                student.setParentStatus("Completed");
                parent.setStatus(bulkParentStatusChangeRequest.getStatus());
                parent.setAcceptBy(acceptedBy.getId());
                this.userRepository.save(parent);
                this.userRepository.save(student);
                flag = true;
            } catch (Exception e) {
                logger.error("Error updating parent status for {}: {}", userId, e.getMessage(), e);
                flag = false;
            }
        }
        if (Boolean.TRUE.equals(flag)) {
            mainResponse.setMessage("Parent accepted successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
            logger.info("Bulk parent status change completed successfully");
        } else {
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public List<RolesActivitiesResponse> rolesActivities() {
        logger.info("Fetching roles activities");
        List<ERole> roles = Arrays.asList(
                ERole.ROLE_ADMIN,
                ERole.ROLE_TEACHER,
                ERole.ROLE_STUDENT,
                ERole.ROLE_PARENT
        );

        List<User> users = this.userRepository.findUsersByRoles(roles);

        List<RolesActivitiesResponse> responses = users.stream().map(user -> {
            RolesActivitiesResponse response = new RolesActivitiesResponse();

            if ("Active".equals(user.getStatus())) {
                response.setStatus("Online");
            } else {
                response.setStatus("Offline");
            }

            if (!user.getRoles().isEmpty()) {
                ERole userRole = user.getRoles().stream().findFirst().get().getName();
                switch (userRole) {
                    case ROLE_ADMIN:
                        response.setRole("Admin");
                        break;
                    case ROLE_STUDENT:
                        response.setRole("Student");
                        break;
                    case ROLE_PARENT:
                        response.setRole("Parent");
                        break;
                    case ROLE_TEACHER:
                        response.setRole("Teacher");
                        break;
                    default:
                        response.setRole("Unknown");
                        break;
                }
            }

            String fullName = user.getFirstName() + " " + user.getLastName();
            response.setId(user.getId());
            response.setName(fullName);
            response.setEmail(user.getEmail());
            response.setLastActive(user.getLastLogin());

            return response;
        }).collect(Collectors.toList());

        logger.info("Roles activities fetched: {} records", responses.size());
        return responses;
    }

    @Override
    public MainResponse updateStatus(UpdateStatusRequest updateStatusRequest) {
        logger.info("Updating status for user ID: {}", updateStatusRequest.getId());
        MainResponse mainResponse = new MainResponse();
        User user = this.userRepository.findById(updateStatusRequest.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(updateStatusRequest.getStatus());
        try {
            this.userRepository.save(user);
            mainResponse.setMessage("User status changed successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
            logger.info("User status updated for ID: {}", updateStatusRequest.getId());
        } catch (Exception e) {
            logger.error("Error updating status for user {}: {}", updateStatusRequest.getId(), e.getMessage(), e);
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;
    }

    @Override
    public List<UserDetails> allActiveAdmins(String role) {
        logger.info("Fetching all active admins for role: {}", role);
        userRepository.markExpiredUsersInactive();
        ERole roleEnum = ERole.valueOf(role);
        List<UserDetails> roleWiseList = this.userRepository.getAllActiveRoleWiseList(roleEnum);
        Collections.reverse(roleWiseList);
        logger.info("Active admins found: {}", roleWiseList.size());
        return roleWiseList;
    }

    @Override
    public List<InstituteDetailsResponse> getAllActiveInstitutes(String role) {
        logger.info("Fetching all active institutes for role: {}", role);
        ERole roleEnum = ERole.valueOf(role);
        List<InstituteDetailsResponse> roleWiseList = this.userRepository.getAllActiveInstitutes(roleEnum);
        Collections.reverse(roleWiseList);
        logger.info("Active institutes found: {}", roleWiseList.size());
        return roleWiseList;
    }

    @Override
    public InstituteDetailsResponse getInstituteById(Long id) {
        logger.info("Fetching institute by ID: {}", id);
        InstituteDetailsResponse response = this.userRepository.getInstituteById(id);
        logger.info("Institute details fetched for ID: {}", id);
        return response;
    }

    @Override
    @Transactional
    public MainResponse deleteInstitute(Long instituteId) {
        logger.info("Starting deletion process for institute with ID: {}", instituteId);

        try {
            // Fetch the institute user
            Optional<User> instituteOpt = userRepository.findById(instituteId);
            if (!instituteOpt.isPresent()) {
                logger.warn("Institute not found with ID: {}", instituteId);
                return new MainResponse("Institute not found with ID: " + instituteId, 404, false);
            }

            User institute = instituteOpt.get();

            // Verify that this is actually an institute
            boolean isInstitute = institute.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_INSTITUTE);

            if (!isInstitute) {
                logger.warn("User with ID {} is not an institute", instituteId);
                return new MainResponse("User is not an institute", 400, false);
            }

            // Check if it's SUPER_ADMIN (shouldn't happen but just in case)
            boolean isSuperAdmin = institute.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_SUPER_ADMIN);

            if (isSuperAdmin) {
                logger.warn("Attempt to delete SUPER_ADMIN institute with ID: {}", instituteId);
                return new MainResponse("SUPER_ADMIN cannot be deleted", 400, false);
            }

            // Get institute name for additional cleanup
            String instituteName = institute.getInstituteName();

            // 1. Handle all users created by this institute (teachers, students, parents)
            List<User> instituteUsers = userRepository.findByCreatorId(instituteId);
            if (instituteUsers != null && !instituteUsers.isEmpty()) {
                logger.info("Found {} users created by institute {}", instituteUsers.size(), instituteName);

                for (User user : instituteUsers) {
                    try {
                        // Set creatorId to null to remove association
                        user.setCreatorId(null);
                        user.setStatus("InActive");
                        userRepository.save(user);
                        logger.debug("Updated user {} created by institute", user.getId());
                    } catch (Exception e) {
                        logger.error("Error updating user {}: {}", user.getId(), e.getMessage());
                        // Continue with other users even if one fails
                    }
                }
            }

            // 2. Handle the InstituteMaster entry
            if (instituteName != null && !instituteName.isEmpty()) {
                try {
                    InstituteMaster instituteMaster = instituteRepository.findByInstituteNameIgnoreCase(instituteName);
                    if (instituteMaster != null) {
                        logger.info("Deleting InstituteMaster entry for: {}", instituteName);
                        instituteRepository.delete(instituteMaster);
                    }
                } catch (Exception e) {
                    logger.error("Error deleting InstituteMaster: {}", e.getMessage());
                    // Continue with deletion even if InstituteMaster deletion fails
                }
            }

            // 3. Handle user management mappings
            try {
                List<UserManagementMaster> userManagements = userManagementMasterRepository.getAllByTeacherId(instituteId);
                if (userManagements != null && !userManagements.isEmpty()) {
                    logger.info("Deleting {} user management records for institute", userManagements.size());
                    userManagementMasterRepository.deleteAll(userManagements);
                }
            } catch (Exception e) {
                logger.error("Error deleting user management records: {}", e.getMessage());
            }

            // 4. Handle student management mappings
            try {
                List<StudentManagementMaster> studentManagements = studentManagementRepository.getAllByStudentId(instituteId);
                if (studentManagements != null && !studentManagements.isEmpty()) {
                    logger.info("Deleting {} student management records for institute", studentManagements.size());
                    studentManagementRepository.deleteAll(studentManagements);
                }
            } catch (Exception e) {
                logger.error("Error deleting student management records: {}", e.getMessage());
            }

            // 5. Delete all associations using repository methods
            try {
                userRepository.deleteUserRoles(instituteId);
                userRepository.deleteUserSubjects(instituteId);
                userRepository.deleteUserStandards(instituteId);
                userRepository.deleteUserEntranceExams(instituteId);
                userRepository.deleteTeacherStudentMapping(instituteId);
                userRepository.deleteTeacherPatterns(instituteId);
                logger.debug("Deleted all associations for institute {}", instituteId);
            } catch (Exception e) {
                logger.error("Error deleting associations: {}", e.getMessage());
            }

            // 6. Finally, soft delete the institute
            institute.setStatus("Deleted");
            // Append timestamp to institute name to avoid unique constraint violations if you have any
            institute.setInstituteName(institute.getInstituteName() + "_deleted_" + System.currentTimeMillis());
            userRepository.save(institute);

            logger.info("Successfully deleted institute with ID: {}", instituteId);
            return new MainResponse("Institute deleted successfully", 200, true);

        } catch (Exception e) {
            logger.error("Error deleting institute {}: {}", instituteId, e.getMessage(), e);
            return new MainResponse("Failed to delete institute: " + e.getMessage(), 500, false);
        }
    }

    @Override
    public MainResponse updateInstitute(UpdateInstituteRequest updateInstituteRequest) {
        logger.info("Updating institute with ID: {}", updateInstituteRequest.getId());
        MainResponse mainResponse = new MainResponse();
        User institute = this.userRepository.findById(updateInstituteRequest.getId()).orElseThrow(() -> new RuntimeException("Institute not found"));
        try {
            BeanUtils.copyProperties(updateInstituteRequest, institute);
            institute.setExpiryDate(updateInstituteRequest.getExpiryDate());
            this.userRepository.save(institute);
            mainResponse.setMessage("Institute updated successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
            logger.info("Institute updated successfully: {}", updateInstituteRequest.getId());
        } catch (Exception e) {
            logger.error("Error updating institute {}: {}", updateInstituteRequest.getId(), e.getMessage(), e);
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }

        return mainResponse;
    }

    @Override
    public List<InstituteDetailsResponse> getAllInActiveInstitutes() {
        logger.info("Fetching all inactive institutes");
        String role = "ROLE_INSTITUTE";
        String status = "InActive";
        ERole roleEnum = ERole.valueOf(role);
        List<InstituteDetailsResponse> instituteDetailsResponses = this.userRepository.getAllInstitutesByStatus(roleEnum, status);
        Collections.reverse(instituteDetailsResponses);
        logger.info("Inactive institutes found: {}", instituteDetailsResponses.size());
        return instituteDetailsResponses;
    }

    @Override
    public InstituteCountsResponse countsOfInstitutesUsers(Long id) {
        logger.info("Fetching institute counts for ID: {}", id);
        InstituteCountsResponse response = new InstituteCountsResponse();
        Integer totalUsers, totalActiveUsers, totalInActiveUsers;
        Integer totalTeachers = 0, totalActiveTeachers = 0, totalInActiveTeachers = 0;
        Integer totalStudents = 0, totalActiveStudents = 0, totalInActiveStudents = 0;
        Integer totalParents = 0, totalActiveParents = 0, totalInActiveParents = 0;

        ERole role = ERole.ROLE_INSTITUTE;

        totalUsers = this.userRepository.createdUsersCounts(id);
        totalActiveUsers = this.userRepository.totalActiveUserCount(role);
        totalInActiveUsers = this.userRepository.totalInActiveUserCount(role);

        List<Long> usersIdsOfTheInstitute = this.userRepository.totalTeachersOfTheInstitute(id);

        for (Long uId : usersIdsOfTheInstitute) {
            User user = this.userRepository.findById(uId).get();
            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_TEACHER")) {
                totalTeachers += 1;
            }

            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_STUDENT")) {
                totalStudents += 1;
            }

            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_PARENT")) {
                totalParents += 1;
            }

            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_TEACHER") && user.getStatus().equals("Active")) {
                totalActiveTeachers += 1;
            }
            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_TEACHER") && user.getStatus().equals("InActive")) {
                totalInActiveTeachers += 1;
            }

            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_STUDENT") && user.getStatus().equals("Active")) {
                totalActiveStudents += 1;
            }
            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_STUDENT") && user.getStatus().equals("InActive")) {
                totalInActiveStudents += 1;
            }

            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_PARENT") && user.getStatus().equals("Active")) {
                totalActiveParents += 1;
            }
            if (user.getRoles().stream().findFirst().get().getName().name().equals("ROLE_PARENT") && user.getStatus().equals("InActive")) {
                totalInActiveParents += 1;
            }
        }

        response.setTotalUsers(totalUsers);
        response.setTotalActiveUsers(totalActiveUsers);
        response.setTotalInActiveUsers(totalInActiveUsers);
        response.setTotalTeachers(totalTeachers);
        response.setTotalActiveTeachers(totalActiveTeachers);
        response.setTotalInActiveTeachers(totalInActiveTeachers);
        response.setTotalStudents(totalStudents);
        response.setTotalActiveStudents(totalActiveStudents);
        response.setTotalInActiveStudents(totalInActiveStudents);
        response.setTotalParents(totalParents);
        response.setTotalActiveParents(totalActiveParents);
        response.setTotalInActiveParents(totalInActiveParents);

        logger.info("Institute counts fetched for ID: {}", id);
        logger.debug("Total Users: {}, Active: {}, Inactive: {}", totalUsers, totalActiveUsers, totalInActiveUsers);
        return response;
    }

    @Override
    public List<UserDetails> allActiveInstitutes(String role) {
        logger.info("Fetching all active institutes for role: {}", role);
        ERole roleEnum = ERole.valueOf(role);
        List<UserDetails> roleWiseList = this.userRepository.getAllActiveRoleWiseList(roleEnum);
        for (UserDetails userDetails : roleWiseList) {
            User institute = this.userRepository.findById(userDetails.getId()).get();
            userDetails.setFirstName(institute.getInstituteName());
            userDetails.setLastName(null);
        }
        Collections.reverse(roleWiseList);
        logger.info("Active institutes found: {}", roleWiseList.size());
        return roleWiseList;
    }

    @Override
    public List<SubjectMastersResponse> userAndEntranceExamAndStandardIdWiseSubjects(Long id, Integer entranceExamId, Integer standardId) {
        logger.info("Fetching subjects for user ID: {}, entrance exam ID: {}, standard ID: {}", id, entranceExamId, standardId);
        List<SubjectMastersResponse> subjectMastersResponses = new ArrayList<>();
        List<UserManagementMaster> userManagementMaster = this.userRepository.userAndEntranceExamAndStandardIdWiseSubjects(id, entranceExamId, standardId);

        for (UserManagementMaster managementMaster : userManagementMaster) {
            SubjectMaster master = this.subjectRepository.findById(managementMaster.getSubjectId()).get();
            SubjectMastersResponse subjectMastersResponse = new SubjectMastersResponse();
            subjectMastersResponse.setSubjectId(master.getSubjectId());
            subjectMastersResponse.setSubjectName(master.getSubjectName());
            subjectMastersResponses.add(subjectMastersResponse);
        }

        logger.info("Subjects found: {}", subjectMastersResponses.size());
        return subjectMastersResponses;
    }

    @Override
    public Set<StandardMasterResponse> userAndEntranceExamIdWiseStandards(Long id, Integer entranceExamId) {
        logger.info("Fetching standards for user ID: {}, entrance exam ID: {}", id, entranceExamId);
        Set<UserManagementMaster> userManagementMasters = this.userManagementMasterRepository
                .teacherAndEntranceExamIdWiseStandardsSet(id, entranceExamId);

        Set<StandardMasterResponse> responses = userManagementMasters.stream()
                .map(userManagementMaster -> standardRepository.findById(userManagementMaster.getStandardId()).orElse(null))
                .filter(standardMaster -> standardMaster != null && "ACTIVE".equalsIgnoreCase(standardMaster.getStatus()))
                .map(standardMaster -> {
                    StandardMasterResponse response = new StandardMasterResponse();
                    response.setStandardId(standardMaster.getStandardId());
                    response.setStandardName(standardMaster.getStandardName());
                    return response;
                })
                .collect(Collectors.toSet());

        logger.info("Standards found: {}", responses.size());
        return responses;
    }

    @Override
    public Set<EntranceExamResponse> teacherWiseEntranceExams(Long id) {
        logger.info("Fetching entrance exams for teacher ID: {}", id);
        Set<EntranceExamResponse> entranceExamResponses = new HashSet<>();
        Set<UserManagementMaster> userManagementMasters = this.userManagementMasterRepository.teacherWiseEntranceExams(id);

        for (UserManagementMaster userManagementMaster : userManagementMasters) {
            EntranceExamResponse entranceExamResponse = new EntranceExamResponse();
            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(userManagementMaster.getEntranceExamId()).get();
            entranceExamResponse.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            entranceExamResponse.setEntranceExamName(entranceExamMaster.getEntranceExamName());
            entranceExamResponse.setImage(entranceExamMaster.getImage());
            entranceExamResponse.setStatus(entranceExamMaster.getStatus());
            entranceExamResponses.add(entranceExamResponse);
        }

        logger.info("Entrance exams found for teacher {}: {}", id, entranceExamResponses.size());
        return entranceExamResponses;
    }

    @Override
    public Set<EntranceExamResponse> studentEntranceExams(Long id) {
        logger.info("Fetching entrance exams for student ID: {}", id);
        Set<EntranceExamResponse> entranceExamResponses = new HashSet<>();
        Set<StudentManagementMaster> studentManagementMasters = this.studentManagementRepository.studentEntranceExams(id);

        for (StudentManagementMaster studentManagementMaster : studentManagementMasters) {
            EntranceExamResponse entranceExamResponse = new EntranceExamResponse();
            EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(studentManagementMaster.getEntranceExamId()).get();
            entranceExamResponse.setEntranceExamId(entranceExamMaster.getEntranceExamId());
            entranceExamResponse.setEntranceExamName(entranceExamMaster.getEntranceExamName());
            entranceExamResponse.setImage(entranceExamMaster.getImage());
            entranceExamResponse.setStatus(entranceExamMaster.getStatus());
            entranceExamResponses.add(entranceExamResponse);
        }

        logger.info("Entrance exams found for student {}: {}", id, entranceExamResponses.size());
        return entranceExamResponses;
    }

    @Override
    public List<StandardSubjectResponse> studentData(Integer entranceExamId, Long id) {
        logger.info("Fetching student data for student ID: {}, entrance exam ID: {}", id, entranceExamId);
        List<StandardSubjectResponse> standardResponses = new ArrayList<>();

        User student = this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        Set<Integer> studentStandardIds =
                this.studentManagementRepository.getStudentIdAndEntranceExamIdWiseStandard(
                        student.getId(),
                        entranceExamId
                );

        EntranceExamMaster entranceExamMaster = this.entranceExamRepository.findById(entranceExamId)
                .orElseThrow(() -> new RuntimeException("Entrance Exam not found with id: " + entranceExamId));

        for (Integer standardId : studentStandardIds) {
            StandardMaster standardMaster = this.standardRepository.findById(standardId)
                    .orElseThrow(() -> new RuntimeException("Standard not found with id: " + standardId));

            StandardSubjectResponse standardResponse = new StandardSubjectResponse();
            standardResponse.setId(standardMaster.getStandardId());
            standardResponse.setName(standardMaster.getStandardName());

            List<StudentDataResponse> subjectResponses = new ArrayList<>();

            Set<Integer> subjectIds =
                    this.studentManagementRepository.getSubjectsByStudentIdEntranceExamIdStandardId(
                            student.getId(),
                            entranceExamMaster.getEntranceExamId(),
                            standardMaster.getStandardId()
                    );

            for (Integer subjectId : subjectIds) {
                SubjectMaster subjectMaster = this.subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

                StudentDataResponse subjectResponse = new StudentDataResponse();
                subjectResponse.setId(subjectMaster.getSubjectId());
                subjectResponse.setName(subjectMaster.getSubjectName());

                List<StudentChaptersResponse> chapterResponses = new ArrayList<>();

                List<Integer> chapterIds = this.chapterRepository.getChaptersByEntranceStandardSubject(
                        entranceExamMaster.getEntranceExamId(),
                        standardMaster.getStandardId(),
                        subjectMaster.getSubjectId()
                );

                for (Integer chapterId : chapterIds) {
                    ChapterMaster chapterMaster = this.chapterRepository.findById(chapterId)
                            .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + chapterId));

                    StudentChaptersResponse chapterResponse = new StudentChaptersResponse();
                    chapterResponse.setId(chapterMaster.getChapterId());
                    chapterResponse.setName(chapterMaster.getChapterName());

                    List<Integer> topicCount =
                            this.topicRepository.getTopicCountByEntranceStandardSubjectChapter(
                                    entranceExamMaster.getEntranceExamId(),
                                    standardMaster.getStandardId(),
                                    subjectMaster.getSubjectId(),
                                    chapterMaster.getChapterId()
                            );

                    chapterResponse.setTopicCount(topicCount.size());

                    Long chapterQuestionCount =
                            this.questionRepository.countQuestionsByChapter(chapterMaster.getChapterId());
                    chapterResponse.setQuestionCount(chapterQuestionCount);

                    List<StudentTopicResponse> topicResponses = new ArrayList<>();

                    List<Integer> topicIds =
                            this.topicRepository.getTopicsByEntranceStandardSubjectChapterWise(
                                    entranceExamMaster.getEntranceExamId(),
                                    standardMaster.getStandardId(),
                                    subjectMaster.getSubjectId(),
                                    chapterMaster.getChapterId()
                            );

                    for (Integer topicId : topicIds) {
                        TopicMaster topicMaster = this.topicRepository.findById(topicId)
                                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + topicId));

                        StudentTopicResponse topicResponse = new StudentTopicResponse();
                        topicResponse.setId(topicMaster.getTopicId());
                        topicResponse.setName(topicMaster.getTopicName());

                        List<Integer> questionCount =
                                this.questionRepository.getCountOfQuestionsByEntranceStandardSubjectChapterTopic(
                                        entranceExamMaster.getEntranceExamId(),
                                        standardMaster.getStandardId(),
                                        subjectMaster.getSubjectId(),
                                        chapterMaster.getChapterId(),
                                        topicMaster.getTopicId()
                                );

                        topicResponse.setQuestionCount(questionCount.size());
                        topicResponses.add(topicResponse);
                    }

                    chapterResponse.setTopics(topicResponses);
                    chapterResponses.add(chapterResponse);
                }

                subjectResponse.setChapters(chapterResponses);
                subjectResponses.add(subjectResponse);
            }

//            standardResponse.setSubjects(subjectResponses);
//            standardResponses.add(standardResponse);
        }

        logger.info("Student data fetched for ID {}: {} standards found", id, standardResponses.size());
        return standardResponses;
    }

    @Override
    @Transactional
    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        logger.info("Registering new user with email: {}", signUpRequest.getEmail());
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Email already in use: {}", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        if (userRepository.existsByMobile(signUpRequest.getMobile())) {
            logger.warn("Mobile number already in use: {}", signUpRequest.getMobile());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Mobile number is already in use!"));
        }

        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            logger.warn("Passwords do not match for email: {}", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Passwords do not match"));
        }

        User user = new User();

        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getEmail());
        user.setMobile(signUpRequest.getMobile());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setConfirmPassword(user.getPassword());
        user.setStatus("pending");  // OmkarP Change this line for bulk processing (pending status)

        user.setDate(new Date());
        user.setExpiryDate(signUpRequest.getExpiryDate());
        user.setColorTheme(signUpRequest.getColorTheme());
        user.setLogoImage(signUpRequest.getLogoImage());
        user.setWatermarkImage(signUpRequest.getWatermarkImage());
        user.setProfilePicture(signUpRequest.getProfilePicture());
        user.setAddress(signUpRequest.getAddress());
        user.setPrintAccess(signUpRequest.getPrintAccess());
        user.setOrmSheetAccess(signUpRequest.getOrmSheetAccess());
        user.setEditAccess(signUpRequest.getEditAccess());
        user.setStudentKeys(signUpRequest.getStudentKeys());
        user.setTeacherKeys(signUpRequest.getTeacherKeys());
        user.setInstituteName(signUpRequest.getInstituteName());
        user.setSlogan(signUpRequest.getSlogan());
        user.setParentStatus(signUpRequest.getParentStatus());
        user.setCreatorId(signUpRequest.getCreatorId());

        Set<Role> roles = new HashSet<>();
        String strRole = signUpRequest.getRole();

        if ("super_admin".equalsIgnoreCase(strRole)) {
            Role superAdminRole = roleRepository
                    .findByName(ERole.ROLE_SUPER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: SUPER_ADMIN role not found"));

            roles.clear();
            roles.add(superAdminRole);
            user.setCreatorId(null);
            user.setInstituteName(null);
            user.setStatus("Active");  // Only super_admin gets Active
        } else if ("admin".equalsIgnoreCase(strRole)) {
            Role adminRole = roleRepository
                    .findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: ADMIN role not found"));

            roles.add(adminRole);
            // ⭐ REMOVED: user.setStatus("Pending"); (Already set above)
        } else if ("institute".equalsIgnoreCase(strRole)) {
            Role instituteRole = roleRepository
                    .findByName(ERole.ROLE_INSTITUTE)
                    .orElseThrow(() -> new RuntimeException("Error: INSTITUTE role not found"));

            roles.add(instituteRole);
            // ⭐ REMOVED: user.setStatus("Pending"); (Already set above)
        } else if ("teacher".equalsIgnoreCase(strRole)) {
            Role teacherRole = roleRepository
                    .findByName(ERole.ROLE_TEACHER)
                    .orElseThrow(() -> new RuntimeException("Error: TEACHER role not found"));

            roles.add(teacherRole);
            // ⭐ REMOVED: user.setStatus("Pending"); (Already set above)
        } else if ("student".equalsIgnoreCase(strRole)) {
            Role studentRole = roleRepository
                    .findByName(ERole.ROLE_STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: STUDENT role not found"));

            roles.add(studentRole);
            // ⭐ REMOVED: user.setStatus("Pending"); (Already set above)
        } else if ("parent".equalsIgnoreCase(strRole)) {
            Role parentRole = roleRepository
                    .findByName(ERole.ROLE_PARENT)
                    .orElseThrow(() -> new RuntimeException("Error: PARENT role not found"));

            roles.add(parentRole);
            // ⭐ REMOVED: user.setStatus("Pending"); (Already set above)
        } else {
            logger.error("Invalid role provided: {}", strRole);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid role"));
        }

        if (roles.size() != 1) {
            throw new RuntimeException("User must have exactly ONE primary role");
        }

        user.setRoles(roles);
        userRepository.save(user);

        logger.info("User registered successfully: {}", signUpRequest.getEmail());
        return ResponseEntity.ok(
                new MessageResponse("User registered successfully!")
        );
    }

    @Override
    @Transactional
    public MainResponse deleteAdmin(Long adminId) {
        logger.info("Starting deletion process for admin with ID: {}", adminId);

        try {
            // Fetch the admin user
            Optional<User> adminOpt = userRepository.findById(adminId);
            if (!adminOpt.isPresent()) {
                logger.warn("Admin not found with ID: {}", adminId);
                return new MainResponse("Admin not found with ID: " + adminId, 404, false);
            }

            User admin = adminOpt.get();

            // Verify that this is actually an admin
            boolean isAdmin = admin.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);

            if (!isAdmin) {
                logger.warn("User with ID {} is not an admin", adminId);
                return new MainResponse("User is not an admin", 400, false);
            }

            // Check if it's SUPER_ADMIN (can't delete super admin)
            boolean isSuperAdmin = admin.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_SUPER_ADMIN);

            if (isSuperAdmin) {
                logger.warn("Attempt to delete SUPER_ADMIN with ID: {}", adminId);
                return new MainResponse("SUPER_ADMIN cannot be deleted", 400, false);
            }

            // 1. Handle all users created by this admin
            List<User> createdUsers = userRepository.findByCreatorId(adminId);
            if (createdUsers != null && !createdUsers.isEmpty()) {
                logger.info("Found {} users created by admin {}", createdUsers.size(), adminId);

                for (User user : createdUsers) {
                    try {
                        user.setCreatorId(null);
                        user.setStatus("InActive");
                        userRepository.save(user);
                        logger.debug("Updated user {} created by admin", user.getId());
                    } catch (Exception e) {
                        logger.error("Error updating user {}: {}", user.getId(), e.getMessage());
                    }
                }
            }

            // 2. Handle user management mappings
            try {
                List<UserManagementMaster> userManagements = userManagementMasterRepository.getAllByTeacherId(adminId);
                if (userManagements != null && !userManagements.isEmpty()) {
                    logger.info("Deleting {} user management records for admin", userManagements.size());
                    userManagementMasterRepository.deleteAll(userManagements);
                }
            } catch (Exception e) {
                logger.error("Error deleting user management records: {}", e.getMessage());
            }

            // 3. Handle student management mappings
            try {
                List<StudentManagementMaster> studentManagements = studentManagementRepository.getAllByStudentId(adminId);
                if (studentManagements != null && !studentManagements.isEmpty()) {
                    logger.info("Deleting {} student management records for admin", studentManagements.size());
                    studentManagementRepository.deleteAll(studentManagements);
                }
            } catch (Exception e) {
                logger.error("Error deleting student management records: {}", e.getMessage());
            }

            // 4. Handle teacher-student mappings - Using existing repository method
            try {
                List<Long> studentIds = userRepository.findStudentsByTeacherId(adminId);
                if (studentIds != null && !studentIds.isEmpty()) {
                    logger.info("Found {} students with teacher ID {}", studentIds.size(), adminId);

                    for (Long studentId : studentIds) {
                        try {
                            Optional<User> studentOpt = userRepository.findById(studentId);
                            if (studentOpt.isPresent()) {
                                User student = studentOpt.get();
                                student.getTeacher().removeIf(teacher -> teacher.getId().equals(adminId));
                                userRepository.save(student);
                            }
                        } catch (Exception e) {
                            logger.error("Error updating student {}: {}", studentId, e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error handling teacher-student mappings: {}", e.getMessage());
            }

            // 5. Delete all associations using repository methods
            try {
                userRepository.deleteUserRoles(adminId);
                userRepository.deleteUserSubjects(adminId);
                userRepository.deleteUserStandards(adminId);
                userRepository.deleteUserEntranceExams(adminId);
                userRepository.deleteTeacherStudentMapping(adminId);
                userRepository.deleteTeacherPatterns(adminId);
                logger.debug("Deleted all associations for admin {}", adminId);
            } catch (Exception e) {
                logger.error("Error deleting associations: {}", e.getMessage());
            }

            // 6. Soft delete the admin
            admin.setStatus("Deleted");
            if (admin.getEmail() != null) {
                admin.setEmail(admin.getEmail() + "_deleted_" + System.currentTimeMillis());
            }
            if (admin.getMobile() != null) {
                admin.setMobile(admin.getMobile() + "_deleted");
            }
            userRepository.save(admin);

            logger.info("Successfully deleted admin with ID: {}", adminId);
            return new MainResponse("Admin deleted successfully", 200, true);

        } catch (Exception e) {
            logger.error("Error deleting admin {}: {}", adminId, e.getMessage(), e);
            return new MainResponse("Failed to delete admin: " + e.getMessage(), 500, false);
        }
    }

    private boolean canAddNewRole(User existingUser, ERole newRole) {
        if (existingUser.getRoles() == null || existingUser.getRoles().isEmpty()) {
            return true;
        }

        String newRoleName = newRole.name();
        boolean alreadyHasNewRole = existingUser.getRoles().stream()
                .anyMatch(r -> r.getName().name().equalsIgnoreCase(newRoleName));
        if (alreadyHasNewRole) {
            return false;
        }

        for (Role existingRole : existingUser.getRoles()) {
            String existing = existingRole.getName().name();

            if ("ROLE_SUPER_ADMIN".equals(existing)) {
                return true;
            } else if ("ROLE_ADMIN".equals(existing)) {
                return false;
            } else if ("ROLE_INSTITUTE".equals(existing)) {
                if (!"ROLE_TEACHER".equals(newRoleName)) {
                    return false;
                }
            } else if ("ROLE_TEACHER".equals(existing)) {
                return false;
            } else if ("ROLE_STUDENT".equals(existing) || "ROLE_PARENT".equals(existing)) {
                return false;
            }
        }

        return true;
    }


    @Override
    public List<String> processBulkSignup1(MultipartFile file, Long creatorId) {
        logger.info("Processing bulk signup from file for creator ID: {}", creatorId);
        List<String> results = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 1) {
                results.add("Error: No data in file.");
                return results;
            }

            List<SignupRequest> signupRequests = parseSheet(sheet, creatorId);
            if (signupRequests.isEmpty()) {
                results.add("Error: No valid data in file.");
                return results;
            }

            // FIX 1: Check for duplicates
            List<String> emailsList = signupRequests.stream()
                    .map(r -> r.getEmail() == null ? null : r.getEmail().trim().toLowerCase())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<String> mobilesList = signupRequests.stream()
                    .map(r -> r.getMobile() == null ? null : r.getMobile().trim())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<Object[]> rawExisting = userRepository.findExistingEmailsAndMobiles(emailsList, mobilesList);
            Set<String> existingSet = new HashSet<>();
            for (Object[] row : rawExisting) {
                if (row[0] != null) existingSet.add(row[0].toString().trim().toLowerCase());
                if (row[1] != null) existingSet.add(row[1].toString().trim());
            }

            Set<String> localEmails = new HashSet<>();
            Set<String> localMobiles = new HashSet<>();
            List<User> usersToSave = new ArrayList<>();
            int savedCount = 0;

            for (SignupRequest req : signupRequests) {
                String email = req.getEmail() == null ? null : req.getEmail().trim().toLowerCase();
                String mobile = req.getMobile() == null ? null : req.getMobile().trim();

                // Skip duplicates
                if (email != null && (existingSet.contains(email) || localEmails.contains(email))) {
                    results.add("Duplicate Email -> " + email);
                    continue;
                }
                if (mobile != null && (existingSet.contains(mobile) || localMobiles.contains(mobile))) {
                    results.add("Duplicate Mobile -> " + mobile);
                    continue;
                }

                if (email != null) localEmails.add(email);
                if (mobile != null) localMobiles.add(mobile);

                //  FIX 2: Force status to "pending" for admins
                if ("admin".equalsIgnoreCase(req.getRole())) {
                    req.setStatus("pending");
                }

                User user = createUserFromRequest(req);
                System.out.println(" CREATING USER: " + user.getEmail());
                //  FIX 3: Validate user before saving
                if (user.getRoles() == null || user.getRoles().isEmpty()) {
                    results.add("Error: No role assigned for " + email);
                    continue;
                }

                //  FIX 4: Save ONE BY ONE to catch individual errors
                try {
                    userRepository.save(user);
                    System.out.println(" SAVED USER: " + user.getEmail() + " | Status: " + user.getStatus());
                    savedCount++;

                    // Send email in background
                    EMAIL_EXECUTOR.submit(() -> {
                        try {
                            sendEmail(user, req.getPassword());
                        } catch (Exception e) {
                            logger.error("Email failed for: {}", user.getEmail(), e);
                        }
                    });

                    //  DEBUG LOG
                    logger.info(" SAVED - Email: {}, Status: {}, Role: {}",
                            user.getEmail(),
                            user.getStatus(),
                            user.getRoles().stream()
                                    .map(r -> r.getName().name())
                                    .collect(Collectors.joining(", "))
                    );

                } catch (Exception e) {
                    logger.error(" FAILED to save user {}: {}", email, e.getMessage());
                    results.add("Failed to save: " + email + " - " + e.getMessage());
                }
            }

            //  Final result
            if (savedCount > 0) {
                results.add("Success: Saved " + savedCount + " users. They will appear in pending list.");
            } else {
                results.add("Warning: No users were saved. Check duplicates or errors above.");
                System.out.println(" BATCH SAVE FAILED!");
            }

        } catch (Exception e) {
            logger.error("Error processing file:", e);
            results.add("Error: " + e.getMessage());
        }

        return results;
    }

    private List<SignupRequest> parseSheet(Sheet sheet, Long creatorId) {
        List<SignupRequest> requests = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            SignupRequest req = mapRowToSignupRequest(row, creatorId, formatter);
            if (req != null) {
                requests.add(req);
            }
        }
        return requests;
    }

    private SignupRequest mapRowToSignupRequest(Row row, Long creatorId, DataFormatter formatter) {
        SignupRequest signupRequest = new SignupRequest();

        signupRequest.setFirstName(       getCellValueAsString(row.getCell(0),  formatter));
        signupRequest.setLastName(        getCellValueAsString(row.getCell(1),  formatter));
        signupRequest.setMobile(          getCellValueAsString(row.getCell(2),  formatter));
        String rawEmail  = getCellValueAsString(row.getCell(3), formatter);
        if (rawEmail != null) {
            rawEmail = rawEmail.trim().toLowerCase();
        }
        signupRequest.setEmail(rawEmail);
        signupRequest.setAddress(         getCellValueAsString(row.getCell(4),  formatter));
        signupRequest.setProfilePicture(  getCellValueAsString(row.getCell(5), formatter));
        signupRequest.setRole(            getCellValueAsString(row.getCell(6), formatter));

        signupRequest.setPassword(        getCellValueAsString(row.getCell(7),  formatter));
        signupRequest.setConfirmPassword( getCellValueAsString(row.getCell(8),  formatter));

        // 🔥 FIX: Check if role is admin, then set status to "pending"
        String role = getCellValueAsString(row.getCell(6), formatter);
        if (role != null && role.equalsIgnoreCase("admin")) {
            signupRequest.setStatus("pending");
            System.out.println("FORCED status to 'pending' for: " + signupRequest.getEmail());// Always "pending" for admins
        } else {
            signupRequest.setStatus(getCellValueAsString(row.getCell(9), formatter));
        }

        signupRequest.setExpiryDate(  getCellValueAsDate(row.getCell(10),formatter));
        signupRequest.setColorTheme(      getCellValueAsString(row.getCell(11),  formatter));
        signupRequest.setClassName(       getCellValueAsString(row.getCell(12),  formatter));
        signupRequest.setStudentKeys(     getCellValueAsInteger(row.getCell(13), formatter));
        signupRequest.setCreatorId(creatorId);
        signupRequest.setLogoImage(       getCellValueAsString(row.getCell(14), formatter));
        signupRequest.setWatermarkImage(  getCellValueAsString(row.getCell(15), formatter));
        signupRequest.setParentStatus(       getCellValueAsString(row.getCell(16), formatter));
        signupRequest.setTeacherId(     getCellValueAsLong(row.getCell(17),formatter));
        signupRequest.setPrintAccess( getCellValueAsBoolean(row.getCell(18),formatter));
        signupRequest.setOrmSheetAccess(  getCellValueAsBoolean(row.getCell(19),formatter));
        signupRequest.setEditAccess( getCellValueAsBoolean(row.getCell(20),formatter));

        return signupRequest;
    }

    private String getCellValueAsString(Cell cell, DataFormatter formatter) {
        if (cell == null) return null;
        String val = formatter.formatCellValue(cell).trim();
        return val.isEmpty() ? null : val;
    }

    private Integer getCellValueAsInteger(Cell cell, DataFormatter formatter) {
        String val = getCellValueAsString(cell, formatter);
        if (val == null) return null;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long getCellValueAsLong(Cell cell, DataFormatter formatter) {
        String val = getCellValueAsString(cell, formatter);
        if (val == null) return null;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean getCellValueAsBoolean(Cell cell, DataFormatter formatter) {
        String val = getCellValueAsString(cell, formatter);
        if (val == null) return null;
        val = val.toLowerCase();
        return ("true".equals(val) || "yes".equals(val) || "1".equals(val));
    }

    public User createUserFromRequest(SignupRequest signupRequest) {
        User user = new User();
        BeanUtils.copyProperties(signupRequest, user);
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setRoles(getRolesForUser(signupRequest.getRole()));
        user.setDate(new Date());

        // FIX: Don't overwrite status - keep what's in signupRequest
        // If status is null, then set to "pending"
        if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
            user.setStatus("pending");
        }

        return user;
    }

    private Set<Role> getRolesForUser(String roleName) {
        if (roleName == null) {
            throw new RuntimeException("Error: roleName is null");
        }
        String formattedRole = "ROLE_" + roleName.toUpperCase();
        Role role = roleCache.computeIfAbsent(formattedRole, r -> roleRepository.findByName(ERole.valueOf(r))
                .orElseThrow(() -> new RuntimeException("Error: Role " + r + " not found.")));
        return Collections.singleton(role);
    }

    private void sendEmail(User user, String originalPassword) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(user.getEmail());
            helper.setFrom("zplushrms@gmail.com");
            helper.setSubject("Account Registration Complete");
            helper.setSentDate(new Date());

            String roleName = user.getRoles().iterator().next().getName().name().replace("ROLE_", "");
            String formattedRole = roleName.substring(0, 1).toUpperCase() + roleName.substring(1).toLowerCase();

            String baseUrl = "http://elitecodo.zplusglobalmarketinsights.com/auth/";
            String loginLink = baseUrl + formattedRole + "-Login";

            String emailContent = "<html><body>"
                    + "<p>Dear " + user.getFirstName() + ",</p>"
                    + "<p>Your registration is successful.</p>"
                    + "<p><strong>Username:</strong> " + user.getEmail() + " or " + user.getMobile() + "</p>"
                    + "<p><strong>Password:</strong> " + originalPassword + "</p>"
                    + "<p>Click <a href=\"" + loginLink + "\">here</a> to log in.</p>"
                    + "<p>Thank you!</p>"
                    + "</body></html>";

            helper.setText(emailContent, true);
            mailSender.send(mimeMessage);
            logger.info("Registration email sent to: {}", user.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", user.getEmail(), e);
        }
    }

    private Date getCellValueAsDate(Cell cell, DataFormatter formatter) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                String dateStr = formatter.formatCellValue(cell).trim();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void deleteAllByIds(List<Long> userIds) {
        logger.info("Deleting {} users by IDs", userIds.size());
        userRepository.deleteAllById(userIds);
        logger.info("Users deleted successfully");
    }

    @Override
    public List<UserDetails> getAllStudentByTeachId(Long id) {
        logger.info("Fetching all students by teacher ID: {}", id);
        List<UserDetails> students = userRepository.getAllUsersByTeacherId(id);
        logger.info("Found {} students for teacher ID: {}", students.size(), id);
        return students;
    }

    @Override
    public UserInfo getInfoById(Long id) {
        logger.info("Fetching user info for ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());

        userInfo.setName(
                (user.getFirstName() != null ? user.getFirstName() : "") +
                        " " +
                        (user.getLastName() != null ? user.getLastName() : "")
        );

        userInfo.setEmail(user.getEmail() != null ? user.getEmail() : "N/A");

        String rolesString = (user.getRoles() != null && !user.getRoles().isEmpty())
                ? user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(", "))
                : "No Role Assigned";

        userInfo.setUserType(rolesString);
        userInfo.setRegistrationDate(user.getDate().toString());
        userInfo.setContactNumber(user.getMobile() != null ? user.getMobile() : "XXXXXXXXXX");
        userInfo.setStatus(user.getStatus() != null ? user.getStatus() : "Pending");
        userInfo.setAvatarUrl(user.getProfilePicture() != null ? user.getProfilePicture() : "Not-Found.png");

        logger.info("User info fetched for ID: {}", id);
        return userInfo;
    }

    @Override
    public List<StandardResponse1> findSubjectsAssignedToStudentId(Long id) {
        logger.info("Finding subjects assigned to student ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
        Set<StandardMaster> stds = user.getStandardMasters();
        List<StandardResponse1> responses = new ArrayList<>();
        Iterator<StandardMaster> itr = stds.iterator();
        while (itr.hasNext()) {
            StandardMaster standardMaster = itr.next();
            StandardResponse1 std = new StandardResponse1();
            std.setId(standardMaster.getStandardId());
            std.setName(standardMaster.getStandardName());
            responses.add(std);
        }

        logger.info("Found {} subjects for student ID: {}", responses.size(), id);
        return responses;
    }

    @Override
    public List<TestNameResponse> getAllTestByStudent(Long id) {
        logger.info("Fetching all tests for student ID: {}", id);
        List<TestSubmission> submissions = submissionRepository.findByUserId(id);
        if (submissions == null || submissions.isEmpty()) {
            logger.warn("No tests found for student ID: {}", id);
            return Collections.emptyList();
        }

        List<TestNameResponse> responses = submissions.stream()
                .filter(Objects::nonNull)
                .map(submission -> {
                    TestNameResponse response = new TestNameResponse();
                    response.setId(submission.getTest().getTestId());

                    if (submission.getTest() != null) {
                        response.setName(submission.getTest().getTestName());
                    } else {
                        response.setName("Unknown Test");
                    }

                    if (submission.getSubmittedAt() != null) {
                        response.setCreatedDate(Date.from(
                                submission.getSubmittedAt().atZone(ZoneId.systemDefault()).toInstant()));
                    } else {
                        response.setCreatedDate(new Date());
                    }

                    return response;
                })
                .collect(Collectors.toList());

        logger.info("Found {} tests for student ID: {}", responses.size(), id);
        return responses;
    }

    @Override
    public TeacherDashboardCountResponse getTeacherDashboardCounts(Long teacherId) {
        logger.info("Fetching dashboard counts for teacher ID: {}", teacherId);
        User teacher = userRepository.findById(teacherId).get();
        long studentCount = userRepository.countStudentsByTeacher(teacher);
        long parentCount = userRepository.countParentsByTeacher(teacherId);
        long questionCount = questionRepository.countByUserId(teacherId);
        long testCount = testRepository.countByCreatedById(teacherId);

        TeacherDashboardCountResponse response = new TeacherDashboardCountResponse(studentCount, parentCount,
                questionCount, testCount);

        logger.info("Dashboard counts for teacher {} - Students: {}, Parents: {}, Questions: {}, Tests: {}",
                teacherId, studentCount, parentCount, questionCount, testCount);
        return response;
    }

    @Override
    public List<ProfilePatternResponse> allTeachersByCreatorId(Long id) {
        logger.info("Fetching all teachers by creator ID: {}", id);
        List<ProfileResponse> allTeachers = this.userRepository.allTeachersByCreatorId(id);
        List<ProfilePatternResponse> responses = new ArrayList<>();

        for (ProfileResponse teacher : allTeachers) {
            teacher.setQuestionCount(this.questionRepository.teacherWiseQuestionCount(teacher.getId()));
            teacher.setPendingQuestionCount(this.questionRepository.teacherWisePendingQuestionCount(teacher.getId()));
            teacher.setAcceptedQuestionCount(this.questionRepository.teacherWiseAcceptedQuestionCount(teacher.getId()));
            teacher.setRejectedQuestionCount(this.questionRepository.teacherWiseRejectedQuestionCount(teacher.getId()));
            teacher.setPrintAccess(teacher.getPrintAccess());
            teacher.setOrmSheetAccess(teacher.getOrmSheetAccess());

            User userEntity = this.userRepository.findById(teacher.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Set<PatternMaster> assignedPatterns = userEntity.getAssignedPatterns();
            Set<PatternMasterResponse> patternMasterResponses = new HashSet<>();
            for (PatternMaster pattern : assignedPatterns) {
                PatternMasterResponse pmr = new PatternMasterResponse();
                BeanUtils.copyProperties(pattern, pmr);
                patternMasterResponses.add(pmr);
            }

            ProfilePatternResponse profilePatternResponse = new ProfilePatternResponse();
            BeanUtils.copyProperties(teacher, profilePatternResponse);
            profilePatternResponse.setAssignedPatterns(patternMasterResponses);
            responses.add(profilePatternResponse);
        }

        logger.info("Found {} teachers by creator ID: {}", responses.size(), id);
        return responses;
    }

    @Override
    public List<UserDetails> allStudentsByCreatorId(Long id) {
        logger.info("Fetching all students by creator ID: {}", id);
        List<UserDetails> allStudents = this.userRepository.allStudentsByCreatorId(id);
        allStudents = allStudents.stream().filter(e -> !e.getStatus().equalsIgnoreCase("Deleted")).collect(Collectors.toList());
        logger.info("Found {} students by creator ID: {}", allStudents.size(), id);
        return allStudents;
    }

    @Override
    public List<InstituteDetailsResponse> getAllActiveInstitutesById(String role, Long id) {
        logger.info("Fetching active institutes by creator ID: {} for role: {}", id, role);
        ERole roleEnum = ERole.valueOf(role);
        List<InstituteDetailsResponse> roleWiseList = this.userRepository.getAllActiveInstitutesById(roleEnum, id);
        Collections.reverse(roleWiseList);
        logger.info("Found {} active institutes by creator ID: {}", roleWiseList.size(), id);
        return roleWiseList;
    }

    @Override
    public List<UserStatResponse> getUserStats(Long adminId) {
        logger.info("Fetching user stats for admin ID: {}", adminId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin user not found: " + adminId));

        Set<Long> userIds = hierarchyService.findAllDescendants(adminId);
        List<UserStatResponse> results = new ArrayList<>();

        long totalTeachers = userRepository.countByIdsAndRole(userIds, ERole.ROLE_TEACHER);
        long activeTeachers = userRepository.countByIdsAndRoleAndStatus(userIds, ERole.ROLE_TEACHER, "Active");
        long inactiveTeachers = totalTeachers - activeTeachers;

        UserStatResponse teacherStats = new UserStatResponse();
        teacherStats.setTitle("Total Teachers");
        teacherStats.setBgClass("gradient-blue");
        teacherStats.setIcon("fas fa-users");
        teacherStats.setTotal(totalTeachers);
        teacherStats.setActive(activeTeachers);
        teacherStats.setInactive(inactiveTeachers);
        teacherStats.setTrend(0.0);
        results.add(teacherStats);

        long totalStudents = userRepository.countByIdsAndRole(userIds, ERole.ROLE_STUDENT);
        long activeStudents = userRepository.countByIdsAndRoleAndStatus(userIds, ERole.ROLE_STUDENT, "Active");
        long inactiveStudents = totalStudents - activeStudents;

        UserStatResponse studentStats = new UserStatResponse();
        studentStats.setTitle("Total Students");
        studentStats.setBgClass("gradient-purple");
        studentStats.setIcon("fas fa-user-graduate");
        studentStats.setTotal(totalStudents);
        studentStats.setActive(activeStudents);
        studentStats.setInactive(inactiveStudents);
        studentStats.setTrend(0.0);
        results.add(studentStats);

        long totalParents = userRepository.countByIdsAndRole(userIds, ERole.ROLE_PARENT);
        long activeParents = userRepository.countByIdsAndRoleAndStatus(userIds, ERole.ROLE_PARENT, "Active");
        long inactiveParents = totalParents - activeParents;

        UserStatResponse parentStats = new UserStatResponse();
        parentStats.setTitle("Total Parents");
        parentStats.setBgClass("gradient-green");
        parentStats.setIcon("fas fa-chalkboard-teacher");
        parentStats.setTotal(totalParents);
        parentStats.setActive(activeParents);
        parentStats.setInactive(inactiveParents);
        parentStats.setTrend(0.0);
        results.add(parentStats);

        long totalInstitutions = userRepository.countByIdsAndRole(userIds, ERole.ROLE_INSTITUTE);
        long activeInstitutions = userRepository.countByIdsAndRoleAndStatus(userIds, ERole.ROLE_INSTITUTE, "Active");
        long inactiveInstitutions = totalInstitutions - activeInstitutions;

        UserStatResponse instituteStats = new UserStatResponse();
        instituteStats.setTitle("Total Institutions");
        instituteStats.setBgClass("gradient-orange");
        instituteStats.setIcon("fas fa-school");
        instituteStats.setTotal(totalInstitutions);
        instituteStats.setActive(activeInstitutions);
        instituteStats.setInactive(inactiveInstitutions);
        instituteStats.setTrend(0.0);
        results.add(instituteStats);

        logger.info("User stats fetched for admin ID: {}", adminId);
        return results;
    }

    @Override
    public List<ExpiringUserResponse> getUsersExpiringInNextDays(int days, Long creatorid) {
        logger.info("Fetching users expiring in next {} days for creator ID: {}", days, creatorid);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        Date start = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<User> users;
        if (creatorid > 0) {
            users = userRepository.findExpiringBetweenAndBycreatorId(start, end, creatorid);
        } else {
            users = userRepository.findExpiringBetween(start, end);
        }

        List<ExpiringUserResponse> responses = users.stream()
                .map(u -> {
                    long daysLeft = ChronoUnit.DAYS.between(
                            today,
                            DateUtils.toLocalDate(u.getExpiryDate())
                    );

                    String userImg = (u.getLogoImage() != null && !u.getLogoImage().isEmpty())
                            ? u.getLogoImage()
                            : u.getProfilePicture();

                    String role = u.getRoles().stream()
                            .map(r -> r.getName().name())
                            .findFirst()
                            .orElse("N/A");

                    String name = (u.getInstituteName() != null && !u.getInstituteName().isEmpty())
                            ? u.getInstituteName()
                            : u.getFirstName() + " " + u.getLastName();

                    return new ExpiringUserResponse(u.getId(), name, userImg, role, daysLeft);
                })
                .filter(dto -> dto.getDaysLeft() <= days)
                .sorted(Comparator.comparingLong(ExpiringUserResponse::getDaysLeft))
                .collect(Collectors.toList());

        logger.info("Found {} users expiring in next {} days", responses.size(), days);
        return responses;
    }

    @Override
    public void updateSubscription(Long userId, Date expiryDate) {
        logger.info("Updating subscription for user ID: {}", userId);
        if (userId == null || expiryDate == null) {
            logger.error("Invalid parameters for subscription update");
            throw new IllegalArgumentException("userId and expiryDate must be provided");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            logger.error("User not found with ID: {}", userId);
            throw new Apierrorr("User not found with ID " + userId, "404");
        }

        User user = userOpt.get();
        user.setExpiryDate(expiryDate);
        userRepository.save(user);
        logger.info("Subscription updated for user ID: {}", userId);
    }

    @Override
    public UserAccess getEditAccessById(Long id) {
        logger.info("Fetching edit access for user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserAccess userAccess = new UserAccess();
        userAccess.setId(user.getId());
        userAccess.setEditAccess(user.getEditAccess());
        logger.info("Edit access fetched for user ID: {}", id);
        return userAccess;
    }
}