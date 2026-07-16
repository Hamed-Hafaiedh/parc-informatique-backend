package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.dto.ImportErrorDTO;
import com.tunisiecables.parc_informatique.dto.ImportResultDTO;
import com.tunisiecables.parc_informatique.entity.Employe;
import com.tunisiecables.parc_informatique.entity.Site;
import com.tunisiecables.parc_informatique.repository.EmployeRepository;
import com.tunisiecables.parc_informatique.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeImportService {

    private final EmployeRepository employeRepository;
    private final SiteRepository siteRepository;

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
        String matriculeRaw = getCellString(row, 0);
        String nomRaw = getCellString(row, 1);
        String prenomRaw = getCellString(row, 2);
        String poste = getCellString(row, 3);
        String email = getCellString(row, 4);
        String telephone = getCellString(row, 5);
        String departement = getCellString(row, 6);
        String siteNom = getCellString(row, 7);

        if (nomRaw == null || nomRaw.isBlank()) {
            throw new ImportLineException("Nom manquant (colonne B)");
        }
        if (prenomRaw == null || prenomRaw.isBlank()) {
            throw new ImportLineException("Prenom manquant (colonne C)");
        }

        final String nom = nomRaw.trim();
        final String prenom = prenomRaw.trim();

        boolean existeDeja = employeRepository.findAll().stream()
                .anyMatch(e -> e.getNom().equalsIgnoreCase(nom)
                        && e.getPrenom().equalsIgnoreCase(prenom));
        if (existeDeja) {
            throw new ImportLineException("Employe deja existant : " + nom + " " + prenom);
        }

        String matricule = (matriculeRaw != null && !matriculeRaw.isBlank())
                ? matriculeRaw.trim()
                : genererProchainMatricule();

        Site site = null;
        if (siteNom != null && !siteNom.isBlank()) {
            site = resolveOrCreateSite(siteNom.trim());
        }

        Employe employe = new Employe();
        employe.setMatricule(matricule);
        employe.setNom(nom);
        employe.setPrenom(prenom);
        employe.setPoste(trimOrNull(poste));
        employe.setEmail(trimOrNull(email));
        employe.setTelephone(trimOrNull(telephone));
        employe.setDepartement(trimOrNull(departement));
        employe.setSite(site);

        employeRepository.save(employe);
    }

    private String genererProchainMatricule() {
        long next = employeRepository.count() + 1;
        String candidate;
        do {
            candidate = String.format("EMP-%04d", next);
            next++;
        } while (matriculeExiste(candidate));
        return candidate;
    }

    private boolean matriculeExiste(String matricule) {
        return employeRepository.findAll().stream()
                .anyMatch(e -> matricule.equals(e.getMatricule()));
    }

    private Site resolveOrCreateSite(String nom) {
        final String nomFinal = nom;
        return siteRepository.findAll().stream()
                .filter(s -> s.getNom().equalsIgnoreCase(nomFinal))
                .findFirst()
                .orElseGet(() -> {
                    Site nouveau = new Site();
                    nouveau.setNom(nomFinal);
                    return siteRepository.save(nouveau);
                });
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
        for (int i = 0; i < 8; i++) {
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