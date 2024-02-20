package com.todev.pdv.core.services.contracts;

import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

public interface ReportService {
    void saleReport(Integer id, HttpServletResponse response);

    void salesReportByDate(LocalDateTime date, HttpServletResponse response);
}
