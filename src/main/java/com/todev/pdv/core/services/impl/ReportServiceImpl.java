package com.todev.pdv.core.services.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.todev.pdv.core.exceptions.FileExportException;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.providers.contracts.ProductProvider;
import com.todev.pdv.core.providers.contracts.SaleItemProvider;
import com.todev.pdv.core.providers.contracts.SaleProvider;
import com.todev.pdv.core.services.contracts.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.lowagie.text.Element.ALIGN_CENTER;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final SaleProvider saleProvider;
    private final SaleItemProvider saleItemProvider;
    private final ProductProvider productProvider;

    @Override
    public void saleReport(Integer id, HttpServletResponse response) {
        try (var report = new Document(PageSize.B6)) {
            PdfWriter.getInstance(report, response.getOutputStream());
            var sale = saleProvider.findById(id);
            var items = saleItemProvider.findBySaleId(id);

            report.open();

            var reportHeader = createReportHeader("Minha Make", List.of(
                    "Vitória Park Shopping",
                    "Rua Henrique de Holanda - Nº 3000",
                    "(81) 99451-3987",
                    "CNPJ 35.699.902/000010-42"
            ));

            reportHeader.forEach(report::add);

            var table = createTable(3, List.of("PROD", "QTD", "PREÇO"));

            items.forEach(item -> {
                var product = productProvider.findById(item.getProductId());

                var tableCells = createTableCells(List.of(
                        product.getDescription(),
                        item.getAmount().toString(),
                        String.format("R$ %.2f", product.getPrice())
                ));

                tableCells.forEach(table::addCell);

            });

            report.add(table);
            var discount = (double) sale.getDiscount() / 100 * sale.getTotal();

            var reportFooter = createReportFooter(List.of(
                    String.format("Subtotal: R$ %.2f", sale.getTotal()),
                    String.format("Desconto: R$ %.2f", discount),
                    String.format("Total: R$ %.2f", sale.getTotal() - discount)
            ));

            var lastItemOfFooter = reportFooter.get(reportFooter.size() - 1);
            lastItemOfFooter.setSpacingAfter(10);

            reportFooter.forEach(report::add);


        } catch (Exception exception) {
            throw new FileExportException("Não foi possível gerar o PDF da venda!");
        }
    }

    @Override
    public void salesReportByDate(LocalDateTime date, HttpServletResponse response) {
        try (var report = new Document(PageSize.B6)) {
            PdfWriter.getInstance(report, response.getOutputStream());
            var start = date.withHour(0).withMinute(0).withSecond(0);
            var end = date.withHour(23).withMinute(59).withSecond(59);
            var sales = saleProvider.findActiveByDate(start, end);
            var totalOfSales = 0.0;

            report.open();

            var reportHeader = createReportHeader("Relatório de Vendas", List.of());

            reportHeader.forEach(report::add);

            var table = createTable(4, List.of("DESC(%)", "SUBTOTAL", "TOTAL", "DATA"));

            for (Sale sale : sales) {
                var total = sale.getTotal();
                var discount = (double) sale.getDiscount() / 100 * sale.getTotal();

                total = sale.getTotal() - discount;

                totalOfSales += total;

                var tableCells = createTableCells(List.of(
                        sale.getDiscount().toString(),
                        String.format("R$ %.2f", sale.getTotal()),
                        String.format("R$ %.2f", total),
                        sale.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ));

                tableCells.forEach(table::addCell);
            }

            report.add(table);

            var reportFooter = createReportFooter(List.of(String.format("Total: R$ %.2f", totalOfSales)));
            var lastItemOfFooter = reportFooter.get(reportFooter.size() - 1);

            lastItemOfFooter.setSpacingAfter(10);

            reportFooter.forEach(report::add);

        } catch (Exception exception) {
            throw new FileExportException("Não foi possível gerar o PDF das vendas!");
        }
    }

    private List<Paragraph> createReportHeader(String titleContent, List<String> subtitles) {
        List<Paragraph> paragraphs = new ArrayList<>();
        var title = createParagraph(titleContent, 22);
        title.getFont().setStyle("bold");
        paragraphs.add(title);
        subtitles.forEach(subtitle -> paragraphs.add(createParagraph(subtitle, 16)));
        return paragraphs;
    }

    private List<Paragraph> createReportFooter(List<String> contents) {
        List<Paragraph> paragraphs = new ArrayList<>();
        contents.forEach(content -> paragraphs.add(createParagraph(content, 16)));
        return paragraphs;
    }

    private Paragraph createParagraph(String content, Integer fontSize) {
        var fontStyle = FontFactory.getFont(FontFactory.defaultEncoding);
        fontStyle.setSize(fontSize);
        var paragraph = new Paragraph(content, fontStyle);
        paragraph.setAlignment(ALIGN_CENTER);
        return paragraph;
    }

    private PdfPTable createTable(int columns, List<String> headers) {
        var table = new PdfPTable(columns);
        table.setSpacingBefore(15);
        table.setSpacingAfter(15);
        headers.forEach(title -> {
            var header = new PdfPCell();
            header.setBorder(0);
            header.setPadding(6);
            header.setPhrase(new Phrase(title));
            header.getPhrase().getFont().setStyle("bold");
            table.addCell(header);
        });
        return table;
    }

    private List<PdfPCell> createTableCells(List<String> contents) {
        List<PdfPCell> cells = new ArrayList<>();
        contents.forEach(content -> {
            var cell = new PdfPCell();
            cell.setBorder(0);
            cell.setPadding(6);
            cell.setPhrase(new Phrase(content));
            cells.add(cell);
        });
        return cells;
    }
}
