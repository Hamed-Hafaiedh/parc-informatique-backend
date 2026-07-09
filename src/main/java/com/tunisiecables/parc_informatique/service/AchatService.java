package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Achat;
import com.tunisiecables.parc_informatique.repository.AchatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchatService {

    private final AchatRepository achatRepository;

    public List<Achat> findAll() {
        return achatRepository.findAll();
    }

    public Achat findById(Long id) {
        return achatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achat introuvable avec id : " + id));
    }

    public Achat create(Achat achat) {
        return achatRepository.save(achat);
    }

    public Achat update(Long id, Achat achatDetails) {
        Achat achat = findById(id);

        achat.setDateAchat(achatDetails.getDateAchat());
        achat.setMontant(achatDetails.getMontant());
        achat.setQuantite(achatDetails.getQuantite());
        achat.setNumeroFacture(achatDetails.getNumeroFacture());
        achat.setFournisseur(achatDetails.getFournisseur());

        return achatRepository.save(achat);
    }

    public void delete(Long id) {
        Achat achat = findById(id);
        achatRepository.delete(achat);
    }
}