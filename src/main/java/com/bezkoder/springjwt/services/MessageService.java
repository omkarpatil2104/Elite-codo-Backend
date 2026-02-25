package com.bezkoder.springjwt.services;


import com.bezkoder.springjwt.payload.request.MessageRequest;
import com.bezkoder.springjwt.payload.response.AllMessagesResponse;
import com.bezkoder.springjwt.payload.response.MainResponse;

import java.util.List;

public interface MessageService {
    MainResponse sendMessage(MessageRequest request);

    List<AllMessagesResponse> getMsgForParent(Long parentId, String role);
}
