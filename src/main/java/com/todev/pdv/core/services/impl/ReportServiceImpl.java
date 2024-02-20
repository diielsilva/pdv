package com.todev.pdv.core.services.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.todev.pdv.core.exceptions.FileExportException;
import com.todev.pdv.core.models.Sale;
import com.todev.pdv.core.models.SaleItem;
import com.todev.pdv.core.providers.contracts.ProductProvider;
import com.todev.pdv.core.providers.contracts.SaleItemProvider;
import com.todev.pdv.core.providers.contracts.SaleProvider;
import com.todev.pdv.core.services.contracts.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Rectangle.NO_BORDER;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private static final String TOTAL_MESSAGE = "Total: R$ %.2f";
    private final SaleProvider saleProvider;
    private final SaleItemProvider saleItemProvider;
    private final ProductProvider productProvider;

    @Override
    public void saleReport(Integer id, HttpServletResponse response) {
        try (var pdf = new Document(PageSize.A4)) {
            PdfWriter.getInstance(pdf, response.getOutputStream());
            var sale = saleProvider.findById(id);
            var items = saleItemProvider.findBySaleId(id);
            var total = 0.0;

            pdf.open();

            var pdfTitle = getParagraph("Loja Minha Make", 19);

            pdf.add(pdfTitle);

            for (SaleItem item : items) {
                total += item.getAmount() * item.getPrice();
            }

            pdf.add(getSaleItemsTable(items));

            if (sale.getDiscount() != 0) {
                var percentDiscount = sale.getDiscount() / 10;
                var discount = total * percentDiscount;
                var subTotalParagraph = getParagraph(String.format("Sub-Total: R$ %.2f", total), 14);
                var discountParagraph = getParagraph(String.format("Desconto: R$ %.2f", discount), 14);
                pdf.add(subTotalParagraph);
                pdf.add(discountParagraph);
                total -= discount;
            }

            var totalParagraph = getParagraph(String.format(TOTAL_MESSAGE, total), 14);
            pdf.add(totalParagraph);

        } catch (Exception exception) {
            throw new FileExportException("Não foi possível gerar o PDF da venda!");
        }
    }

    @Override
    public void salesReportByDate(LocalDateTime date, HttpServletResponse response) {
        try (var pdf = new Document(PageSize.A4)) {
            PdfWriter.getInstance(pdf, response.getOutputStream());
            var start = date.withHour(0).withMinute(0).withSecond(0);
            var end = date.withHour(23).withMinute(59).withSecond(59);
            var sales = saleProvider.findActiveByDate(start, end);
            var total = 0.0;

            pdf.open();

            var title = getParagraph("Relatório de Vendas", 19);

            pdf.add(title);

            var salesTable = new PdfPTable(4);
            var idTitle = getCell();
            var discountTitle = getCell();
            var totalTitle = getCell();
            var dateTitle = getCell();

            idTitle.setPhrase(new Phrase("ID"));
            discountTitle.setPhrase(new Phrase("DISC (%)"));
            totalTitle.setPhrase(new Phrase("TOTAL"));
            dateTitle.setPhrase(new Phrase("DATA"));

            salesTable.addCell(idTitle);
            salesTable.addCell(discountTitle);
            salesTable.addCell(totalTitle);
            salesTable.addCell(dateTitle);

            for (Sale sale : sales) {
                var idCell = getCell();
                var discountCell = getCell();
                var totalCell = getCell();
                var dateCell = getCell();

                if (sale.getDiscount() != 0) {
                    var percentDiscount = sale.getDiscount() / 100;
                    var discount = sale.getTotal() * percentDiscount;
                    var totalSale = sale.getTotal() - discount;
                    total += totalSale;
                } else {
                    total += sale.getTotal();
                }

                idCell.setPhrase(new Phrase(sale.getId()));
                discountCell.setPhrase(new Phrase(sale.getDiscount()));
                totalCell.setPhrase(new Phrase(String.format(TOTAL_MESSAGE, sale.getTotal())));
                dateCell.setPhrase(new Phrase(String.format("%s", sale.getCreatedAt())));
            }

            pdf.add(salesTable);

            var totalParagraph = getParagraph(String.format(TOTAL_MESSAGE, total), 14);

            pdf.add(totalParagraph);

        } catch (Exception exception) {
            throw new FileExportException("Não foi possível gerar o PDF das vendas!");
        }
    }

    private PdfPTable getSaleItemsTable(List<SaleItem> items) {
        var saleItemsTable = new PdfPTable(3);
        var descriptionTitle = getCell();
        var amountTitle = getCell();
        var priceTitle = getCell();

        descriptionTitle.setPhrase(new Phrase("DESC"));
        amountTitle.setPhrase(new Phrase("QTD"));
        priceTitle.setPhrase(new Phrase("PREÇO"));

        saleItemsTable.addCell(descriptionTitle);
        saleItemsTable.addCell(amountTitle);
        saleItemsTable.addCell(priceTitle);


        for (SaleItem item : items) {
            var product = productProvider.findById(item.getProductId());
            var description = getCell();
            var amount = getCell();
            var price = getCell();

            description.setPhrase(new Phrase(product.getDescription()));
            amount.setPhrase(new Phrase(item.getAmount()));
            price.setPhrase(new Phrase(String.format("R$ %.2f", item.getPrice())));

            saleItemsTable.addCell(description);
            saleItemsTable.addCell(amount);
            saleItemsTable.addCell(price);

        }

        return saleItemsTable;
    }

    private Paragraph getParagraph(String content, Integer fontSize) {
        var fontStyle = FontFactory.getFont(FontFactory.defaultEncoding);
        fontStyle.setSize(fontSize);
        var paragraph = new Paragraph(content, fontStyle);
        paragraph.setAlignment(ALIGN_CENTER);
        return paragraph;
    }

    private PdfPCell getCell() {
        var cell = new PdfPCell();
        cell.setBorder(NO_BORDER);
        return cell;
    }
}
