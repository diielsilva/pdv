package com.todev.pdv.security.utils.contracts;

import jakarta.servlet.http.HttpServletResponse;

public interface ResponseUtil {
    void sendResponse(HttpServletResponse response, Integer status, Object body);
}
