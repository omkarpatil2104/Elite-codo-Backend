// src/main/java/com/bezkoder/springjwt/repository/DeviceSessionRepository.java
package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.dto.DeviceInfo;
import com.bezkoder.springjwt.models.DeviceSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface DeviceSessionRepository extends JpaRepository<DeviceSession,Long> {

    Optional<DeviceInfo> findByUserIdAndDeviceId(String userId, String deviceId);

    @Query("SELECT ds FROM DeviceSession ds WHERE ds.userId = :uid AND ds.active = true")
    List<DeviceSession> findActiveByUser(@Param("uid") Long userId);

    @Query("SELECT ds FROM DeviceSession ds WHERE ds.userId = :uid AND ds.deviceId = :did AND ds.active = true")
    Optional<DeviceSession> findActiveByUserAndDevice(@Param("uid") Long uid, @Param("did") String deviceId);

    @Query("SELECT ds FROM DeviceSession ds WHERE ds.userId = :uid AND ds.deviceId <> :did AND ds.active = true")
    List<DeviceSession> findOtherActive(@Param("uid") Long uid, @Param("did") String did);

    @Query("SELECT ds FROM DeviceSession ds WHERE ds.active = true AND ds.lastActivity < :cutoff")
    List<DeviceSession> findExpired(@Param("cutoff") LocalDateTime cutoff);
}
