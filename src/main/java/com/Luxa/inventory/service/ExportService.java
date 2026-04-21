package com.Luxa.inventory.service;

import com.Luxa.inventory.model.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    private final ProductService productService;

    public ExportService(ProductService productService) {
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public byte[] exportProductsToExcel() {
        List<Product> products = productService.findAll();
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Products");
            
            // Create a bold header style
            CellStyle bold = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            bold.setFont(font);

            // Define Headers
            Row header = sheet.createRow(0);
            String[] cols = {"ID", "Name", "Category", "Price", "Quantity", "Created At"};
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(bold);
            }

            // Fill Data
            int r = 1;
            for (Product p : products) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0L);
                row.createCell(1).setCellValue(p.getName() != null ? p.getName() : "");
                row.createCell(2).setCellValue(p.getCategory() != null ? p.getCategory() : "");
                row.createCell(3).setCellValue(p.getPrice() != null ? p.getPrice().doubleValue() : 0.0);
                row.createCell(4).setCellValue((double) p.getQuantity());
                row.createCell(5).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            }

            // Auto-size columns for a clean look
            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Excel export failed", e);
        }
    }
}
