package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.dto.ImportResultDTO;
import com.tunisiecables.parc_informatique.entity.Employe;
import com.tunisiecables.parc_informatique.service.EmployeExportService;
import com.tunisiecables.parc_informatique.service.EmployeImportService;
import com.tunisiecables.parc_informatique.service.EmployeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeService employeService;
    private final EmployeImportService employeImportService;
    private final EmployeExportService employeExportService;

    @GetMapping
    public ResponseEntity<List<Employe>> getAllActifs() {
        return ResponseEntity.ok(employeService.findAllActifs());
    }

    @GetMapping("/archives")
    public ResponseEntity<List<Employe>> getAllArchives() {
        return ResponseEntity.ok(employeService.findAllArchives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employe> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Employe> create(@RequestBody Employe employe) {
        return ResponseEntity.ok(employeService.create(employe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employe> update(@PathVariable Long id, @RequestBody Employe employe) {
        return ResponseEntity.ok(employeService.update(id, employe));
    }

    @PutMapping("/{id}/archiver")
    public ResponseEntity<Employe> archiver(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.archiver(id));
    }

    @PutMapping("/{id}/restaurer")
    public ResponseEntity<Employe> restaurer(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.restaurer(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResultDTO> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(employeImportService.importFromExcel(file));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excelBytes = employeExportService.exportToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"historique_employes.xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}