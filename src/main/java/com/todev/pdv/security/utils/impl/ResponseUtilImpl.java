package com.todev.pdv.security.utils.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todev.pdv.security.utils.contracts.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResponseUtilImpl implements ResponseUtil {
    private final ObjectMapper objectMapper;

    @Override
    public void sendResponse(HttpServletResponse response, Integer status, Object body) {
        try {
            response.setContentType("application/json");
            response.setStatus(status);
            response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
            response.getWriter().flush();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }
}
