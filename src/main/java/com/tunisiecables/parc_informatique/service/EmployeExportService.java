package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Employe;
import com.tunisiecables.parc_informatique.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeExportService {

    private final EmployeRepository employeRepository;

    public byte[] exportToExcel() {
        List<Employe> employes = employeRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employes");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                    "Matricule", "Nom", "Prenom", "Poste", "Email",
                    "Telephone", "Departement", "Site", "Archive", "DateArchivage"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Employe e : employes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nullToEmpty(e.getMatricule()));
                row.createCell(1).setCellValue(nullToEmpty(e.getNom()));
                row.createCell(2).setCellValue(nullToEmpty(e.getPrenom()));
                row.createCell(3).setCellValue(nullToEmpty(e.getPoste()));
                row.createCell(4).setCellValue(nullToEmpty(e.getEmail()));
                row.createCell(5).setCellValue(nullToEmpty(e.getTelephone()));
                row.createCell(6).setCellValue(nullToEmpty(e.getDepartement()));
                row.createCell(7).setCellValue(e.getSite() != null ? nullToEmpty(e.getSite().getNom()) : "");
                row.createCell(8).setCellValue(e.isArchive() ? "OUI" : "NON");
                row.createCell(9).setCellValue(
                        e.getDateArchivage() != null ? e.getDateArchivage().toString() : "");
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

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}