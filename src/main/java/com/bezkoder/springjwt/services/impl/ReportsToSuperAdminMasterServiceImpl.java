package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.ReportsToSuperAdminMaster;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.ReportsToSuperAdminMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.ReplyReportsToSuperAdminResponse;
import com.bezkoder.springjwt.payload.response.ReplyReportsToSuperAdminResponses;
import com.bezkoder.springjwt.payload.response.ReportsToSuperAdminResponse;
import com.bezkoder.springjwt.repository.ReportsToSuperAdminMasterRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.ReportsToSuperAdminMasterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsToSuperAdminMasterServiceImpl implements ReportsToSuperAdminMasterService {
    @Autowired
    private ReportsToSuperAdminMasterRepository reportsToSuperAdminMasterRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public MainResponse sendReports(ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest) {
        MainResponse mainResponse = new MainResponse();
        ReportsToSuperAdminMaster reportsToSuperAdminMaster = new ReportsToSuperAdminMaster();
        User sender = this.userRepository.findById(reportsToSuperAdminMasterRequest.getSenderId()).orElseThrow(()->new RuntimeException("Sender not found"));

        try {
            BeanUtils.copyProperties(reportsToSuperAdminMasterRequest,reportsToSuperAdminMaster);
            reportsToSuperAdminMaster.setDate(new Date());
            reportsToSuperAdminMaster.setSenderId(sender.getId());
            reportsToSuperAdminMaster.setStatus(reportsToSuperAdminMasterRequest.getStatus());
            this.reportsToSuperAdminMasterRepository.save(reportsToSuperAdminMaster);
            mainResponse.setMessage("Report to the super admin");
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
    public List<ReportsToSuperAdminMaster> senderIdWiseReports(Long senderId) {
        List<ReportsToSuperAdminMaster> reportsToSuperAdminMasters = this.reportsToSuperAdminMasterRepository.senderIdWiseReports(senderId);
        return reportsToSuperAdminMasters;
    }

    @Override
    public List<ReportsToSuperAdminResponse> getAllReports() {
        List<ReportsToSuperAdminMaster> reportsToSuperAdminMasters1 = this.reportsToSuperAdminMasterRepository.findAll();
        List<ReportsToSuperAdminMaster> reportsToSuperAdminMasters = reportsToSuperAdminMasters1.stream().filter(e->e.getTeacherId()==null).collect(Collectors.toList());
        List<ReportsToSuperAdminMaster> filterList = reportsToSuperAdminMasters.stream()
                .filter(e -> e.getStatus() != null && !e.getStatus().equals("reply"))
                .sorted(Comparator.comparing(ReportsToSuperAdminMaster::getReportsToSuperAdminId).reversed())
                .collect(Collectors.toList());

        List<ReportsToSuperAdminResponse> reportsToSuperAdminResponses = new ArrayList<>();

        for (ReportsToSuperAdminMaster reportsToSuperAdminMaster : filterList) {
            ReportsToSuperAdminResponse reportsToSuperAdminResponse = new ReportsToSuperAdminResponse();
            reportsToSuperAdminResponse.setReportsToSuperAdminId(reportsToSuperAdminMaster.getReportsToSuperAdminId());
            User sender = this.userRepository.findById(reportsToSuperAdminMaster.getSenderId()).get();
            reportsToSuperAdminResponse.setUserId(reportsToSuperAdminMaster.getSenderId());
            String name = sender.getFirstName()+" "+sender.getLastName();
            reportsToSuperAdminResponse.setUserName(name);
            reportsToSuperAdminResponse.setUserAvatar(sender.getProfilePicture());
            reportsToSuperAdminResponse.setPriority(reportsToSuperAdminMaster.getPriority());
            reportsToSuperAdminResponse.setStatus(reportsToSuperAdminMaster.getStatus());
            reportsToSuperAdminResponse.setDate(reportsToSuperAdminMaster.getDate());
            reportsToSuperAdminResponse.setMessage(reportsToSuperAdminMaster.getDescription());

            if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_ADMIN")){
                reportsToSuperAdminResponse.setUserType("Admin");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_STUDENT")){
                reportsToSuperAdminResponse.setUserType("Student");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_TEACHER")) {
                reportsToSuperAdminResponse.setUserType("Teacher");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_INSTITUTE")) {
                reportsToSuperAdminResponse.setUserType("Institute");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_PARENT")) {
                reportsToSuperAdminResponse.setUserType("Parent");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_SUPER_ADMIN")) {
                reportsToSuperAdminResponse.setUserType("SuperAdmin");
            }

            reportsToSuperAdminResponses.add(reportsToSuperAdminResponse);
        }

        return reportsToSuperAdminResponses;
    }

    @Override
    public ReportsToSuperAdminResponse getReportsFromReportId(Integer reportsToSuperAdminId) {
        ReportsToSuperAdminMaster reportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.findById(reportsToSuperAdminId).orElseThrow(()->new RuntimeException("Reports not found"));
        User sender = this.userRepository.findById(reportsToSuperAdminMaster.getSenderId()).get();
        ReportsToSuperAdminResponse  response = new ReportsToSuperAdminResponse();
        response.setUserId(reportsToSuperAdminMaster.getSenderId());
        String name = sender.getFirstName()+" "+sender.getLastName();
        response.setUserName(name);
        response.setUserAvatar(sender.getProfilePicture());
        response.setPriority(reportsToSuperAdminMaster.getPriority());
        response.setStatus(reportsToSuperAdminMaster.getStatus());
        response.setDate(reportsToSuperAdminMaster.getDate());
        response.setMessage(reportsToSuperAdminMaster.getDescription());
        response.setUserType(reportsToSuperAdminMaster.getType());

        return response;
    }

    @Override
    public MainResponse reply(ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest) {
        MainResponse mainResponse = new MainResponse();
        ReportsToSuperAdminMaster reportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.findById(reportsToSuperAdminMasterRequest.getReportsToSuperAdminId()).get();

        try {
            ReportsToSuperAdminMaster master = new ReportsToSuperAdminMaster();
            master.setReplyReportsToSuperAdminId(reportsToSuperAdminMaster.getReportsToSuperAdminId());
            master.setDate(new Date());
            master.setReceiverId(reportsToSuperAdminMaster.getSenderId());
            master.setStatus("reply");
            master.setDescription(reportsToSuperAdminMasterRequest.getDescription());
            master.setAttachments(reportsToSuperAdminMasterRequest.getAttachments());
            this.reportsToSuperAdminMasterRepository.save(master);
            mainResponse.setMessage("Reply send to the user");
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
    public ReportsToSuperAdminResponse readReport(Integer reportsToSuperAdminId) {
        ReportsToSuperAdminMaster reportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.findById(reportsToSuperAdminId).orElseThrow(()->new RuntimeException("Reports not found"));
        reportsToSuperAdminMaster.setStatus("read");
        try {
            this.reportsToSuperAdminMasterRepository.save(reportsToSuperAdminMaster);
        }catch (Exception e){
            e.printStackTrace();
        }
        User sender = this.userRepository.findById(reportsToSuperAdminMaster.getSenderId()).get();
        ReportsToSuperAdminResponse  response = new ReportsToSuperAdminResponse();
        response.setUserId(reportsToSuperAdminMaster.getSenderId());
        String name = sender.getFirstName()+" "+sender.getLastName();
        response.setUserName(name);
        response.setUserAvatar(sender.getProfilePicture());
        response.setPriority(reportsToSuperAdminMaster.getPriority());
        response.setStatus(reportsToSuperAdminMaster.getStatus());
        response.setDate(reportsToSuperAdminMaster.getDate());
        response.setMessage(reportsToSuperAdminMaster.getDescription());
        response.setUserType(reportsToSuperAdminMaster.getType());

        return response;
    }

    @Override
    public ReplyReportsToSuperAdminResponses getAllReply(Integer reportsToSuperAdminId) {
        ReplyReportsToSuperAdminResponses reportsToSuperAdminResponses = new ReplyReportsToSuperAdminResponses();
        ReportsToSuperAdminMaster reportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.findById(reportsToSuperAdminId).get();

        reportsToSuperAdminResponses.setReportsToSuperAdminId(reportsToSuperAdminMaster.getReportsToSuperAdminId());
        User sender = this.userRepository.findById(reportsToSuperAdminMaster.getSenderId()).get();
        reportsToSuperAdminResponses.setUserId(reportsToSuperAdminMaster.getSenderId());
        String name = sender.getFirstName()+" "+sender.getLastName();
        reportsToSuperAdminResponses.setUserName(name);
        reportsToSuperAdminResponses.setUserAvatar(sender.getProfilePicture());
        reportsToSuperAdminResponses.setPriority(reportsToSuperAdminMaster.getPriority());
        reportsToSuperAdminResponses.setStatus(reportsToSuperAdminMaster.getStatus());
        reportsToSuperAdminResponses.setDate(reportsToSuperAdminMaster.getDate());
        reportsToSuperAdminResponses.setAttachments(reportsToSuperAdminMaster.getAttachments());
        reportsToSuperAdminResponses.setMessage(reportsToSuperAdminMaster.getDescription());
        reportsToSuperAdminResponses.setEmail(sender.getEmail());

        if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_ADMIN")){
            reportsToSuperAdminResponses.setUserType("Admin");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_STUDENT")){
            reportsToSuperAdminResponses.setUserType("Student");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_TEACHER")) {
            reportsToSuperAdminResponses.setUserType("Teacher");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_INSTITUTE")) {
            reportsToSuperAdminResponses.setUserType("Institute");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_PARENT")) {
            reportsToSuperAdminResponses.setUserType("Parent");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_SUPER_ADMIN")) {
            reportsToSuperAdminResponses.setUserType("SuperAdmin");
        }

        ReportsToSuperAdminMaster replyReportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.getReply(reportsToSuperAdminId);
        if (replyReportsToSuperAdminMaster!=null){
            ReplyReportsToSuperAdminResponse replyReportsToSuperAdminResponse = new ReplyReportsToSuperAdminResponse();
            replyReportsToSuperAdminResponse.setReportsToSuperAdminId(replyReportsToSuperAdminMaster.getReportsToSuperAdminId());
            replyReportsToSuperAdminResponse.setStatus(replyReportsToSuperAdminMaster.getStatus());
            replyReportsToSuperAdminResponse.setMessage(replyReportsToSuperAdminMaster.getDescription());
            replyReportsToSuperAdminResponse.setDate(replyReportsToSuperAdminMaster.getDate());
            replyReportsToSuperAdminResponse.setAttachments(replyReportsToSuperAdminMaster.getAttachments());

            reportsToSuperAdminResponses.setReply(replyReportsToSuperAdminResponse);
        }

        return reportsToSuperAdminResponses;
    }

    @Override
    public MainResponse sendReportToTeacher(ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest) {
        MainResponse mainResponse = new MainResponse();

        ReportsToSuperAdminMaster reportsToSuperAdminMaster = new ReportsToSuperAdminMaster();
        User sender = this.userRepository.findById(reportsToSuperAdminMasterRequest.getSenderId()).orElseThrow(()->new RuntimeException("Sender not found"));
        Long teacher = sender.getTeacher().stream().findFirst().get().getId();
        try {
            BeanUtils.copyProperties(reportsToSuperAdminMasterRequest,reportsToSuperAdminMaster);
            reportsToSuperAdminMaster.setDate(new Date());
            reportsToSuperAdminMaster.setTeacherId(teacher);
            reportsToSuperAdminMaster.setSenderId(sender.getId());
            reportsToSuperAdminMaster.setStatus(reportsToSuperAdminMasterRequest.getStatus());
            this.reportsToSuperAdminMasterRepository.save(reportsToSuperAdminMaster);
            mainResponse.setMessage("Feedback send to the teacher");
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
    public List<ReportsToSuperAdminResponse> getAllTeacherReports() {

        List<ReportsToSuperAdminMaster> reportsToSuperAdminMasters1 = this.reportsToSuperAdminMasterRepository.findAll();
        List<ReportsToSuperAdminMaster> reportsToSuperAdminMasters = reportsToSuperAdminMasters1.stream().filter(e->e.getTeacherId()!=null).collect(Collectors.toList());
        List<ReportsToSuperAdminMaster> filterList = reportsToSuperAdminMasters.stream()
                .filter(e -> e.getStatus() != null && !e.getStatus().equals("reply"))
                .sorted(Comparator.comparing(ReportsToSuperAdminMaster::getReportsToSuperAdminId).reversed())
                .collect(Collectors.toList());

        List<ReportsToSuperAdminResponse> reportsToSuperAdminResponses = new ArrayList<>();

        for (ReportsToSuperAdminMaster reportsToSuperAdminMaster : filterList) {
            ReportsToSuperAdminResponse reportsToSuperAdminResponse = new ReportsToSuperAdminResponse();
            reportsToSuperAdminResponse.setReportsToSuperAdminId(reportsToSuperAdminMaster.getReportsToSuperAdminId());
            User sender = this.userRepository.findById(reportsToSuperAdminMaster.getSenderId()).get();
            reportsToSuperAdminResponse.setUserId(reportsToSuperAdminMaster.getSenderId());
            String name = sender.getFirstName()+" "+sender.getLastName();
            reportsToSuperAdminResponse.setUserName(name);
            reportsToSuperAdminResponse.setUserAvatar(sender.getProfilePicture());
            reportsToSuperAdminResponse.setPriority(reportsToSuperAdminMaster.getPriority());
            reportsToSuperAdminResponse.setStatus(reportsToSuperAdminMaster.getStatus());
            reportsToSuperAdminResponse.setDate(reportsToSuperAdminMaster.getDate());
            reportsToSuperAdminResponse.setMessage(reportsToSuperAdminMaster.getDescription());

            if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_ADMIN")){
                reportsToSuperAdminResponse.setUserType("Admin");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_STUDENT")){
                reportsToSuperAdminResponse.setUserType("Student");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_TEACHER")) {
                reportsToSuperAdminResponse.setUserType("Teacher");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_INSTITUTE")) {
                reportsToSuperAdminResponse.setUserType("Institute");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_PARENT")) {
                reportsToSuperAdminResponse.setUserType("Parent");
            } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_SUPER_ADMIN")) {
                reportsToSuperAdminResponse.setUserType("SuperAdmin");
            }

            reportsToSuperAdminResponses.add(reportsToSuperAdminResponse);
        }

        return reportsToSuperAdminResponses;
    }

    @Override
    public List<ReportsToSuperAdminResponse> getAllTeacherReportsById(Long id) {
         User teacher = userRepository.findById(id).orElseThrow(()->new RuntimeException("user not found"));

        return Collections.emptyList();
    }

    @Override
    public ReportsToSuperAdminResponse readStudentReport(Integer reportsToSuperAdminId) {
        ReportsToSuperAdminMaster reportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.findById(reportsToSuperAdminId).orElseThrow(()->new RuntimeException("Reports not found"));
        reportsToSuperAdminMaster.setStatus("read");
        try {
            this.reportsToSuperAdminMasterRepository.save(reportsToSuperAdminMaster);
        }catch (Exception e){
            e.printStackTrace();
        }
        User sender = this.userRepository.findById(reportsToSuperAdminMaster.getSenderId()).get();
        ReportsToSuperAdminResponse  response = new ReportsToSuperAdminResponse();
        response.setUserId(reportsToSuperAdminMaster.getSenderId());
        String name = sender.getFirstName()+" "+sender.getLastName();
        response.setReportsToSuperAdminId(reportsToSuperAdminMaster.getReportsToSuperAdminId());
        response.setUserName(name);
        response.setUserAvatar(sender.getProfilePicture());
        response.setPriority(reportsToSuperAdminMaster.getPriority());
        response.setStatus(reportsToSuperAdminMaster.getStatus());
        response.setDate(reportsToSuperAdminMaster.getDate());
        response.setMessage(reportsToSuperAdminMaster.getDescription());
        response.setUserType(reportsToSuperAdminMaster.getType());

        return response;
    }

    @Override
    public MainResponse replyToStudent(ReportsToSuperAdminMasterRequest reportsToSuperAdminMasterRequest) {
        MainResponse mainResponse = new MainResponse();
        ReportsToSuperAdminMaster reportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.findById(reportsToSuperAdminMasterRequest.getReportsToSuperAdminId()).get();

        try {
            ReportsToSuperAdminMaster master = new ReportsToSuperAdminMaster();
            master.setReplyReportsToSuperAdminId(reportsToSuperAdminMaster.getReportsToSuperAdminId());
            master.setDate(new Date());
            master.setReceiverId(reportsToSuperAdminMaster.getSenderId());
            master.setStatus("reply");
            master.setDescription(reportsToSuperAdminMasterRequest.getDescription());
            master.setAttachments(reportsToSuperAdminMasterRequest.getAttachments());
            master.setTeacherId(reportsToSuperAdminMaster.getTeacherId());
            this.reportsToSuperAdminMasterRepository.save(master);
            mainResponse.setMessage("Reply send to the student");
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
    public ReplyReportsToSuperAdminResponses getAllStudentReply(Integer reportsToSuperAdminId) {
        ReplyReportsToSuperAdminResponses reportsToSuperAdminResponses = new ReplyReportsToSuperAdminResponses();
        ReportsToSuperAdminMaster reportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.findById(reportsToSuperAdminId).get();

        reportsToSuperAdminResponses.setReportsToSuperAdminId(reportsToSuperAdminMaster.getReportsToSuperAdminId());
        User sender = this.userRepository.findById(reportsToSuperAdminMaster.getSenderId()).get();
        reportsToSuperAdminResponses.setUserId(reportsToSuperAdminMaster.getSenderId());
        String name = sender.getFirstName()+" "+sender.getLastName();
        reportsToSuperAdminResponses.setUserName(name);
        reportsToSuperAdminResponses.setUserAvatar(sender.getProfilePicture());
        reportsToSuperAdminResponses.setPriority(reportsToSuperAdminMaster.getPriority());
        reportsToSuperAdminResponses.setStatus(reportsToSuperAdminMaster.getStatus());
        reportsToSuperAdminResponses.setDate(reportsToSuperAdminMaster.getDate());
        reportsToSuperAdminResponses.setAttachments(reportsToSuperAdminMaster.getAttachments());
        reportsToSuperAdminResponses.setMessage(reportsToSuperAdminMaster.getDescription());
        reportsToSuperAdminResponses.setEmail(sender.getEmail());

        if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_ADMIN")){
            reportsToSuperAdminResponses.setUserType("Admin");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_STUDENT")){
            reportsToSuperAdminResponses.setUserType("Student");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_TEACHER")) {
            reportsToSuperAdminResponses.setUserType("Teacher");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_INSTITUTE")) {
            reportsToSuperAdminResponses.setUserType("Institute");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_PARENT")) {
            reportsToSuperAdminResponses.setUserType("Parent");
        } else if (sender.getRoles().stream().findFirst().get().getName().name().equals("ROLE_SUPER_ADMIN")) {
            reportsToSuperAdminResponses.setUserType("SuperAdmin");
        }

        ReportsToSuperAdminMaster replyReportsToSuperAdminMaster = this.reportsToSuperAdminMasterRepository.getReply(reportsToSuperAdminId);
        if (replyReportsToSuperAdminMaster!=null){
            ReplyReportsToSuperAdminResponse replyReportsToSuperAdminResponse = new ReplyReportsToSuperAdminResponse();
            replyReportsToSuperAdminResponse.setReportsToSuperAdminId(replyReportsToSuperAdminMaster.getReportsToSuperAdminId());
            replyReportsToSuperAdminResponse.setStatus(replyReportsToSuperAdminMaster.getStatus());
            replyReportsToSuperAdminResponse.setMessage(replyReportsToSuperAdminMaster.getDescription());
            replyReportsToSuperAdminResponse.setDate(replyReportsToSuperAdminMaster.getDate());
            replyReportsToSuperAdminResponse.setAttachments(replyReportsToSuperAdminMaster.getAttachments());

            reportsToSuperAdminResponses.setReply(replyReportsToSuperAdminResponse);
        }

        return reportsToSuperAdminResponses;
    }


}
