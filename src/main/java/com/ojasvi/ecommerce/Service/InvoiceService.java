package com.ojasvi.ecommerce.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.OrderItem;

@Service
public class InvoiceService {

    public ByteArrayInputStream generateInvoice(Order order)
            throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.setMargins(30, 30, 30, 30);
        
        PdfFont boldFont =
                PdfFontFactory.createFont(
                        StandardFonts.HELVETICA_BOLD);

        // ===========================
        // COMPANY HEADER
        // ===========================

        Paragraph companyName = new Paragraph("OJASVI")
        		.setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(companyName);

        Paragraph companyDetails =
                new Paragraph(
                        "Luxury Home Linen\n"
                                + "Pune, Maharashtra, India\n"
                                + "Email : info@ojasvi.com\n"
                                + "Phone : +91 XXXXX XXXXX")
                        .setTextAlignment(TextAlignment.CENTER);

        document.add(companyDetails);

        document.add(new Paragraph("\n"));

        // ===========================
        // INVOICE DETAILS
        // ===========================

        Table invoiceTable =
                new Table(UnitValue.createPercentArray(2));

        invoiceTable.setWidth(
                UnitValue.createPercentValue(100));

        invoiceTable.addCell(createLabelCell("Invoice Number", boldFont));
        invoiceTable.addCell(createValueCell(order.getInvoiceNumber()));

        invoiceTable.addCell(createLabelCell("Order Number", boldFont));
        invoiceTable.addCell(createValueCell(order.getOrderNumber()));

        invoiceTable.addCell(createLabelCell("Order Date", boldFont));
        invoiceTable.addCell(
                createValueCell(
                        order.getCreatedAt().toString()));

        invoiceTable.addCell(createLabelCell("Payment Method", boldFont));
        invoiceTable.addCell(
                createValueCell(
                        order.getPaymentMethod().name()));

        invoiceTable.addCell(createLabelCell("Payment Status", boldFont));
        invoiceTable.addCell(
                createValueCell(
                        order.getPaymentStatus().toString()));

        document.add(invoiceTable);

        document.add(new Paragraph("\n"));

        // ===========================
        // CUSTOMER DETAILS
        // ===========================

        document.add(
                new Paragraph("Bill To")
                .setFont(boldFont)
                        .setFontSize(14));

        document.add(
                new Paragraph(
                        order.getCustomer().getFullName()));

        // Adjust according to your Address entity
        document.add(
                new Paragraph(
                        String.valueOf(
                                order.getShippingAddress())));

        document.add(
                new Paragraph(
                        order.getCustomer().getEmail()));

        document.add(
                new Paragraph(
                        order.getCustomer().getMobile()));

        document.add(new Paragraph("\n"));

        // ===========================
        // PRODUCT TABLE
        // ===========================

        float[] columnWidths = {5, 2, 3, 3};

        Table productTable =
                new Table(
                        UnitValue.createPercentArray(
                                columnWidths));

        productTable.setWidth(
                UnitValue.createPercentValue(100));

        productTable.addHeaderCell(
                createHeaderCell("Product", boldFont));

        productTable.addHeaderCell(
                createHeaderCell("Qty", boldFont));

        productTable.addHeaderCell(
                createHeaderCell("Price", boldFont));

        productTable.addHeaderCell(
                createHeaderCell("Total", boldFont));

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {

            BigDecimal lineTotal =
                    item.getProductPrice().multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity()));

            subtotal = subtotal.add(lineTotal);

            productTable.addCell(
                    createValueCell(
                            item.getProduct()
                                    .getProductName()));

            productTable.addCell(
                    createValueCell(
                            String.valueOf(
                                    item.getQuantity())));

            productTable.addCell(
                    createValueCell(
                            "₹" + item.getProductPrice()));

            productTable.addCell(
                    createValueCell(
                            "₹" + lineTotal));
        }

        document.add(productTable);

        document.add(new Paragraph("\n"));

        // ===========================
        // TOTALS
        // ===========================

        BigDecimal shipping =
                order.getShippingCharge() == null
                        ? BigDecimal.ZERO
                        : order.getShippingCharge();

        BigDecimal discount =
                order.getDiscountAmount() == null
                        ? BigDecimal.ZERO
                        : order.getDiscountAmount();

        Table totalTable =
                new Table(
                        UnitValue.createPercentArray(2));

        totalTable.setWidth(
                UnitValue.createPercentValue(40));

        totalTable.setHorizontalAlignment(
                HorizontalAlignment.RIGHT);

        totalTable.addCell(createLabelCell("Subtotal", boldFont));
        totalTable.addCell(
                createValueCell(
                        "₹" + subtotal));

        totalTable.addCell(createLabelCell("Shipping", boldFont));
        totalTable.addCell(
                createValueCell(
                        "₹" + shipping));

        totalTable.addCell(createLabelCell("Discount", boldFont));
        totalTable.addCell(
                createValueCell(
                        "₹" + discount));

        totalTable.addCell(
                createLabelCell("Grand Total", boldFont));

        totalTable.addCell(
                createValueCell(
                        "₹" + order.getGrandTotal()));

        document.add(totalTable);

        document.add(new Paragraph("\n"));

        // ===========================
        // FOOTER
        // ===========================

        document.add(
                new Paragraph(
                        "Thank you for shopping with OJASVI.")
                .setFont(boldFont)
                        .setTextAlignment(
                                TextAlignment.CENTER));

        document.add(
                new Paragraph(
                        "This is a computer generated invoice.")
                        .setTextAlignment(
                                TextAlignment.CENTER));

        document.close();

        return new ByteArrayInputStream(
                out.toByteArray());
    }

    private Cell createHeaderCell(String value, PdfFont boldFont) {

        return new Cell()
                .add(new Paragraph(value).setFont(boldFont))
                .setBackgroundColor(
                        ColorConstants.LIGHT_GRAY);
    }

    private Cell createLabelCell(String value, PdfFont boldFont) {

        return new Cell()
                .add(new Paragraph(value) .setFont(boldFont));
    }

    private Cell createValueCell(String value) {

        return new Cell()
                .add(new Paragraph(
                        value == null ? "" : value));
    }
}