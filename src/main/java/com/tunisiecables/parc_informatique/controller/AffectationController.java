package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.dto.ImportResultDTO;
import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.service.AffectationExportService;
import com.tunisiecables.parc_informatique.service.AffectationImportService;
import com.tunisiecables.parc_informatique.service.AffectationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/affectations")
@RequiredArgsConstructor
public class AffectationController {

    private final AffectationService affectationService;
    private final AffectationImportService affectationImportService;
    private final AffectationExportService affectationExportService;

    @GetMapping
    public ResponseEntity<List<Affectation>> getAllActives() {
        return ResponseEntity.ok(affectationService.findAllActives());
    }

    @GetMapping("/cloturees")
    public ResponseEntity<List<Affectation>> getAllCloturees() {
        return ResponseEntity.ok(affectationService.findAllCloturees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Affectation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(affectationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Affectation> create(@RequestBody Affectation affectation) {
        return ResponseEntity.ok(affectationService.create(affectation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Affectation> update(@PathVariable Long id, @RequestBody Affectation affectation) {
        return ResponseEntity.ok(affectationService.update(id, affectation));
    }

    @PutMapping("/{id}/cloturer")
    public ResponseEntity<Affectation> cloturer(@PathVariable Long id) {
        return ResponseEntity.ok(affectationService.cloturer(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        affectationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResultDTO> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(affectationImportService.importFromExcel(file));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excelBytes = affectationExportService.exportToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"historique_affectations.xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}