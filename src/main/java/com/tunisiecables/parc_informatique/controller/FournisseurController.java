package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.entity.Fournisseur;
import com.tunisiecables.parc_informatique.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public ResponseEntity<List<Fournisseur>> getAll() {
        return ResponseEntity.ok(fournisseurService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fournisseur> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fournisseurService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Fournisseur> create(@RequestBody Fournisseur fournisseur) {
        return ResponseEntity.ok(fournisseurService.create(fournisseur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fournisseur> update(@PathVariable Long id, @RequestBody Fournisseur fournisseur) {
        return ResponseEntity.ok(fournisseurService.update(id, fournisseur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fournisseurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}