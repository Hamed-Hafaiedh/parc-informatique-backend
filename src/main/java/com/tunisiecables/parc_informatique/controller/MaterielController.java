package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.dto.ImportResultDTO;
import com.tunisiecables.parc_informatique.entity.Materiel;
import com.tunisiecables.parc_informatique.service.MaterielImportService;
import com.tunisiecables.parc_informatique.service.MaterielService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.tunisiecables.parc_informatique.service.MaterielExportService;


import java.util.List;

@RestController
@RequestMapping("/api/materiels")
@RequiredArgsConstructor
public class MaterielController {

    private final MaterielService materielService;
    private final MaterielImportService materielImportService;
    private final MaterielExportService materielExportService;

    @GetMapping
    public ResponseEntity<List<Materiel>> getAllActifs() {
        return ResponseEntity.ok(materielService.findAllActifs());
    }

    @GetMapping("/archives")
    public ResponseEntity<List<Materiel>> getAllArchives() {
        return ResponseEntity.ok(materielService.findAllArchives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Materiel> getById(@PathVariable Long id) {
        return ResponseEntity.ok(materielService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Materiel> create(@RequestBody Materiel materiel) {
        return ResponseEntity.ok(materielService.create(materiel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Materiel> update(@PathVariable Long id, @RequestBody Materiel materiel) {
        return ResponseEntity.ok(materielService.update(id, materiel));
    }

    @PutMapping("/{id}/archiver")
    public ResponseEntity<Materiel> archiver(@PathVariable Long id) {
        return ResponseEntity.ok(materielService.archiver(id));
    }

    @PutMapping("/{id}/restaurer")
    public ResponseEntity<Materiel> restaurer(@PathVariable Long id) {
        return ResponseEntity.ok(materielService.restaurer(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        materielService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResultDTO> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(materielImportService.importFromExcel(file));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excelBytes = materielExportService.exportToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"historique_materiels.xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}