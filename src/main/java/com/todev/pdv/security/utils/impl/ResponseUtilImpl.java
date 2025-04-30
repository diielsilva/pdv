package com.todev.pdv.security.utils.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todev.pdv.security.utils.contracts.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResponseUtilImpl implements ResponseUtil {
    private final ObjectMapper objectMapper;

    public ResponseUtilImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendResponse(HttpServletResponse response, Integer status, Object body) {
        try {
            response.setContentType("application/json");
            response.setStatus(status);
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
            response.getWriter().flush();
        } catch (IOException exception) {
        }
    }
}
