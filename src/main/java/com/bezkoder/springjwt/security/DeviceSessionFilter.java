// src/main/java/com/bezkoder/springjwt/security/DeviceSessionFilter.java
package com.bezkoder.springjwt.security;

import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.services.impl.DeviceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

@RequiredArgsConstructor
public class DeviceSessionFilter extends OncePerRequestFilter {

//    private final jwtUtils;
//    private final DeviceManagementService svc;

    private final JwtUtils jwtUtils;
    private final DeviceManagementService svc;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String hdr = req.getHeader("Authorization");
        if (hdr == null || !hdr.startsWith("Bearer ")) { chain.doFilter(req,res); return; }

        String token = hdr.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            res.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid JWT");
            return;
        }

        Long   uid      = Long.parseLong(jwtUtils.getUserNameFromJwtToken(token)); // username == id
        String deviceId = req.getHeader("X-Device-Id");

        if (deviceId == null || deviceId.isEmpty()) {
            res.sendError(HttpStatus.BAD_REQUEST.value(), "Missing X-Device-Id header");
            return;
        }

        if (!svc.validateUserSession(uid, deviceId, "")) {
            res.sendError(HttpStatus.UNAUTHORIZED.value(), "Device session invalid or expired");
            return;
        }
        chain.doFilter(req, res);
    }
}
