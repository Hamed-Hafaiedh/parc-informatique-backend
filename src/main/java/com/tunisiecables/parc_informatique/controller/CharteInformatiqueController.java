package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.entity.CharteInformatique;
import com.tunisiecables.parc_informatique.service.CharteInformatiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/chartes")
@RequiredArgsConstructor
public class CharteInformatiqueController {

    private final CharteInformatiqueService charteService;

    @GetMapping
    public ResponseEntity<List<CharteInformatique>> getAll() {
        return ResponseEntity.ok(charteService.findAll());
    }

    @PostMapping("/upload")
    public ResponseEntity<CharteInformatique> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("employeId") Long employeId,
            @RequestParam(value = "dateSignature", required = false) String dateSignature) {

        LocalDate signature = (dateSignature != null && !dateSignature.isBlank())
                ? LocalDate.parse(dateSignature)
                : null;

        CharteInformatique charte = charteService.upload(file, employeId, signature);
        return ResponseEntity.ok(charte);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        CharteInformatique charte = charteService.findById(id);
        Resource resource = charteService.loadFileAsResource(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + charte.getNomFichier() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        charteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}