package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Imprimante;
import com.tunisiecables.parc_informatique.entity.Materiel;
import com.tunisiecables.parc_informatique.entity.Ordinateur;
import com.tunisiecables.parc_informatique.repository.MaterielRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterielExportService {

    private final MaterielRepository materielRepository;

    public byte[] exportToExcel() {
        List<Materiel> materiels = materielRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historique Materiels");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                    "Type", "NumeroDeSerie", "Marque", "Modele", "Statut",
                    "SystemeExploitation", "Processeur", "SsdVerifie",
                    "NombrePagesImprimees", "TypeEncre",
                    "Categorie", "Site", "Description",
                    "Archive", "DateArchivage"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Materiel m : materiels) {
                Row row = sheet.createRow(rowIdx++);

                boolean isOrdinateur = m instanceof Ordinateur;
                Ordinateur ord = isOrdinateur ? (Ordinateur) m : null;
                Imprimante imp = (!isOrdinateur && m instanceof Imprimante) ? (Imprimante) m : null;

                row.createCell(0).setCellValue(isOrdinateur ? "ORDINATEUR" : "IMPRIMANTE");
                row.createCell(1).setCellValue(nullToEmpty(m.getNumeroDeSerie()));
                row.createCell(2).setCellValue(nullToEmpty(m.getMarque()));
                row.createCell(3).setCellValue(nullToEmpty(m.getModele()));
                row.createCell(4).setCellValue(m.getStatut() != null ? m.getStatut().toString() : "");
                row.createCell(5).setCellValue(ord != null ? nullToEmpty(ord.getSystemeExploitation()) : "");
                row.createCell(6).setCellValue(ord != null ? nullToEmpty(ord.getProcesseur()) : "");
                row.createCell(7).setCellValue(
                        ord != null && ord.getSsdVerifie() != null && ord.getSsdVerifie() ? "OUI" : "NON");
                row.createCell(8).setCellValue(
                        imp != null && imp.getNombrePagesImprimees() != null
                                ? imp.getNombrePagesImprimees() : 0);
                row.createCell(9).setCellValue(imp != null ? nullToEmpty(imp.getTypeEncre()) : "");
                row.createCell(10).setCellValue(m.getCategorie() != null ? nullToEmpty(m.getCategorie().getNom()) : "");
                row.createCell(11).setCellValue(m.getSite() != null ? nullToEmpty(m.getSite().getNom()) : "");
                row.createCell(12).setCellValue(nullToEmpty(m.getDescription()));
                row.createCell(13).setCellValue(m.isArchive() ? "OUI" : "NON");
                row.createCell(14).setCellValue(
                        m.getDateArchivage() != null ? m.getDateArchivage().toString() : "");
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