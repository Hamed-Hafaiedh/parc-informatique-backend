package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Maintenance;
import com.tunisiecables.parc_informatique.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceExportService {

    private final MaintenanceRepository maintenanceRepository;

    public byte[] exportToExcel() {
        List<Maintenance> maintenances = maintenanceRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historique Maintenances");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                    "NumeroDeSerie", "Technicien", "DateDeclaration",
                    "DateResolution", "Statut", "Description", "Rapport"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Maintenance m : maintenances) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(
                        m.getMateriel() != null ? m.getMateriel().getNumeroDeSerie() : "");
                row.createCell(1).setCellValue(
                        m.getTechnicien() != null ? m.getTechnicien().getUsername() : "");
                row.createCell(2).setCellValue(
                        m.getDateDeclaration() != null ? m.getDateDeclaration().toString() : "");
                row.createCell(3).setCellValue(
                        m.getDateResolution() != null ? m.getDateResolution().toString() : "");
                row.createCell(4).setCellValue(
                        m.getStatut() != null ? m.getStatut().toString() : "");
                row.createCell(5).setCellValue(
                        m.getDescription() != null ? m.getDescription() : "");
                row.createCell(6).setCellValue(
                        m.getRapport() != null ? m.getRapport() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la generation du fichier Excel", e);
        }
    }
}