package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Affectation;
import com.tunisiecables.parc_informatique.entity.CharteInformatique;
import com.tunisiecables.parc_informatique.entity.Employe;
import com.tunisiecables.parc_informatique.enums.StatutAffectation;
import com.tunisiecables.parc_informatique.exception.SuppressionImpossibleException;
import com.tunisiecables.parc_informatique.repository.AffectationRepository;
import com.tunisiecables.parc_informatique.repository.CharteInformatiqueRepository;
import com.tunisiecables.parc_informatique.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final AffectationRepository affectationRepository;
    private final CharteInformatiqueRepository charteInformatiqueRepository;

    public List<Employe> findAllActifs() {
        return employeRepository.findAll().stream()
                .filter(e -> !e.isArchive())
                .toList();
    }

    public List<Employe> findAllArchives() {
        return employeRepository.findAll().stream()
                .filter(Employe::isArchive)
                .toList();
    }

    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employe introuvable avec id : " + id));
    }

    public Employe create(Employe employe) {
        return employeRepository.save(employe);
    }

    public Employe update(Long id, Employe employeDetails) {
        Employe employe = findById(id);

        employe.setNom(employeDetails.getNom());
        employe.setPrenom(employeDetails.getPrenom());
        employe.setMatricule(employeDetails.getMatricule());
        employe.setPoste(employeDetails.getPoste());
        employe.setEmail(employeDetails.getEmail());
        employe.setTelephone(employeDetails.getTelephone());
        employe.setDepartement(employeDetails.getDepartement());
        employe.setSite(employeDetails.getSite());

        return employeRepository.save(employe);
    }

    /**
     * Archive un employe (depart de l'entreprise, suppression douce).
     * Cloture automatiquement toutes ses affectations en cours,
     * pour eviter qu'un materiel reste "affecte" a quelqu'un qui est parti.
     */
    public Employe archiver(Long id) {
        Employe employe = findById(id);

        List<Affectation> affectationsActives = affectationRepository.findAll().stream()
                .filter(a -> a.getEmploye() != null && a.getEmploye().getId().equals(id))
                .filter(a -> a.getStatut() != StatutAffectation.CLOTUREE)
                .toList();

        for (Affectation a : affectationsActives) {
            a.setStatut(StatutAffectation.CLOTUREE);
            a.setDateFin(LocalDate.now());
            affectationRepository.save(a);
        }

        employe.setArchive(true);
        employe.setDateArchivage(LocalDate.now());
        return employeRepository.save(employe);
    }

    /**
     * Restaure un employe archive.
     * Les affectations cloturees automatiquement lors de l'archivage
     * restent cloturees (pas de reactivation automatique).
     */
    public Employe restaurer(Long id) {
        Employe employe = findById(id);
        employe.setArchive(false);
        employe.setDateArchivage(null);
        return employeRepository.save(employe);
    }

    /**
     * Suppression DEFINITIVE et physique.
     * Supprime aussi en cascade les Affectations et la CharteInformatique liees.
     */
    public void delete(Long id) {
        Employe employe = findById(id);

        List<Affectation> affectationsLiees = affectationRepository.findAll().stream()
                .filter(a -> a.getEmploye() != null && a.getEmploye().getId().equals(id))
                .toList();
        affectationRepository.deleteAll(affectationsLiees);

        List<CharteInformatique> chartesLiees = charteInformatiqueRepository.findAll().stream()
                .filter(c -> c.getEmploye() != null && c.getEmploye().getId().equals(id))
                .toList();
        charteInformatiqueRepository.deleteAll(chartesLiees);

        try {
            employeRepository.delete(employe);
        } catch (DataIntegrityViolationException e) {
            throw new SuppressionImpossibleException(
                    "Impossible de supprimer cet employe : une autre donnee y fait encore reference.");
        }
    }
}