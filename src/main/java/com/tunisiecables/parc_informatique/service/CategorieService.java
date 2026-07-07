package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Categorie;
import com.tunisiecables.parc_informatique.repository.CategorieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    public Categorie findById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categorie introuvable avec id : " + id));
    }

    public Categorie create(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    public Categorie update(Long id, Categorie categorieDetails) {
        Categorie categorie = findById(id);

        categorie.setNom(categorieDetails.getNom());
        categorie.setDescription(categorieDetails.getDescription());

        return categorieRepository.save(categorie);
    }

    public void delete(Long id) {
        Categorie categorie = findById(id);
        categorieRepository.delete(categorie);
    }
}