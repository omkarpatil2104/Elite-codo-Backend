package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.NotificationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMasterRepository extends JpaRepository<NotificationMaster, Integer> {
    @Query("select n from NotificationMaster as n where n.userId=:id")
    List<NotificationMaster> useridWiseNotifications(@Param("id") Long id);


}
