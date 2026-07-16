package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.dto.ImportErrorDTO;
import com.tunisiecables.parc_informatique.dto.ImportResultDTO;
import com.tunisiecables.parc_informatique.entity.Maintenance;
import com.tunisiecables.parc_informatique.entity.Materiel;
import com.tunisiecables.parc_informatique.entity.User;
import com.tunisiecables.parc_informatique.enums.StatutMaintenance;
import com.tunisiecables.parc_informatique.repository.MaintenanceRepository;
import com.tunisiecables.parc_informatique.repository.MaterielRepository;
import com.tunisiecables.parc_informatique.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceImportService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaterielRepository materielRepository;
    private final UserRepository userRepository;

    public ImportResultDTO importFromExcel(MultipartFile file) {
        List<ImportErrorDTO> erreurs = new ArrayList<>();
        int totalLignes = 0;
        int importees = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                totalLignes++;
                int numeroLigneHumain = rowIndex + 1;

                try {
                    transformAndSaveRow(row);
                    importees++;
                } catch (ImportLineException e) {
                    erreurs.add(new ImportErrorDTO(numeroLigneHumain, e.getMessage()));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Impossible de lire le fichier Excel : " + e.getMessage());
        }

        int rejetees = erreurs.size();
        return new ImportResultDTO(totalLignes, importees, rejetees, 0, 0, erreurs);
    }

    private void transformAndSaveRow(Row row) {
        String numeroDeSerieRaw = getCellString(row, 0);
        String technicienUsernameRaw = getCellString(row, 1);
        String dateDeclarationRaw = getCellString(row, 2);
        String dateResolutionRaw = getCellString(row, 3);
        String statutRaw = getCellString(row, 4);
        String description = getCellString(row, 5);
        String rapport = getCellString(row, 6);

        if (numeroDeSerieRaw == null || numeroDeSerieRaw.isBlank()) {
            throw new ImportLineException("NumeroDeSerie manquant (colonne A)");
        }
        final String numeroDeSerie = numeroDeSerieRaw.trim();

        Materiel materiel = materielRepository.findAll().stream()
                .filter(m -> m.getNumeroDeSerie().equalsIgnoreCase(numeroDeSerie))
                .findFirst()
                .orElseThrow(() -> new ImportLineException(
                        "Materiel introuvable pour NumeroDeSerie : " + numeroDeSerie));

        User technicien = null;
        if (technicienUsernameRaw != null && !technicienUsernameRaw.isBlank()) {
            final String username = technicienUsernameRaw.trim();
            technicien = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ImportLineException(
                            "Technicien introuvable pour username : " + username));
        }

        if (dateDeclarationRaw == null || dateDeclarationRaw.isBlank()) {
            throw new ImportLineException("DateDeclaration manquante (colonne C)");
        }

        LocalDate dateDeclaration;
        try {
            dateDeclaration = LocalDate.parse(dateDeclarationRaw.trim());
        } catch (Exception e) {
            throw new ImportLineException("DateDeclaration invalide (format attendu : AAAA-MM-JJ)");
        }

        LocalDate dateResolution = null;
        if (dateResolutionRaw != null && !dateResolutionRaw.isBlank()) {
            try {
                dateResolution = LocalDate.parse(dateResolutionRaw.trim());
            } catch (Exception e) {
                throw new ImportLineException("DateResolution invalide (format attendu : AAAA-MM-JJ)");
            }
        }

        StatutMaintenance statut = normalizeStatut(statutRaw);

        Maintenance maintenance = new Maintenance();
        maintenance.setMateriel(materiel);
        maintenance.setTechnicien(technicien);
        maintenance.setDateDeclaration(dateDeclaration);
        maintenance.setDateResolution(dateResolution);
        maintenance.setStatut(statut);
        maintenance.setDescription(trimOrNull(description));
        maintenance.setRapport(trimOrNull(rapport));

        maintenanceRepository.save(maintenance);
    }

    private StatutMaintenance normalizeStatut(String raw) {
        if (raw == null || raw.isBlank()) return StatutMaintenance.EN_ATTENTE;
        String cleaned = raw.trim().toUpperCase().replace(" ", "_");
        try {
            return StatutMaintenance.valueOf(cleaned);
        } catch (IllegalArgumentException e) {
            return StatutMaintenance.EN_ATTENTE;
        }
    }

    private String trimOrNull(String raw) {
        return (raw == null || raw.isBlank()) ? null : raw.trim();
    }

    private String getCellString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < 7; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private static class ImportLineException extends RuntimeException {
        public ImportLineException(String message) {
            super(message);
        }
    }
}