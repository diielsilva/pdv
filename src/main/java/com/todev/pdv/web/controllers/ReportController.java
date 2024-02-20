package com.todev.pdv.web.controllers;

import com.todev.pdv.core.services.contracts.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("sale/{saleId}")
    public void saleReport(@PathVariable Integer saleId, HttpServletResponse response) {
        var headerKey = "Content-Disposition";
        var headerValue = "attachment; filename=report-" + LocalDateTime.now() + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader(headerKey, headerValue);
        reportService.saleReport(saleId, response);
    }

    @GetMapping("sales/by-date")
    public void salesReportByDate(@RequestParam LocalDateTime date, HttpServletResponse response) {
        var headerKey = "Content-Disposition";
        var headerValue = "attachment; filename=report-" + LocalDateTime.now() + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader(headerKey, headerValue);
        reportService.salesReportByDate(date, response);
    }
}
