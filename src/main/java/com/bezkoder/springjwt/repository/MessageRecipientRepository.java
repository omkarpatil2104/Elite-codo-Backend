package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.MessageRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRecipientRepository extends JpaRepository<MessageRecipient,Long> {

    List<MessageRecipient> findByRecipientId(Long recipientId);
    List<MessageRecipient> findByMessageId(Long messageId);
}
