package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.dto.ImportErrorDTO;
import com.tunisiecables.parc_informatique.dto.ImportResultDTO;
import com.tunisiecables.parc_informatique.entity.*;
import com.tunisiecables.parc_informatique.enums.StatutMateriel;
import com.tunisiecables.parc_informatique.repository.*;
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
public class MaterielImportService {

    private final MaterielRepository materielRepository;
    private final CategorieRepository categorieRepository;
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
                    Materiel materiel = transformRow(row);
                    materielRepository.save(materiel);
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

    // ===================== TRANSFORM =====================

    private Materiel transformRow(Row row) {

        String typeRaw = getCellString(row, 0);
        String numeroDeSerieRaw = getCellString(row, 1);
        String marque = getCellString(row, 2);
        String modele = getCellString(row, 3);
        String statutRaw = getCellString(row, 4);
        String systemeExploitation = getCellString(row, 5);
        String processeur = getCellString(row, 6);
        String ssdRaw = getCellString(row, 7);
        String nombrePagesRaw = getCellString(row, 8);
        String typeEncre = getCellString(row, 9);
        String categorieNom = getCellString(row, 10);
        String siteNom = getCellString(row, 11);
        String description = getCellString(row, 12);

        if (numeroDeSerieRaw == null || numeroDeSerieRaw.isBlank()) {
            throw new ImportLineException("NumeroDeSerie manquant (colonne B)");
        }
        final String numeroDeSerie = numeroDeSerieRaw.trim();

        if (materielRepository.findAll().stream()
                .anyMatch(m -> m.getNumeroDeSerie().equalsIgnoreCase(numeroDeSerie))) {
            throw new ImportLineException("NumeroDeSerie deja existant : " + numeroDeSerie);
        }

        String typeNormalise = normalizeType(typeRaw);
        if (typeNormalise == null) {
            throw new ImportLineException(
                    "Type invalide : '" + typeRaw + "' (attendu ORDINATEUR ou IMPRIMANTE)");
        }

        StatutMateriel statut = normalizeStatut(statutRaw);

        Categorie categorie = null;
        if (categorieNom != null && !categorieNom.isBlank()) {
            categorie = resolveOrCreateCategorie(categorieNom.trim());
        }

        Site site = null;
        if (siteNom != null && !siteNom.isBlank()) {
            site = resolveOrCreateSite(siteNom.trim());
        }

        if (typeNormalise.equals("ORDINATEUR")) {
            Ordinateur ordinateur = new Ordinateur();
            ordinateur.setNumeroDeSerie(numeroDeSerie);
            ordinateur.setMarque(trimOrNull(marque));
            ordinateur.setModele(trimOrNull(modele));
            ordinateur.setStatut(statut);
            ordinateur.setDescription(trimOrNull(description));
            ordinateur.setCategorie(categorie);
            ordinateur.setSite(site);
            ordinateur.setSystemeExploitation(trimOrNull(systemeExploitation));
            ordinateur.setProcesseur(trimOrNull(processeur));
            ordinateur.setSsdVerifie(parseBoolean(ssdRaw));
            return ordinateur;
        } else {
            Imprimante imprimante = new Imprimante();
            imprimante.setNumeroDeSerie(numeroDeSerie);
            imprimante.setMarque(trimOrNull(marque));
            imprimante.setModele(trimOrNull(modele));
            imprimante.setStatut(statut);
            imprimante.setDescription(trimOrNull(description));
            imprimante.setCategorie(categorie);
            imprimante.setSite(site);
            imprimante.setNombrePagesImprimees(parseInteger(nombrePagesRaw));
            imprimante.setTypeEncre(trimOrNull(typeEncre));
            return imprimante;
        }
    }

    // ===================== HELPERS DE NORMALISATION =====================

    private String normalizeType(String raw) {
        if (raw == null) return null;
        String cleaned = raw.trim().toUpperCase();
        if (cleaned.equals("ORDINATEUR") || cleaned.equals("PC")
                || cleaned.equals("LAPTOP") || cleaned.equals("PC PORTABLE")
                || cleaned.equals("PC BUREAU") || cleaned.equals("MINI PC")) {
            return "ORDINATEUR";
        }
        if (cleaned.equals("IMPRIMANTE") || cleaned.equals("PRINTER")) {
            return "IMPRIMANTE";
        }
        return null;
    }

    private StatutMateriel normalizeStatut(String raw) {
        if (raw == null || raw.isBlank()) return StatutMateriel.EN_STOCK;
        String cleaned = raw.trim().toUpperCase().replace(" ", "_");
        try {
            return StatutMateriel.valueOf(cleaned);
        } catch (IllegalArgumentException e) {
            return StatutMateriel.EN_STOCK;
        }
    }

    private Categorie resolveOrCreateCategorie(String nom) {
        final String nomFinal = nom;
        return categorieRepository.findAll().stream()
                .filter(c -> c.getNom().equalsIgnoreCase(nomFinal))
                .findFirst()
                .orElseGet(() -> {
                    Categorie nouvelle = new Categorie();
                    nouvelle.setNom(nomFinal);
                    return categorieRepository.save(nouvelle);
                });
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

    private Boolean parseBoolean(String raw) {
        if (raw == null) return false;
        String cleaned = raw.trim().toUpperCase();
        return cleaned.equals("OUI") || cleaned.equals("YES")
                || cleaned.equals("TRUE") || cleaned.equals("1");
    }

    private Integer parseInteger(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return (int) Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            return null;
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
        for (int i = 0; i < 13; i++) {
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