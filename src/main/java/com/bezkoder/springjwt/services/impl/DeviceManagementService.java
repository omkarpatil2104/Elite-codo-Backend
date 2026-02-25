// src/main/java/com/bezkoder/springjwt/services/impl/DeviceManagementService.java
package com.bezkoder.springjwt.services.impl;

import com.bezkoder.springjwt.dto.DeviceCheckResponse;
import com.bezkoder.springjwt.dto.DeviceInfo;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.repository.DeviceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceManagementService {

    @Autowired
    private DeviceSessionRepository repo;

    /* ---------- policy: one device for everyone except SUPER_ADMIN & PARENT ---------- */
    private boolean allowsMultiple(ERole role) {
        return role == ERole.ROLE_SUPER_ADMIN || role == ERole.ROLE_PARENT;
    }

    /* ---------- API ---------- */

    public boolean canUserLoginFromDevice(Long uid, ERole role, DeviceInfo info) {

        if (allowsMultiple(role)) return true;

        List<DeviceSession> actives = repo.findActiveByUser(uid);
        return actives.stream().anyMatch(s -> s.getDeviceId().equals(info.getDeviceId()))
                || actives.isEmpty();
    }

    public DeviceSession registerUserDevice(Long uid, ERole role, DeviceInfo info) {

        Optional<DeviceSession> existing =
                repo.findActiveByUserAndDevice(uid, info.getDeviceId());

        if (existing.isPresent()) {
            DeviceSession s = existing.get();
            s.setLastActivity(LocalDateTime.now());
            s.setUserAgent(info.getUserAgent());
            return repo.save(s);
        }

        DeviceSession s = new DeviceSession();
        s.setUserId(uid);
        s.setRole(role);
        s.setDeviceId(info.getDeviceId());
        s.setUserAgent(info.getUserAgent());
        s.setIpAddress(info.getIpAddress());
        s.setLoginTime(LocalDateTime.now());
        s.setLastActivity(LocalDateTime.now());
        return repo.save(s);
    }

    public int forceLogoutOtherDevices(Long uid, String currentDid) {
        List<DeviceSession> others = repo.findOtherActive(uid, currentDid);
        others.forEach(s -> { s.setActive(false); s.setLogoutTime(LocalDateTime.now()); });
        repo.saveAll(others);
        return others.size();
    }

    public List<DeviceSession> getUserActiveDevices(Long uid) {
        return repo.findActiveByUser(uid);
    }

    public boolean validateUserSession(Long uid, String did, String ignored) {
        return repo.findActiveByUserAndDevice(uid, did).isPresent();
    }

//    public void logoutUserFromDevice(Long uid, String did) {
//        repo.findActiveByUserAndDevice(uid, did)
//                .ifPresent(s -> { s.setActive(false); s.setLogoutTime(LocalDateTime.now()); repo.save(s); });
//    }

    public Map<String,Object> getDeviceRestrictionsByRole(String roleStr) {
        ERole r     = ERole.valueOf(roleStr);
        boolean multi = allowsMultiple(r);

        // Java 8 – build a mutable HashMap and return it
        Map<String,Object> result = new HashMap<>();
        result.put("role",                  r);
        result.put("deviceLimit",           multi ? -1 : 1);
        result.put("allowsMultipleDevices", multi);
        result.put("description",           multi
                ? "Multiple simultaneous devices allowed"
                : "Only one active device at a time"
        );
        return result;
    }


    /* ---------- maintenance ---------- */
    public void cleanupExpired() {
        List<DeviceSession> expired = repo.findExpired(LocalDateTime.now().minusHours(24));
        expired.forEach(s -> { s.setActive(false); s.setLogoutTime(LocalDateTime.now()); });
        repo.saveAll(expired);
    }

    /**
     * Check if a device is registered for a user
     */
    public DeviceCheckResponse isDeviceRegistered(Long userId, String deviceId) {

        try {
            Optional<DeviceSession> opt = repo.findActiveByUserAndDevice(userId, deviceId);

            if (opt.isPresent()) {
                DeviceSession session = opt.get();

                /* update last-activity */
                session.setLastActivity(LocalDateTime.now());
                repo.save(session);

                return DeviceCheckResponse.builder()
                        .registered(true)
                        .canLogin(true)
                        .message("Device is registered and active")
                        .lastActive(session.getLastActivity())
                        .build();
            }

            /* not found / inactive */
            return DeviceCheckResponse.builder()
                    .registered(false)
                    .canLogin(false)
                    .message("Device is not registered or inactive")
                    .build();

        } catch (Exception ex) {
            // log.error("Error checking device registration", ex);
            return DeviceCheckResponse.builder()
                    .registered(false)
                    .canLogin(false)
                    .message("Error checking device registration")
                    .build();
        }
    }

    public boolean logoutDevice(Long userId, String deviceId) {

        try {
            return repo.findActiveByUserAndDevice(userId, deviceId)
                    .map(s -> {
                        s.setActive(false);
                        s.setLogoutTime(LocalDateTime.now());
                        repo.save(s);
                        return true;
                    })
                    .orElse(false);          // not found or already inactive
        } catch (Exception ex) {
//            log.error("Error logging out device", ex);
            return false;
        }
    }
}
