package com.swp.ihelp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Exception exception = (Exception) httpServletRequest.getAttribute("Exception");

        String message;

        if (exception != null) {
            byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause", exception.toString()));
            httpServletResponse.getOutputStream().write(body);
        } else {
            if (e.getCause() != null) {
                message = e.getCause().toString() + " " + e.getMessage();
            } else {
                message = e.getMessage();
            }

            byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("error", message));
            httpServletResponse.getOutputStream().write(body);
        }
    }
}
