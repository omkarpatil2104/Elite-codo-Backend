package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.NotificationMaster;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.NotificationMasterRequest;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.payload.response.UserDetails;
import com.bezkoder.springjwt.repository.NotificationMasterRepository;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.NotificationMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationMasterServiceImpl implements NotificationMasterService {
    @Autowired
    private NotificationMasterRepository notificationMasterRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public MainResponse BroadCastSend(NotificationMasterRequest notificationMasterRequest) {
        System.out.println("Notification request = "+notificationMasterRequest);
        MainResponse mainResponse = new MainResponse();

        Boolean flag = false;

        for (String userRole : notificationMasterRequest.getUserType()) {
            if (userRole.equals("admin")){
                List<UserDetails> admins = this.userRepository.getRoleWiseList(ERole.ROLE_ADMIN);
                for (UserDetails admin : admins) {
                    if (admin.getStatus().equals("Active")){
                        NotificationMaster notificationMaster = new NotificationMaster();
                        notificationMaster.setUserId(admin.getId());
                        notificationMaster.setDate(new Date());
                        notificationMaster.setMessage(notificationMasterRequest.getMessage());
                        notificationMaster.setStatus("sent");
                        notificationMaster.setSubject(notificationMasterRequest.getSubject());
                        notificationMaster.setSenderId(notificationMasterRequest.getSenderId());
                        this.notificationMasterRepository.save(notificationMaster);
                        flag = true;
                    }else {
                        flag = false;
                    }
                }
            }else if (userRole.equals("teacher")){
                List<UserDetails> teachers = this.userRepository.getRoleWiseList(ERole.ROLE_TEACHER);
                for (UserDetails teacher : teachers) {
                    if (teacher.getStatus().equals("Active")){
                        NotificationMaster notificationMaster = new NotificationMaster();
                        notificationMaster.setUserId(teacher.getId());
                        notificationMaster.setDate(new Date());
                        notificationMaster.setMessage(notificationMasterRequest.getMessage());
                        notificationMaster.setStatus("sent");
                        notificationMaster.setSubject(notificationMasterRequest.getSubject());
                        notificationMaster.setSenderId(notificationMasterRequest.getSenderId());
                        this.notificationMasterRepository.save(notificationMaster);
                        flag = true;
                    }else {
                        flag = false;
                    }
                }
            }else if (userRole.equals("student")){
                List<UserDetails> students = this.userRepository.getRoleWiseList(ERole.ROLE_STUDENT);
                for (UserDetails student : students) {
                    if (student.getStatus().equals("Active")){
                        NotificationMaster notificationMaster = new NotificationMaster();
                        notificationMaster.setUserId(student.getId());
                        notificationMaster.setDate(new Date());
                        notificationMaster.setMessage(notificationMasterRequest.getMessage());
                        notificationMaster.setStatus("sent");
                        notificationMaster.setSubject(notificationMasterRequest.getSubject());
                        notificationMaster.setSenderId(notificationMasterRequest.getSenderId());
                        this.notificationMasterRepository.save(notificationMaster);
                        flag = true;
                    }else {
                        flag = false;
                    }
                }
            }else if (userRole.equals("parent")){
                List<UserDetails> parents = this.userRepository.getRoleWiseList(ERole.ROLE_PARENT);
                for (UserDetails parent : parents) {
                    if (parent.getStatus().equals("Active")){
                        NotificationMaster notificationMaster = new NotificationMaster();
                        notificationMaster.setUserId(parent.getId());
                        notificationMaster.setDate(new Date());
                        notificationMaster.setMessage(notificationMasterRequest.getMessage());
                        notificationMaster.setStatus("Sent");
                        notificationMaster.setSubject(notificationMasterRequest.getSubject());
                        notificationMaster.setSenderId(notificationMasterRequest.getSenderId());
                        this.notificationMasterRepository.save(notificationMaster);
                        flag = true;
                    }else {
                        flag = false;
                    }
                }
            }

            /*else if (userRole.equals("institution")){
                List<UserDetails> institutes = this.userRepository.getRoleWiseList(ERole.ROLE_INSTITUTE);
                for (UserDetails institute : institutes) {
                    if(institute.getStatus.equals("Active")){
                        NotificationMaster notificationMaster = new NotificationMaster();
                    notificationMaster.setUserId(institute.getId());
                    notificationMaster.setDate(new Date());
                    notificationMaster.setMessage(notificationMasterRequest.getMessage());
                    notificationMaster.setStatus("sent");
                    notificationMaster.setSubject(notificationMasterRequest.getSubject());
                    notificationMaster.setSenderId(notificationMasterRequest.getSenderId());
                    this.notificationMasterRepository.save(notificationMaster);
                    flag = true;
                    }else{
                        flag = false;
                    }
                }
            }*/

        }

        if (Boolean.TRUE.equals(flag)){
            mainResponse.setMessage("Notification send successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }else {
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }

        return mainResponse;
    }

    @Override
    public MainResponse individualSend(NotificationMasterRequest notificationMasterRequest) {
        MainResponse mainResponse = new MainResponse();
        Boolean flag = false;
        for (Long recipient : notificationMasterRequest.getRecipients()) {
            User user = this.userRepository.findById(recipient).orElseThrow(()->new RuntimeException("User not found"));
            NotificationMaster notificationMaster = new NotificationMaster();
            notificationMaster.setUserId(user.getId());
            notificationMaster.setSenderId(notificationMasterRequest.getSenderId());
            notificationMaster.setSubject(notificationMasterRequest.getSubject());
            notificationMaster.setMessage(notificationMasterRequest.getMessage());
            notificationMaster.setStatus("Sent");
            notificationMaster.setDate(new Date());
            try {
                this.notificationMasterRepository.save(notificationMaster);
                flag = true;
            }catch (Exception e){
                e.printStackTrace();
                flag = false;
            }
        }

        if (Boolean.TRUE.equals(flag)){
            mainResponse.setMessage("Notification send successfully");
            mainResponse.setResponseCode(HttpStatus.OK.value());
            mainResponse.setFlag(true);
        }else {
            mainResponse.setMessage("Something went wrong");
            mainResponse.setResponseCode(HttpStatus.BAD_REQUEST.value());
            mainResponse.setFlag(false);
        }
        return mainResponse;

    }

    @Override
    public List<NotificationMaster> useridWiseNotifications(Long id) {
        List<NotificationMaster> notificationMasters = this.notificationMasterRepository.useridWiseNotifications(id);
        return notificationMasters;
    }

    @Override
    public NotificationMaster notificationIdWiseNotification(Integer notificationId) {
        NotificationMaster notificationMaster = this.notificationMasterRepository.findById(notificationId).orElseThrow(()->new RuntimeException("Notification not found"));
        return notificationMaster;
    }
}
