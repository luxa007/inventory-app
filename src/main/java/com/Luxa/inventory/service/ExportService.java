package com.Luxa.inventory.service;

import com.Luxa.inventory.model.Product;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Products");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Price");
            header.createCell(4).setCellValue("Quantity");

            int r = 1;
            for (Product p : products) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0);
                row.createCell(1).setCellValue(p.getName());
                row.createCell(2).setCellValue(p.getCategory());
                row.createCell(3).setCellValue(p.getPrice() != null ? p.getPrice().doubleValue() : 0);
                row.createCell(4).setCellValue(p.getQuantity() != null ? p.getQuantity() : 0);
            }
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build Excel export", e);
        }
    }
}
