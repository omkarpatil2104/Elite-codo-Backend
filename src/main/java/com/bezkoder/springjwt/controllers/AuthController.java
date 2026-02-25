package com.bezkoder.springjwt.controllers;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.bezkoder.springjwt.dto.*;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.request.*;
import com.bezkoder.springjwt.payload.response.*;
import com.bezkoder.springjwt.repository.InstituteRepository;
import com.bezkoder.springjwt.repository.SubjectRepository;
import com.bezkoder.springjwt.services.UserService;

import javax.mail.MessagingException;
import javax.validation.Valid;

import com.bezkoder.springjwt.services.impl.DeviceManagementService;
import com.bezkoder.springjwt.services.impl.ProfilePatternResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.internet.MimeMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private InstituteRepository instituteRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Convert the provided role into the enum type
            ERole roleEnum;
            try {
                roleEnum = ERole.valueOf("ROLE_" + loginRequest.getRole().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid role provided."));
            }

            // Find user by email + role
            Optional<User> userOpt = userRepository.findByEmailAndRole(loginRequest.getUsername(), roleEnum);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Error: User not found with the given email and role."));
            }

            User user = userOpt.get();

            // Validate password
            if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Error: Bad credentials."));
            }

            // FETCH INSTITUTE FIRST
            InstituteMaster inst = null;
            if (user.getInstituteName() != null && !user.getInstituteName().isEmpty()) {
                inst = instituteRepository.findByInstituteNameIgnoreCase(user.getInstituteName());
            }

            // INSTITUTE STATUS CHECK
            if (inst != null && !"Active".equalsIgnoreCase(inst.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse(
                                "Institute is not active. Please contact admin.",
                                400,
                                false
                        ));
            }

            // INSTITUTE EXPIRY CHECK
            if (inst != null &&
                    inst.getExpiryDate() != null &&
                    inst.getExpiryDate().before(new Date())) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse(
                                "Institute subscription has expired. Please contact institute admin.",
                                400,
                                false
                        ));
            }

            // USER STATUS CHECK (AFTER INSTITUTE)
            if (!"Active".equalsIgnoreCase(user.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("User is not active", 400, false));
            }

            // USER EXPIRY CHECK (AFTER INSTITUTE)
            if (user.getExpiryDate() != null && user.getExpiryDate().before(new Date())) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Your subscription has expired", 400, false));
            }

            // Build UserDetails
            UserDetailsImpl userDetails = UserDetailsImpl.build(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Login timestamps
            user.setLastLogin(user.getCurrentLogin());
            user.setCurrentLogin(new Date());
            user.setIsLoggedIn(true);
            userRepository.save(user);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getProfilePicture(),
                    user.getWatermarkImage(),
                    user.getLogoImage(),
                    user.getColorTheme(),
                    roles,
                    user.getClassName()
            ));
        } catch (Exception e) {
            logger.error("Error in signin: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error during authentication", 500, false));
        }
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<?> logoutUser(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setIsLoggedIn(false);
                userRepository.save(user);
                return ResponseEntity.ok(new MessageResponse("User logged out successfully."));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found."));
        } catch (Exception e) {
            logger.error("Error in logout: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error during logout", 500, false));
        }
    }

    @PostMapping("/register-device")
    public ResponseEntity<ApiResponse<DeviceSession>> registerDevice(@RequestBody DeviceAccessRequest request) {
        try {
            DeviceSession session = deviceManagementService.registerUserDevice(
                    request.getUserId(),
                    request.getRole(),
                    request.getDeviceInfo()
            );
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Device registered successfully",
                    session
            ));
        } catch (Exception e) {
            logger.error("Error registering device: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to register device: " + e.getMessage(),
                    null
            ));
        }
    }

    @PostMapping("/force-logout-other-devices")
    public ResponseEntity<ApiResponse<String>> forceLogoutOtherDevices(
            @RequestBody ForceLogoutRequest req) {
        try {
            int loggedOut = deviceManagementService
                    .forceLogoutOtherDevices(req.getUserId(), req.getCurrentDeviceId());
            return ResponseEntity.ok(
                    new ApiResponse<>(true,
                            loggedOut + " devices logged out successfully",
                            "Success"));
        } catch (Exception e) {
            logger.error("Error force logout: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to logout devices: " + e.getMessage(),
                    null
            ));
        }
    }

    @GetMapping("/user-devices/{userId}")
    public ResponseEntity<ApiResponse<Map<String, List<DeviceSession>>>> getUserDevices(@PathVariable Long userId) {
        try {
            List<DeviceSession> devices = deviceManagementService.getUserActiveDevices(userId);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "User devices retrieved successfully",
                    Collections.singletonMap("devices", devices)
            ));
        } catch (Exception e) {
            logger.error("Error getting user devices: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to get user devices: " + e.getMessage(),
                    null
            ));
        }
    }

    @PostMapping("/validate-session")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validateSession(
            @RequestBody ValidateSessionRequest req) {
        try {
            boolean valid = deviceManagementService
                    .validateUserSession(req.getUserId(), req.getDeviceId(), req.getRole());
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Session validation completed",
                    Collections.singletonMap("valid", valid)));
        } catch (Exception e) {
            logger.error("Error validating session: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to validate session: " + e.getMessage(),
                    null
            ));
        }
    }

    @GetMapping("/check-device/{userId}/{deviceId}")
    public ResponseEntity<DeviceCheckResponse> checkDevice(
            @PathVariable Long userId,
            @PathVariable String deviceId) {
        try {
            DeviceCheckResponse response = deviceManagementService.isDeviceRegistered(userId, deviceId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in checkDevice endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DeviceCheckResponse.builder()
                            .registered(false)
                            .canLogin(false)
                            .message("Internal server error")
                            .build());
        }
    }

    @GetMapping("/device-restrictions/{role}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDeviceRestrictions(@PathVariable String role) {
        try {
            Map<String, Object> restrictions = deviceManagementService.getDeviceRestrictionsByRole(role);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Device restrictions retrieved",
                    restrictions
            ));
        } catch (Exception e) {
            logger.error("Error getting device restrictions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to get device restrictions: " + e.getMessage(),
                    null
            ));
        }
    }

    @PostMapping("/logout-device")
    public ResponseEntity<Map<String,Object>> logoutDevice(
            @RequestBody LogoutDeviceRequest req) {
        try {
            boolean success = deviceManagementService
                    .logoutDevice(req.getUserId(), req.getDeviceId());

            Map<String,Object> body = new HashMap<>();
            body.put("success", success);
            body.put("message", success
                    ? "Device logged out successfully"
                    : "Device not found or already inactive");
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            logger.error("Error logging out device: {}", ex.getMessage(), ex);
            Map<String,Object> body = new HashMap<>();
            body.put("success", false);
            body.put("message", "Failed to logout device: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @PostMapping("/bulk-signup")
    public ResponseEntity<List<String>> handleBulkSignup(
            @RequestParam("creatorId") Long creatorId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            List<String> results = userService.processBulkSignup1(file, creatorId);
            return ResponseEntity.ok(results);
        } catch (IOException ie) {
            logger.error("Error processing file: {}", ie.getMessage(), ie);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList("Error processing file: " + ie.getMessage()));
        } catch (Exception e) {
            logger.error("Error in bulk signup: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList("Error processing file: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            return userService.registerUser(signUpRequest);
        } catch (Exception e) {
            logger.error("Error in signup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error during registration", 500, false));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateStudent(@RequestBody UpdateStudentRequest updateStudentRequest) {
        try {
            // Log the entire request payload
            logger.info("Update Student Request Received: {}", updateStudentRequest.toString());
            logger.info("Teacher ID in request: {}", updateStudentRequest.getTeacherId());
            logger.info("Student ID in request: {}", updateStudentRequest.getId());

            MainResponse mainResponse = this.userService.updateStudent(updateStudentRequest);

            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                logger.info("Student update successful for ID: {}", updateStudentRequest.getId());
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                logger.warn("Student update failed for ID: {}. Response: {}",
                        updateStudentRequest.getId(), mainResponse.getMessage());
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating student ID {}: {}",
                    updateStudentRequest != null ? updateStudentRequest.getId() : "unknown",
                    e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating student", 500, false));
        }
    }
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid ID is required", 400, false));
            }

            UserDetails1 details = this.userService.getById(id);
            if (details == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("User not found with ID: " + id, 404, false));
            }

            return new ResponseEntity<>(details, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting user by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching user", 500, false));
        }
    }

    @GetMapping("/check-bulk-users")
    public ResponseEntity<?> checkBulkUsers() {
        // Get ALL users
        List<User> allUsers = userRepository.findAll();

        List<Map<String, String>> result = new ArrayList<>();

        for (User user : allUsers) {
            // Check if user has ROLE_ADMIN
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_ADMIN);

            if (isAdmin) {
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("email", user.getEmail());
                userInfo.put("status", user.getStatus());
                userInfo.put("creatorId", String.valueOf(user.getCreatorId()));
                result.add(userInfo);
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/getInfo/{id}")
    public ResponseEntity<?> getInfo(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid ID is required", 400, false));
            }

            UserInfo userInfo = userService.getInfoById(id);
            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("User info not found with ID: " + id, 404, false));
            }

            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting user info for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching user info", 500, false));
        }
    }

    @GetMapping("/getEditAccessById/{id}")
    public ResponseEntity<?> getEditAccessById(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid ID is required", 400, false));
            }

            User user = userRepository.findById(id).orElse(null);
            UserAccess userAccess = new UserAccess();
            userAccess.setId(id);

            if (user != null) {
                userAccess.setEditAccess(user.getEditAccess());
                return new ResponseEntity<>(userAccess, HttpStatus.OK);
            } else {
                userAccess.setEditAccess(false);
                return new ResponseEntity<>("User not found with id: " + id + ". Returning editAccess as false.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error getting edit access for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching edit access", 500, false));
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getAll() {
        try {
            List<UserDetails> userDetailsList = this.userService.getAll();
            return new ResponseEntity<>(userDetailsList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching users", 500, false));
        }
    }

    @GetMapping("/getrolewiselist/{role}")
    public ResponseEntity<?> getRoleWiseList(@PathVariable("role") String role) {
        try {
            if (role == null || role.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Role is required", 400, false));
            }

            List<UserDetails> getRoleWiseList = this.userService.getRoleWiseList(role);
            return new ResponseEntity<>(getRoleWiseList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting role wise list for {}: {}", role, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching role wise list", 500, false));
        }
    }

    @GetMapping("/getallactivestudents")
    public ResponseEntity<?> getAllActiveStudents() {
        try {
            String role = "ROLE_STUDENT";
            List<UserDetails> getAllActiveStudents = this.userService.getAllActiveRoleWiseList(role);
            return new ResponseEntity<>(getAllActiveStudents, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting active students: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching active students", 500, false));
        }
    }

    @GetMapping("/allactiveadmins")
    public ResponseEntity<?> allActiveAdmins() {
        try {
            String role = "ROLE_ADMIN";
            List<UserDetails> allActiveAdmins = this.userService.allActiveAdmins(role);
            return new ResponseEntity<>(allActiveAdmins, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting active admins: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching active admins", 500, false));
        }
    }

    @GetMapping("/getallactiveteachers")
    public ResponseEntity<?> getAllActiveModerators() {
        try {
            String role = "ROLE_TEACHER";
            List<UserDetails> getAllActiveStudents = this.userService.getAllActiveRoleWiseList(role);
            return new ResponseEntity<>(getAllActiveStudents, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting active teachers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching active teachers", 500, false));
        }
    }

    @GetMapping("/allactiveinstitute")
    public ResponseEntity<?> allActiveInstitutes() {
        try {
            String role = "ROLE_INSTITUTE";
            List<UserDetails> allActiveAdmins = this.userService.allActiveInstitutes(role);
            return new ResponseEntity<>(allActiveAdmins, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting active institutes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching active institutes", 500, false));
        }
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            MainResponse mainResponse = this.userService.forgotPassword(forgotPasswordRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error in forgot password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error in forgot password", 500, false));
        }
    }

    @PostMapping(value = "/VerificationOtpReq")
    public ResponseEntity<?> VerificationOtpReq(@RequestBody VerificationOTPRequest verificationOtpReq) {
        try {
            VerificationOTPResponse VerificationOtpResponse = userService.VerificationOtp(verificationOtpReq);
            return new ResponseEntity<>(VerificationOtpResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error in OTP verification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error in OTP verification", 500, false));
        }
    }

    @PostMapping("/newpassword")
    public ResponseEntity<?> newPassword(@RequestBody NewPasswordRequest newPasswordRequest) {
        try {
            MainResponse mainResponse = this.userService.newPassword(newPasswordRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error setting new password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error setting new password", 500, false));
        }
    }

    @PostMapping("/changepassword")
    public ResponseEntity<?> changePasswordRequest(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            MainResponse mainResponse = this.userService.changePasswordRequest(changePasswordRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error changing password", 500, false));
        }
    }

    @GetMapping("/useralldetails/{id}")
    public ResponseEntity<?> userAllDetails(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid ID is required", 400, false));
            }

            User user = this.userService.userAllDetails(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("User not found with ID: " + id, 404, false));
            }

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting user details for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching user details", 500, false));
        }
    }

    @GetMapping("/studentcount")
    public ResponseEntity<?> studentCount() {
        try {
            Integer count = this.userService.studentCount();
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting student count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching student count", 500, false));
        }
    }

    @GetMapping("/teachercount")
    public ResponseEntity<?> teacherCount() {
        try {
            Integer count = this.userService.teacherCount();
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting teacher count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching teacher count", 500, false));
        }
    }

    @PutMapping("/updateteacher")
    public ResponseEntity<?> updateTeacher(@RequestBody UpdateTeacherRequest updateTeacherRequest) {
        try {
            MainResponse mainResponse = this.userService.updateTeacher(updateTeacherRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating teacher: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating teacher", 500, false));
        }
    }

    @GetMapping("/studentdetailsbyid/{id}")
    public ResponseEntity<?> studentDetailsById(@PathVariable("id") Long id) {
        try {
            logger.info("Fetching student details for ID: {}", id);

            if (id == null || id <= 0) {
                logger.warn("Invalid student ID provided: {}", id);
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID is required", 400, false));
            }

            StudentDetailsResponse studentDetailsResponse = this.userService.studentDetailsById(id);

            if (studentDetailsResponse == null) {
                logger.warn("Student not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Student not found with ID: " + id, 404, false));
            }

            logger.info("Successfully fetched student details for ID: {}", id);
            return ResponseEntity.ok(studentDetailsResponse);

        } catch (NoSuchElementException e) {
            logger.error("Student not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MainResponse("Student not found with ID: " + id, 404, false));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request for student ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MainResponse(e.getMessage(), 400, false));
        } catch (Exception e) {
            logger.error("Error fetching student details for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error while fetching student details", 500, false));
        }
    }

    @GetMapping("/allteachers")
    public ResponseEntity<?> allTeachers() {
        try {
            List<ProfilePatternResponse> allTeachers = this.userService.allTeachers();
            return new ResponseEntity<>(allTeachers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting all teachers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching all teachers", 500, false));
        }
    }

    @GetMapping("/allteachers/{id}")
    public ResponseEntity<?> allTeachersByCreatorId(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid creator ID is required", 400, false));
            }

            List<ProfilePatternResponse> allTeachers = this.userService.allTeachersByCreatorId(id);
            return new ResponseEntity<>(allTeachers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting teachers by creator ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching teachers", 500, false));
        }
    }

    @GetMapping("/allstudents/{id}")
    public ResponseEntity<?> allStudentsByCreatorId(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid creator ID is required", 400, false));
            }

            List<UserDetails> allTeachers = this.userService.allStudentsByCreatorId(id);
            return new ResponseEntity<>(allTeachers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting students by creator ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching students", 500, false));
        }
    }

    @GetMapping("/allstudents")
    public ResponseEntity<?> allStudents() {
        try {
            List<UserDetails> allStudents = this.userService.allStudents();
            return new ResponseEntity<>(allStudents, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting all students: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching all students", 500, false));
        }
    }

    @GetMapping("/countof")
    public ResponseEntity<?> countOfStudentTeacherQuestion() {
        try {
            CountOfStudentTeacherQuestionResponse countOfStudentTeacherQuestionResponse = this.userService.countOfStudentTeacherQuestion();
            return new ResponseEntity<>(countOfStudentTeacherQuestionResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting counts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching counts", 500, false));
        }
    }

    @PostMapping("/teacherassignments")
    public ResponseEntity<?> teacherAssignments(@RequestBody TeacherRequestAssignment teacherRequestAssignment) {
        try {
            MainResponse mainResponse = this.userService.teacherAssignments(teacherRequestAssignment);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error in teacher assignments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error in teacher assignments", 500, false));
        }
    }

    @PostMapping("/addteacherassignemnts")
    public ResponseEntity<?> addTeacherAssignments(@RequestBody TeacherRequestAssignment teacherRequestAssignment) {
        try {
            MainResponse mainResponse = this.userService.addTeacherAssignments(teacherRequestAssignment);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error adding teacher assignments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error adding teacher assignments", 500, false));
        }
    }

    @PostMapping("/addstudentassignemnts")
    public ResponseEntity<?> addStudentAssignments(@RequestBody TeacherRequestAssignment teacherRequestAssignment) {
        try {
            MainResponse mainResponse = this.userService.addStudentAssignment(teacherRequestAssignment);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error adding student assignments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error adding student assignments", 500, false));
        }
    }

    @PutMapping("/updateteacherassignments")
    public ResponseEntity<?> updateTeacherAssignments(@RequestBody TeacherRequestAssignment teacherRequestAssignment) {
        try {
            MainResponse mainResponse = this.userService.updateTeacherAssignments(teacherRequestAssignment);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating teacher assignments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating teacher assignments", 500, false));
        }
    }

    @PutMapping("/updatestudentassignments")
    public ResponseEntity<?> updateStudentAssignments(@RequestBody TeacherRequestAssignment teacherRequestAssignment) {
        try {
            MainResponse mainResponse = this.userService.updateStudentAssignments(teacherRequestAssignment);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating student assignments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating student assignments", 500, false));
        }
    }

    @GetMapping("/useridandentranceexamidwisedata/{id}/{entranceExamId}")
    public ResponseEntity<?> userIdAndEntranceExamIdWiseData(@PathVariable("id") Long id, @PathVariable("entranceExamId") Integer entranceExamId) {
        try {
            if (id == null || id <= 0 || entranceExamId == null || entranceExamId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid ID and entrance exam ID are required", 400, false));
            }

            UserManagementResponse userManagementResponse = this.userService.userIdAndEntranceExamIdWiseData(id, entranceExamId);
            return new ResponseEntity<>(userManagementResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting user and entrance exam data for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching user and entrance exam data", 500, false));
        }
    }

    @GetMapping("/studentidandentranceexamidwisedata/{id}/{entranceExamId}")
    public ResponseEntity<?> studentIdAndEntranceExamIdWiseData(@PathVariable("id") Long id, @PathVariable("entranceExamId") Integer entranceExamId) {
        try {
            if (id == null || id <= 0 || entranceExamId == null || entranceExamId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid ID and entrance exam ID are required", 400, false));
            }

            UserManagementResponse userManagementResponse = this.userService.studentIdAndEntranceExamIdWiseData(id, entranceExamId);
            return new ResponseEntity<>(userManagementResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting student and entrance exam data for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching student and entrance exam data", 500, false));
        }
    }

    @GetMapping("/teacheraccessmentdetails/{id}")
    public ResponseEntity<?> teacherAssessmentDetails(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid teacher ID is required", 400, false));
            }

            TeacherAssessmentDetails teacherAssessmentDetails = this.userService.teacherAssessmentDetails(id);
            if (teacherAssessmentDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Teacher assessment details not found for ID: " + id, 404, false));
            }

            return new ResponseEntity<>(teacherAssessmentDetails, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting teacher assessment details for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching teacher assessment details", 500, false));
        }
    }

    @GetMapping("/teachermappeddata/{id}")
    public ResponseEntity<?> teacherMappedData(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid teacher ID is required", 400, false));
            }

            TeacherMappedData teacherMappedData = this.userService.teacherMappedData(id);
            if (teacherMappedData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Teacher mapped data not found for ID: " + id, 404, false));
            }

            return new ResponseEntity<>(teacherMappedData, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting teacher mapped data for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching teacher mapped data", 500, false));
        }
    }

    @GetMapping("/teacherSubject/{id}")
    public ResponseEntity<?> teacherSubject(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid teacher ID is required", 400, false));
            }

            List<Integer> subjectList = userRepository.getSubjectListById(id);
            return new ResponseEntity<>(subjectList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting teacher subjects for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching teacher subjects", 500, false));
        }
    }

    @DeleteMapping("/deleteuser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid user ID is required", 400, false));
            }

            MainResponse mainResponse = this.userService.deleteUser(id);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error deleting user", 500, false));
        }
    }

    @DeleteMapping("/BulkDelete")
    public ResponseEntity<?> deleteUser(@RequestBody List<Long> userIds) {
        try {
            if (userIds == null || userIds.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("User IDs are required", 400, false));
            }

            userService.deleteAllByIds(userIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error bulk deleting users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error bulk deleting users", 500, false));
        }
    }

    @PutMapping("/updateuserpassword")
    public ResponseEntity<?> updateUserPassword(@RequestBody NewPasswordRequest newPasswordRequest) {
        try {
            MainResponse mainResponse = this.userService.updateUserPassword(newPasswordRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating user password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating user password", 500, false));
        }
    }

    @PostMapping("/studentassignments")
    public ResponseEntity<?> addStudentAssignments(@RequestBody StudentAssignmentsRequest studentAssignmentsRequest) {
        try {
            MainResponse mainResponse = this.userService.addStudentAssignments(studentAssignmentsRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error adding student assignments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error adding student assignments", 500, false));
        }
    }

    @GetMapping("/studentidwiseallaccessmanagements/{id}")
    public ResponseEntity<?> studentIdWiseAccessManagements(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID is required", 400, false));
            }

            UserManagementResponse userManagementResponse = this.userService.studentIdWiseAccessManagements(id);
            if (userManagementResponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Student access management not found for ID: " + id, 404, false));
            }

            return new ResponseEntity<>(userManagementResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting student access management for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching student access management", 500, false));
        }
    }

    @GetMapping("/studentmappeddata/{id}")
    public ResponseEntity<?> studentMappedData(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID is required", 400, false));
            }

            StudentMappedData studentMappedData = this.userService.studentMappedData(id);
            if (studentMappedData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Student mapped data not found for ID: " + id, 404, false));
            }

            return new ResponseEntity<>(studentMappedData, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting student mapped data for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching student mapped data", 500, false));
        }
    }

    @DeleteMapping("/delete/{id}/{entranceExamId}/{standardId}")
    public ResponseEntity<?> deleteMappedDataOfTeacher(@PathVariable("id") Long id,
                                                       @PathVariable("entranceExamId") Integer entranceExamId,
                                                       @PathVariable("standardId") Integer standardId,
                                                       @RequestParam("role") String role) {
        try {
            if (id == null || id <= 0 || entranceExamId == null || entranceExamId <= 0 || standardId == null || standardId <= 0 || role == null || role.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("All parameters are required", 400, false));
            }

            MainResponse mainResponse = this.userService.deleteMappedDataOfTeacher(id, entranceExamId, standardId, role);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error deleting mapped data for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error deleting mapped data", 500, false));
        }
    }

    @GetMapping("/pendingteacherlist")
    public ResponseEntity<?> pendingTeacherList() {
        try {
            List<PendingRolesList> pendingTeacherList = this.userService.pendingTeacherList();
            return new ResponseEntity<>(pendingTeacherList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting pending teacher list: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching pending teacher list", 500, false));
        }
    }

    @GetMapping("/pendingstudentlist")
    public ResponseEntity<?> pendingStudentList() {
        try {
            List<PendingRolesList> pendingStudentList = this.userService.pendingStudentList();
            return new ResponseEntity<>(pendingStudentList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting pending student list: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching pending student list", 500, false));
        }
    }

    @GetMapping("/pendingparentlist")
    public ResponseEntity<?> pendingParentList() {
        try {
            List<PendingRolesList> pendingParentList = this.userService.pendingParentList();
            return new ResponseEntity<>(pendingParentList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting pending parent list: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching pending parent list", 500, false));
        }
    }

    @GetMapping("/pendingadminlist")
    public ResponseEntity<?> pendingAdminList() {
        try {
            List<PendingRolesList> pendingAdminList = this.userService.pendingAdminList();
            return new ResponseEntity<>(pendingAdminList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting pending admin list: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching pending admin list", 500, false));
        }
    }

    @GetMapping("/pendinginstitutelist")
    public ResponseEntity<?> pendingInstituteList() {
        try {
            List<PendingRolesList> pendingInstituteList = this.userService.pendingInstituteList();
            return new ResponseEntity<>(pendingInstituteList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting pending institute list: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching pending institute list", 500, false));
        }
    }

    @GetMapping("/acceptuser/{id}/{acceptby}")
    public ResponseEntity<?> acceptUser(@PathVariable("id") Long id, @PathVariable("acceptby") Long acceptby) {
        try {
            if (id == null || id <= 0 || acceptby == null || acceptby <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid user ID and acceptor ID are required", 400, false));
            }

            MainResponse mainResponse = this.userService.acceptUser(id, acceptby);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error accepting user {} by {}: {}", id, acceptby, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error accepting user", 500, false));
        }
    }

    @GetMapping("/dashboarddetails")
    public ResponseEntity<?> dashBoardDetails() {
        try {
            List<DashBoardDetails> dashBoardDetails = this.userService.dashBoardDetails();
            return new ResponseEntity<>(dashBoardDetails, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting dashboard details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching dashboard details", 500, false));
        }
    }

    @GetMapping("/teacheridwiseallaccessmanagements/{id}")
    public ResponseEntity<?> teacherIdWiseAccessManagements(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid teacher ID is required", 400, false));
            }

            UserManagementResponse userManagementResponse = this.userService.teacherIdWiseAccessManagements(id);
            if (userManagementResponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Teacher access management not found for ID: " + id, 404, false));
            }

            return new ResponseEntity<>(userManagementResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting teacher access management for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching teacher access management", 500, false));
        }
    }

    @GetMapping("/countsforsuperadmin")
    public ResponseEntity<?> usersCounts() {
        try {
            List<UsersCountResponse> usersCountResponses = this.userService.usersCounts();
            return new ResponseEntity<>(usersCountResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting user counts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching user counts", 500, false));
        }
    }

    @GetMapping("/activationcounts")
    public ResponseEntity<?> activationCounts() {
        try {
            List<ActivationCountResponse> activationCountResponses = this.userService.activationCounts();
            return new ResponseEntity<>(activationCountResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting activation counts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching activation counts", 500, false));
        }
    }

    @PutMapping("/bulkuserchangestatus")
    public ResponseEntity<?> bulkUserChangeStatus(@RequestBody BulkUserChangeStatusRequest bulkUserChangeStatusRequest) {
        try {
            MainResponse mainResponse = this.userService.bulkUserChangeStatus(bulkUserChangeStatusRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error bulk changing user status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error bulk changing user status", 500, false));
        }
    }

    @GetMapping("/studentwiseparent/{id}")
    public ResponseEntity<?> studentWiseParent(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID is required", 400, false));
            }

            ParentDetails parentDetails = this.userService.studentWiseParent(id);
            if (parentDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Parent details not found for student ID: " + id, 404, false));
            }

            return new ResponseEntity<>(parentDetails, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting parent for student ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching parent details", 500, false));
        }
    }

    @GetMapping("/acceptparent/{id}/{acceptby}")
    public ResponseEntity<?> acceptParent(@PathVariable("id") Long id, @PathVariable("acceptby") Long acceptby) {
        try {
            if (id == null || id <= 0 || acceptby == null || acceptby <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid parent ID and acceptor ID are required", 400, false));
            }

            MainResponse mainResponse = this.userService.acceptParent(id, acceptby);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error accepting parent {} by {}: {}", id, acceptby, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error accepting parent", 500, false));
        }
    }

    @PutMapping("/updateparent")
    public ResponseEntity<?> updateParent(@RequestBody ParentInformationUpdateRequest parentInformationUpdateRequest) {
        try {
            MainResponse mainResponse = this.userService.updateParent(parentInformationUpdateRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating parent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating parent", 500, false));
        }
    }

    @PutMapping("/bulkparentstatuschange")
    public ResponseEntity<?> bulkParentStatusChange(@RequestBody BulkParentStatusChangeRequest bulkParentStatusChangeRequest) {
        try {
            MainResponse mainResponse = this.userService.bulkParentStatusChange(bulkParentStatusChangeRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error bulk changing parent status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error bulk changing parent status", 500, false));
        }
    }

    @GetMapping("/rolesactivities")
    public ResponseEntity<?> rolesActivities() {
        try {
            List<RolesActivitiesResponse> rolesActivitiesResponse = this.userService.rolesActivities();
            return new ResponseEntity<>(rolesActivitiesResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting roles activities: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching roles activities", 500, false));
        }
    }

    @PutMapping("/updatestatus")
    public ResponseEntity<?> updateStatus(@RequestBody UpdateStatusRequest updateStatusRequest) {
        try {
            MainResponse mainResponse = this.userService.updateStatus(updateStatusRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating status", 500, false));
        }
    }

    @GetMapping("/getallactiveinstitutes")
    public ResponseEntity<?> getAllActiveInstitutes() {
        try {
            String role = "ROLE_INSTITUTE";
            List<InstituteDetailsResponse> instituteDetailsResponses = this.userService.getAllActiveInstitutes(role);
            return new ResponseEntity<>(instituteDetailsResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting active institutes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching active institutes", 500, false));
        }
    }

    @GetMapping("/getallactiveinstitutes/{id}")
    public ResponseEntity<?> getAllActiveInstitutesById(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid creator ID is required", 400, false));
            }

            String role = "ROLE_INSTITUTE";
            List<InstituteDetailsResponse> instituteDetailsResponses = this.userService.getAllActiveInstitutesById(role, id);
            return new ResponseEntity<>(instituteDetailsResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting active institutes by creator ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching active institutes", 500, false));
        }
    }

    @GetMapping("/getinstitutebyid/{id}")
    public ResponseEntity<?> getInstituteById(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid institute ID is required", 400, false));
            }

            InstituteDetailsResponse response = this.userService.getInstituteById(id);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Institute not found with ID: " + id, 404, false));
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting institute by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching institute", 500, false));
        }
    }

    @PutMapping("/updateinstitute")
    public ResponseEntity<?> updateInstitute(@RequestBody UpdateInstituteRequest updateInstituteRequest) {
        try {
            MainResponse mainResponse = this.userService.updateInstitute(updateInstituteRequest);
            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error updating institute: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating institute", 500, false));
        }
    }

    @DeleteMapping("/deleteinstitute/{id}")
    public ResponseEntity<?> deleteInstitute(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid institute ID is required", 400, false));
            }

            MainResponse mainResponse = this.userService.deleteInstitute(id);

            if (Boolean.TRUE.equals(mainResponse.getFlag())) {
                logger.info("Institute deleted successfully with ID: {}", id);
                return new ResponseEntity<>(mainResponse, HttpStatus.OK);
            } else {
                logger.warn("Institute deletion failed for ID: {}. Response: {}",
                        id, mainResponse.getMessage());
                return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error deleting institute with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error deleting institute: " + e.getMessage(),
                            500, false));
        }
    }

    @DeleteMapping("/bulk-delete-institutes")
    public ResponseEntity<?> bulkDeleteInstitutes(@RequestBody List<Long> instituteIds) {
        try {
            if (instituteIds == null || instituteIds.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Institute IDs are required", 400, false));
            }

            List<MainResponse> results = new ArrayList<>();
            List<Long> successfulDeletions = new ArrayList<>();
            List<Long> failedDeletions = new ArrayList<>();

            for (Long id : instituteIds) {
                try {
                    MainResponse response = this.userService.deleteInstitute(id);
                    if (Boolean.TRUE.equals(response.getFlag())) {
                        successfulDeletions.add(id);
                    } else {
                        failedDeletions.add(id);
                    }
                } catch (Exception e) {
                    failedDeletions.add(id);
                    logger.error("Error deleting institute ID {}: {}", id, e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("successfulDeletions", successfulDeletions);
            response.put("failedDeletions", failedDeletions);
            response.put("totalProcessed", instituteIds.size());
            response.put("successCount", successfulDeletions.size());
            response.put("failureCount", failedDeletions.size());
            response.put("message", String.format("Processed %d institutes. Success: %d, Failed: %d",
                    instituteIds.size(),
                    successfulDeletions.size(),
                    failedDeletions.size()));

            if (failedDeletions.isEmpty()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response);
            }

        } catch (Exception e) {
            logger.error("Error bulk deleting institutes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error bulk deleting institutes: " + e.getMessage(),
                            500, false));
        }
    }

    @GetMapping("/getinactiveinstitutes")
    public ResponseEntity<?> getAllInActiveInstitutes() {
        try {
            List<InstituteDetailsResponse> instituteDetailsResponses = this.userService.getAllInActiveInstitutes();
            return new ResponseEntity<>(instituteDetailsResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting inactive institutes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching inactive institutes", 500, false));
        }
    }

    @GetMapping("/getStandard/{id}")
    public ResponseEntity<?> findSubjectsAssignedToStudentId(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID is required", 400, false));
            }

            List<StandardResponse1> response = userService.findSubjectsAssignedToStudentId(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting subjects for student ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching subjects", 500, false));
        }
    }

    @GetMapping("/institutecounts/{id}")
    public ResponseEntity<?> countsOfInstitutesUsers(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid institute ID is required", 400, false));
            }

            InstituteCountsResponse response = this.userService.countsOfInstitutesUsers(id);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Institute counts not found for ID: " + id, 404, false));
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting institute counts for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching institute counts", 500, false));
        }
    }

    @GetMapping("/userandentranceandstandardwisesubjects/{id}/{entranceExamId}/{standardId}")
    public ResponseEntity<?> userAndEntranceExamAndStandardIdWiseSubjects(@PathVariable("id") Long id, @PathVariable("entranceExamId") Integer entranceExamId, @PathVariable("standardId") Integer standardId) {
        try {
            if (id == null || id <= 0 || entranceExamId == null || entranceExamId <= 0 || standardId == null || standardId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("All parameters are required", 400, false));
            }

            List<SubjectMastersResponse> subjectMastersResponses = this.userService.userAndEntranceExamAndStandardIdWiseSubjects(id, entranceExamId, standardId);
            return new ResponseEntity<>(subjectMastersResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting subjects for user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching subjects", 500, false));
        }
    }

    @GetMapping("/userandentrancewisestandards/{id}/{entranceExamId}")
    public ResponseEntity<?> userAndEntranceExamIdWiseStandards(@PathVariable("id") Long id, @PathVariable("entranceExamId") Integer entranceExamId) {
        try {
            if (id == null || id <= 0 || entranceExamId == null || entranceExamId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid user ID and entrance exam ID are required", 400, false));
            }

            Set<StandardMasterResponse> standardMasterResponses = this.userService.userAndEntranceExamIdWiseStandards(id, entranceExamId);
            return new ResponseEntity<>(standardMasterResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting standards for user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching standards", 500, false));
        }
    }

    @GetMapping("/teacherentranceexams/{id}")
    public ResponseEntity<?> teacherWiseEntranceExams(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid teacher ID is required", 400, false));
            }

            Set<EntranceExamResponse> entranceExamResponses = this.userService.teacherWiseEntranceExams(id);
            return new ResponseEntity<>(entranceExamResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting entrance exams for teacher {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching entrance exams", 500, false));
        }
    }

    @GetMapping("/studententranceexams/{id}")
    public ResponseEntity<?> studentEntranceExams(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID is required", 400, false));
            }

            Set<EntranceExamResponse> entranceExamResponses = this.userService.studentEntranceExams(id);
            return new ResponseEntity<>(entranceExamResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting entrance exams for student {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching entrance exams", 500, false));
        }
    }

    @GetMapping("/studentdata/{entranceExamId}/{id}")
    public ResponseEntity<?> studentData(@PathVariable("entranceExamId") Integer entranceExamId, @PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0 || entranceExamId == null || entranceExamId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID and entrance exam ID are required", 400, false));
            }

            List<StandardSubjectResponse> StandardSubjectResponse = this.userService.studentData(entranceExamId, id);
            return new ResponseEntity<>(StandardSubjectResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting student data for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching student data", 500, false));
        }
    }

    @GetMapping("/getStudentById/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID is required", 400, false));
            }

            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MainResponse("Student not found with ID: " + id, 404, false));
            }

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting student by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching student", 500, false));
        }
    }

    @GetMapping("/getAllStudentByTeachId/{id}")
    public ResponseEntity<?> getAllStudentByTeachId(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid teacher ID is required", 400, false));
            }

            List<UserDetails> students = userService.getAllStudentByTeachId(id);
            if (students.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("No students found for this teacher."));
            }
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting students by teacher ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching students", 500, false));
        }
    }

    @GetMapping("/getAllTestByStudent/{id}")
    public ResponseEntity<?> getAllTestNameAttemptedByThatStudent(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid student ID is required", 400, false));
            }

            List<TestNameResponse> testName = userService.getAllTestByStudent(id);
            if (testName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("No test found for this student."));
            }
            return ResponseEntity.ok(testName);
        } catch (Exception e) {
            logger.error("Error getting tests for student ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching tests", 500, false));
        }
    }

    @GetMapping("/dashboardCount/{teacherId}")
    public ResponseEntity<?> teacherDashboardCount(@PathVariable Long teacherId) {
        try {
            if (teacherId == null || teacherId <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid teacher ID is required", 400, false));
            }

            TeacherDashboardCountResponse response = userService.getTeacherDashboardCounts(teacherId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting dashboard count for teacher {}: {}", teacherId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching dashboard count", 500, false));
        }
    }

    @GetMapping("getUserStats/{id}")
    public ResponseEntity<?> getUserStats(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid user ID is required", 400, false));
            }

            List<UserStatResponse> userStatResponses = userService.getUserStats(id);
            return new ResponseEntity<>(userStatResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting user stats for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching user stats", 500, false));
        }
    }

    @GetMapping("/expiring-users/{id}")
    public ResponseEntity<?> getExpiringUsers(@PathVariable("id") Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid creator ID is required", 400, false));
            }

            List<ExpiringUserResponse> list = userService.getUsersExpiringInNextDays(15, id);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error getting expiring users for creator {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error fetching expiring users", 500, false));
        }
    }

    @PutMapping("/update-expiry")
    public ResponseEntity<?> updateSubscription(@RequestBody UpdateExpiryRequest req) {
        try {
            if (req.getUserId() == null || req.getUserId() <= 0 || req.getExpiryDate() == null) {
                return ResponseEntity.badRequest()
                        .body(new MainResponse("Valid user ID and expiry date are required", 400, false));
            }

            userService.updateSubscription(req.getUserId(), req.getExpiryDate());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User expiry date updated successfully");
            response.put("status", 200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating subscription: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MainResponse("Internal server error updating subscription", 500, false));
        }
    }
}