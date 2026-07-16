package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.dto.ImportResultDTO;
import com.tunisiecables.parc_informatique.entity.Maintenance;
import com.tunisiecables.parc_informatique.service.MaintenanceExportService;
import com.tunisiecables.parc_informatique.service.MaintenanceImportService;
import com.tunisiecables.parc_informatique.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final MaintenanceImportService maintenanceImportService;
    private final MaintenanceExportService maintenanceExportService;

    @GetMapping
    public ResponseEntity<List<Maintenance>> getAll() {
        return ResponseEntity.ok(maintenanceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Maintenance> getById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Maintenance> create(@RequestBody Maintenance maintenance) {
        return ResponseEntity.ok(maintenanceService.create(maintenance));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> update(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        return ResponseEntity.ok(maintenanceService.update(id, maintenance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResultDTO> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(maintenanceImportService.importFromExcel(file));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excelBytes = maintenanceExportService.exportToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"historique_maintenances.xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}