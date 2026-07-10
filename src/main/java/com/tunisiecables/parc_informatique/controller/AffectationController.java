package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.service.AffectationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affectations")
@RequiredArgsConstructor
public class AffectationController {

    private final AffectationService affectationService;

    @GetMapping
    public ResponseEntity<List<Affectation>> getAll() {
        return ResponseEntity.ok(affectationService.findAll());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        affectationService.delete(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }
}
