package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Fournisseur;
import com.tunisiecables.parc_informatique.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public List<Fournisseur> findAll() {
        return fournisseurRepository.findAll();
    }

    public Fournisseur findById(Long id) {
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable avec id : " + id));
    }

    public Fournisseur create(Fournisseur fournisseur) {
        return fournisseurRepository.save(fournisseur);
    }

    public Fournisseur update(Long id, Fournisseur fournisseurDetails) {
        Fournisseur fournisseur = findById(id);

        fournisseur.setNom(fournisseurDetails.getNom());
        fournisseur.setContact(fournisseurDetails.getContact());
        fournisseur.setEmail(fournisseurDetails.getEmail());
        fournisseur.setTelephone(fournisseurDetails.getTelephone());

        return fournisseurRepository.save(fournisseur);
    }

    public void delete(Long id) {
        Fournisseur fournisseur = findById(id);
        fournisseurRepository.delete(fournisseur);
    }
}