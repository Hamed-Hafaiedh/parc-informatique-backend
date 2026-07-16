package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.enums.StatutAffectation;
import com.tunisiecables.parc_informatique.exception.SuppressionImpossibleException;
import com.tunisiecables.parc_informatique.repository.AffectationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AffectationService {

    private final AffectationRepository affectationRepository;

    /**
     * Affectations en cours (ACTIVE ou SUSPENDUE) - vue par defaut
     */
    public List<Affectation> findAllActives() {
        return affectationRepository.findAll().stream()
                .filter(a -> a.getStatut() != StatutAffectation.CLOTUREE)
                .toList();
    }

    /**
     * Affectations cloturees - vue "historique"
     */
    public List<Affectation> findAllCloturees() {
        return affectationRepository.findAll().stream()
                .filter(a -> a.getStatut() == StatutAffectation.CLOTUREE)
                .toList();
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
        affectation.setGroupeUtilisateurs(affectationDetails.getGroupeUtilisateurs());

        return affectationRepository.save(affectation);
    }

    /**
     * Cloture une affectation (suppression douce).
     * Passe le statut a CLOTUREE et renseigne la date de fin.
     * L'affectation reste conservee dans l'historique.
     */
    public Affectation cloturer(Long id) {
        Affectation affectation = findById(id);
        affectation.setStatut(StatutAffectation.CLOTUREE);
        affectation.setDateFin(LocalDate.now());
        return affectationRepository.save(affectation);
    }

    /**
     * Suppression DEFINITIVE et physique.
     * Reservee aux affectations deja cloturees (nettoyage volontaire).
     */
    public void delete(Long id) {
        Affectation affectation = findById(id);
        try {
            affectationRepository.delete(affectation);
        } catch (DataIntegrityViolationException e) {
            throw new SuppressionImpossibleException(
                    "Impossible de supprimer cette affectation : elle est encore liee a une autre donnee.");
        }
    }
}