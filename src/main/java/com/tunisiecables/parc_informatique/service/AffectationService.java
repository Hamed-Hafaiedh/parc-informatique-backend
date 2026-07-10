package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.repository.AffectationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AffectationService {

    private final AffectationRepository affectationRepository;

    public List<Affectation> findAll() {
        return affectationRepository.findAll();
    }

    public Affectation findById(Long id) {
        return affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation introuvable avec id : " + id));
    }

    public Affectation create(Affectation affectation) {
        return affectationRepository.save(affectation);
    }

    public Affectation update(Long id, Affectation affectationDetails) {
        Affectation affectation = findById(id);

        affectation.setDateDebut(affectationDetails.getDateDebut());
        affectation.setDateFin(affectationDetails.getDateFin());
        affectation.setStatut(affectationDetails.getStatut());
        affectation.setRemarque(affectationDetails.getRemarque());
        affectation.setMateriel(affectationDetails.getMateriel());
        affectation.setEmploye(affectationDetails.getEmploye());

        return affectationRepository.save(affectation);
    }

    public void delete(Long id) {
        Affectation affectation = findById(id);
        affectationRepository.delete(affectation);
    }
}