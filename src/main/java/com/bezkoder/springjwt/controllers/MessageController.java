package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.request.MessageRequest;
import com.bezkoder.springjwt.payload.response.AllMessagesResponse;
import com.bezkoder.springjwt.payload.response.MainResponse;
import com.bezkoder.springjwt.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest request) {
        MainResponse mainResponse = messageService.sendMessage(request);
        if (Boolean.TRUE.equals(mainResponse.getFlag())) {
            return new ResponseEntity<>(mainResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(mainResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllMsgPar/{id}")
    public  ResponseEntity<?> getAllMsgForParents(@PathVariable("id") Long userId,@RequestParam String role){
        List<AllMessagesResponse> allMessagesResponse = messageService.getMsgForParent(userId,role);
        return  new ResponseEntity<>(allMessagesResponse,HttpStatus.OK);

    }
}
