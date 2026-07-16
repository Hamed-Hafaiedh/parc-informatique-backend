package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.dto.ImportErrorDTO;
import com.tunisiecables.parc_informatique.dto.ImportResultDTO;
import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.entity.Employe;
import com.tunisiecables.parc_informatique.entity.Materiel;
import com.tunisiecables.parc_informatique.enums.StatutAffectation;
import com.tunisiecables.parc_informatique.repository.AffectationRepository;
import com.tunisiecables.parc_informatique.repository.EmployeRepository;
import com.tunisiecables.parc_informatique.repository.MaterielRepository;
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
public class AffectationImportService {

    private final AffectationRepository affectationRepository;
    private final MaterielRepository materielRepository;
    private final EmployeRepository employeRepository;

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
        String matriculeRaw = getCellString(row, 1);
        String groupeRaw = getCellString(row, 2);
        String dateDebutRaw = getCellString(row, 3);
        String statutRaw = getCellString(row, 4);

        if (numeroDeSerieRaw == null || numeroDeSerieRaw.isBlank()) {
            throw new ImportLineException("NumeroDeSerie manquant (colonne A)");
        }
        final String numeroDeSerie = numeroDeSerieRaw.trim();

        Materiel materiel = materielRepository.findAll().stream()
                .filter(m -> m.getNumeroDeSerie().equalsIgnoreCase(numeroDeSerie))
                .findFirst()
                .orElseThrow(() -> new ImportLineException(
                        "Materiel introuvable pour NumeroDeSerie : " + numeroDeSerie));

        Employe employe = null;
        String groupeUtilisateurs = null;

        if (matriculeRaw != null && !matriculeRaw.isBlank()) {
            final String matricule = matriculeRaw.trim();
            employe = employeRepository.findAll().stream()
                    .filter(e -> matricule.equalsIgnoreCase(e.getMatricule()))
                    .findFirst()
                    .orElseThrow(() -> new ImportLineException(
                            "Employe introuvable pour Matricule : " + matricule
                                    + " (importez Employes.xlsx d'abord)"));
        } else if (groupeRaw != null && !groupeRaw.isBlank()) {
            groupeUtilisateurs = groupeRaw.trim();
        } else {
            throw new ImportLineException(
                    "Ni Matricule ni GroupeUtilisateurs renseignes (colonnes B ou C)");
        }

        LocalDate dateDebut = parseDate(dateDebutRaw);
        StatutAffectation statut = normalizeStatut(statutRaw);

        Affectation affectation = new Affectation();
        affectation.setMateriel(materiel);
        affectation.setEmploye(employe);
        affectation.setGroupeUtilisateurs(groupeUtilisateurs);
        affectation.setDateDebut(dateDebut);
        affectation.setStatut(statut);

        affectationRepository.save(affectation);
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return LocalDate.now();
        try {
            return LocalDate.parse(raw.trim());
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private StatutAffectation normalizeStatut(String raw) {
        if (raw == null || raw.isBlank()) return StatutAffectation.ACTIVE;
        String cleaned = raw.trim().toUpperCase();
        try {
            return StatutAffectation.valueOf(cleaned);
        } catch (IllegalArgumentException e) {
            return StatutAffectation.ACTIVE;
        }
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
        for (int i = 0; i < 5; i++) {
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