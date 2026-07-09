package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Materiel;
import com.tunisiecables.parc_informatique.repository.MaterielRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterielService {

    private final MaterielRepository materielRepository;

    public List<Materiel> findAll() {
        return materielRepository.findAll();
    }

    public Materiel findById(Long id) {
        return materielRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materiel introuvable avec id : " + id));
    }

    public Materiel create(Materiel materiel) {
        return materielRepository.save(materiel);
    }

    public Materiel update(Long id, Materiel materielDetails) {
        if (!materielRepository.existsById(id)) {
            throw new RuntimeException("Materiel introuvable avec id : " + id);
        }
        materielDetails.setId(id);
        return materielRepository.save(materielDetails);
    }

    public void delete(Long id) {
        Materiel materiel = findById(id);
        materielRepository.delete(materiel);
    }
}