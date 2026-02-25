package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.MessageMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMasterRepository extends JpaRepository<MessageMaster,Long> {
    List<MessageMaster> findAllBySenderId(Long parentId);

    MessageMaster findByReplyToMessageId(Long id);

    List<MessageMaster> findBySenderIdOrRecipientIds(Long userId, Long userId1);


}
