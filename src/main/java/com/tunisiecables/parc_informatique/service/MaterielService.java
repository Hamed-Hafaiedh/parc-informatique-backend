package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.entity.Maintenance;
import com.tunisiecables.parc_informatique.entity.Materiel;
import com.tunisiecables.parc_informatique.enums.StatutMateriel;
import com.tunisiecables.parc_informatique.exception.SuppressionImpossibleException;
import com.tunisiecables.parc_informatique.repository.AffectationRepository;
import com.tunisiecables.parc_informatique.repository.MaintenanceRepository;
import com.tunisiecables.parc_informatique.repository.MaterielRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterielService {

    private final MaterielRepository materielRepository;
    private final AffectationRepository affectationRepository;
    private final MaintenanceRepository maintenanceRepository;

    public List<Materiel> findAllActifs() {
        return materielRepository.findAll().stream()
                .filter(m -> !m.isArchive())
                .toList();
    }

    public List<Materiel> findAllArchives() {
        return materielRepository.findAll().stream()
                .filter(Materiel::isArchive)
                .toList();
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

    /**
     * Archive un materiel (suppression douce).
     * Passe le statut a HORS_SERVICE et conserve tout l'historique
     * (affectations, maintenances) intact.
     */
    public Materiel archiver(Long id) {
        Materiel materiel = findById(id);
        materiel.setArchive(true);
        materiel.setDateArchivage(LocalDate.now());
        materiel.setStatut(StatutMateriel.HORS_SERVICE);
        return materielRepository.save(materiel);
    }

    /**
     * Restaure un materiel archive vers la liste active.
     * Remet le statut a EN_STOCK par defaut.
     */
    public Materiel restaurer(Long id) {
        Materiel materiel = findById(id);
        materiel.setArchive(false);
        materiel.setDateArchivage(null);
        materiel.setStatut(StatutMateriel.EN_STOCK);
        return materielRepository.save(materiel);
    }

    /**
     * Suppression DEFINITIVE et physique.
     * Supprime AUSSI en cascade les Affectations et Maintenances liees.
     * Reservee aux materiels deja archives (action volontaire de nettoyage,
     * accessible uniquement depuis l'onglet "Archives").
     */
    public void delete(Long id) {
        Materiel materiel = findById(id);

        List<Maintenance> maintenancesLiees = maintenanceRepository.findAll().stream()
                .filter(m -> m.getMateriel() != null && m.getMateriel().getId().equals(id))
                .toList();
        maintenanceRepository.deleteAll(maintenancesLiees);

        List<Affectation> affectationsLiees = affectationRepository.findAll().stream()
                .filter(a -> a.getMateriel() != null && a.getMateriel().getId().equals(id))
                .toList();
        affectationRepository.deleteAll(affectationsLiees);

        try {
            materielRepository.delete(materiel);
        } catch (DataIntegrityViolationException e) {
            throw new SuppressionImpossibleException(
                    "Impossible de supprimer ce materiel : une autre donnee (achat, charte...) "
                            + "y fait encore reference.");
        }
    }
}