package com.tunisiecables.parc_informatique.controller;

import com.tunisiecables.parc_informatique.entity.Achat;
import com.tunisiecables.parc_informatique.service.AchatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achats")
@RequiredArgsConstructor
public class AchatController {

    private final AchatService achatService;

    @GetMapping
    public ResponseEntity<List<Achat>> getAll() {
        return ResponseEntity.ok(achatService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Achat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(achatService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Achat> create(@RequestBody Achat achat) {
        return ResponseEntity.ok(achatService.create(achat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Achat> update(@PathVariable Long id, @RequestBody Achat achat) {
        return ResponseEntity.ok(achatService.update(id, achat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        achatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}