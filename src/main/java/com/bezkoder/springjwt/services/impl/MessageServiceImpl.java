package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.MessageMaster;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.MessageRequest;
import com.bezkoder.springjwt.payload.response.AllMessagesResponse;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.repository.MessageMasterRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMasterRepository messageMasterRepository;

    @Autowired
    private UserRepository userRepository;

    // -------------------------------------------------
    // ROLE RESOLVER (REPLACES getRoleById ❌)
    // -------------------------------------------------
    private String resolvePrimaryRole(Long userId) {

        List<ERole> roles = userRepository.getRolesByUserId(userId);

        if (roles == null || roles.isEmpty()) {
            return "UNKNOWN";
        }

        // 🔒 Priority order (VERY IMPORTANT)
        if (roles.contains(ERole.ROLE_SUPER_ADMIN)) return "super_admin";
        if (roles.contains(ERole.ROLE_ADMIN))       return "admin";
        if (roles.contains(ERole.ROLE_INSTITUTE))   return "institute";
        if (roles.contains(ERole.ROLE_TEACHER))     return "teacher";
        if (roles.contains(ERole.ROLE_PARENT))      return "parent";
        if (roles.contains(ERole.ROLE_STUDENT))     return "student";

        return "UNKNOWN";
    }

    // -------------------------------------------------
    // SEND MESSAGE
    // -------------------------------------------------
    @Override
    public MainResponse sendMessage(MessageRequest request) {

        try {
            User sender = userRepository.findById(request.getSenderId())
                    .orElseThrow(() ->
                            new Apierrorr("User with ID " + request.getSenderId() + " not found", "400"));

            Long receiver;

            if ("teacher".equalsIgnoreCase(request.getRole())) {

                if (request.getRecipientIds() == null) {
                    throw new Apierrorr("Recipient id must be provided for teacher", "400");
                }
                receiver = request.getRecipientIds();

            } else if ("parent".equalsIgnoreCase(request.getRole())) {

                receiver = userRepository.findTeacherIdByParentId(request.getSenderId());
                if (receiver == null) {
                    throw new Apierrorr("No teacher found for parent", "404");
                }

            } else if ("student".equalsIgnoreCase(request.getRole())) {

                receiver = userRepository.findTeacherIdByStudentId(request.getSenderId());
                if (receiver == null) {
                    throw new Apierrorr("No teacher found for student", "404");
                }

            } else {
                throw new Apierrorr("Invalid role provided", "400");
            }

            if ("reply".equalsIgnoreCase(request.getType())) {
                Optional<MessageMaster> msg =
                        messageMasterRepository.findById(request.getReplyToMessageId());
                msg.ifPresent(m -> m.setStatus("read"));
            }

            MessageMaster master = new MessageMaster();
            master.setSenderId(request.getSenderId());
            master.setRecipientIds(receiver);
            master.setDescription(request.getDescription());
            master.setAttachments(request.getAttachments());
            master.setCreatedAt(new Date());
            master.setPriority(request.getPriority());
            master.setTitle(request.getTitle());
            master.setStatus(request.getStatus());
            master.setType(request.getType());

            if ("reply".equalsIgnoreCase(request.getType())) {
                if (request.getReplyToMessageId() == null) {
                    throw new Apierrorr("replyToMessageId is required", "400");
                }
                master.setReplyToMessageId(request.getReplyToMessageId());
            }

            messageMasterRepository.save(master);

            return new MainResponse("Message sent successfully",
                    HttpStatus.OK.value(), true);

        } catch (Apierrorr e) {
            return new MainResponse(e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(), false);
        } catch (Exception e) {
            return new MainResponse("Internal error: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), false);
        }
    }

    // -------------------------------------------------
    // GET MESSAGES (FIXED)
    // -------------------------------------------------
    @Override
    public List<AllMessagesResponse> getMsgForParent(Long userId, String role) {

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with ID: " + userId));

        List<MessageMaster> messages =
                messageMasterRepository.findBySenderIdOrRecipientIds(userId, userId);

        List<AllMessagesResponse> responseList = new ArrayList<>();

        for (MessageMaster msg : messages) {

            if (!"message".equalsIgnoreCase(msg.getType())) continue;

            AllMessagesResponse response = new AllMessagesResponse();
            response.setId(msg.getId());
            response.setUserId(msg.getSenderId());
            response.setMessage(msg.getDescription());
            response.setTitle(msg.getTitle());
            response.setDate(msg.getCreatedAt());
            response.setAttachments(msg.getAttachments());
            response.setPriority(msg.getPriority());
            response.setStatus(msg.getStatus());
            response.setRecipientIds(msg.getRecipientIds());
            response.setReplyToMessageId(msg.getReplyToMessageId());

            // ✅ FIXED ROLE HANDLING
            response.setUserType(resolvePrimaryRole(msg.getSenderId()));

            User sender = userRepository.findById(msg.getSenderId()).orElse(null);
            if (sender != null) {
                response.setUserName(sender.getFirstName() + " " + sender.getLastName());
            }

            MessageMaster replyMaster =
                    messageMasterRepository.findByReplyToMessageId(msg.getId());

            if (replyMaster != null) {

                AllMessagesResponse reply = new AllMessagesResponse();
                reply.setUserId(replyMaster.getSenderId());
                reply.setUserType(resolvePrimaryRole(replyMaster.getSenderId()));
                reply.setMessage(replyMaster.getDescription());
                reply.setTitle(replyMaster.getTitle());
                reply.setDate(replyMaster.getCreatedAt());
                reply.setAttachments(replyMaster.getAttachments());
                reply.setPriority(replyMaster.getPriority());
                reply.setStatus(replyMaster.getStatus());
                reply.setRecipientIds(replyMaster.getRecipientIds());

                User replyUser =
                        userRepository.findById(replyMaster.getSenderId()).orElse(null);
                if (replyUser != null) {
                    reply.setUserName(replyUser.getUsername());
                }

                response.setReply(reply);
            }

            responseList.add(response);
        }

        return responseList;
    }
}
