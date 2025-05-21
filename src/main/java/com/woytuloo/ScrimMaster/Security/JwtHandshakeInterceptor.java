package com.woytuloo.ScrimMaster.Security;


import jakarta.servlet.http.Cookie;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtils jwtUtils;
    private final UserDetailsService uds;


    public JwtHandshakeInterceptor(JwtUtils jwtUtils, UserDetailsService uds) {
        this.jwtUtils = jwtUtils;
        this.uds = uds;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                   WebSocketHandler h, Map<String, Object> attrs) {
        if (req instanceof ServletServerHttpRequest servletReq) {
            Cookie[] cookies = servletReq.getServletRequest().getCookies();
            String token = Arrays.stream(Optional.ofNullable(cookies).orElse(new Cookie[0]))
                    .filter(c -> "accessToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (token != null && jwtUtils.validate(token)) {
                String username = jwtUtils.parseClaims(token).getSubject();
                var principal   = uds.loadUserByUsername(username);
                attrs.put("principal", principal);
            }
        }
        return true;
    }
    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception)
    {}
}

