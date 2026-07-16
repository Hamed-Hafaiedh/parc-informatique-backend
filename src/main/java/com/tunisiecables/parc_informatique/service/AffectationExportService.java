package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.repository.AffectationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AffectationExportService {

    private final AffectationRepository affectationRepository;

    public byte[] exportToExcel() {
        List<Affectation> affectations = affectationRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historique Affectations");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                    "NumeroDeSerie", "Marque", "Modele", "Employe",
                    "GroupeUtilisateurs", "DateDebut", "DateFin", "Statut", "Remarque"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Affectation a : affectations) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(
                        a.getMateriel() != null ? a.getMateriel().getNumeroDeSerie() : "");
                row.createCell(1).setCellValue(
                        a.getMateriel() != null && a.getMateriel().getMarque() != null
                                ? a.getMateriel().getMarque() : "");
                row.createCell(2).setCellValue(
                        a.getMateriel() != null && a.getMateriel().getModele() != null
                                ? a.getMateriel().getModele() : "");
                row.createCell(3).setCellValue(
                        a.getEmploye() != null
                                ? a.getEmploye().getNom() + " " + a.getEmploye().getPrenom() : "");
                row.createCell(4).setCellValue(
                        a.getGroupeUtilisateurs() != null ? a.getGroupeUtilisateurs() : "");
                row.createCell(5).setCellValue(
                        a.getDateDebut() != null ? a.getDateDebut().toString() : "");
                row.createCell(6).setCellValue(
                        a.getDateFin() != null ? a.getDateFin().toString() : "");
                row.createCell(7).setCellValue(
                        a.getStatut() != null ? a.getStatut().toString() : "");
                row.createCell(8).setCellValue(
                        a.getRemarque() != null ? a.getRemarque() : "");
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