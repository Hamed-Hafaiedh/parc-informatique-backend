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
        Materiel materiel = findById(id);

        materiel.setNumeroDeSerie(materielDetails.getNumeroDeSerie());
        materiel.setMarque(materielDetails.getMarque());
        materiel.setModele(materielDetails.getModele());
        materiel.setStatut(materielDetails.getStatut());
        materiel.setDescription(materielDetails.getDescription());
        materiel.setCategorie(materielDetails.getCategorie());
        materiel.setSite(materielDetails.getSite());

        return materielRepository.save(materiel);
    }

    public void delete(Long id) {
        Materiel materiel = findById(id);
        materielRepository.delete(materiel);
    }
}