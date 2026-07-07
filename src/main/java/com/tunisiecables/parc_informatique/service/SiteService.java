package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Site;
import com.tunisiecables.parc_informatique.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    public List<Site> findAll() {
        return siteRepository.findAll();
    }

    public Site findById(Long id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site introuvable avec id : " + id));
    }

    public Site create(Site site) {
        return siteRepository.save(site);
    }

    public Site update(Long id, Site siteDetails) {
        Site site = findById(id);

        site.setNom(siteDetails.getNom());
        site.setAdresse(siteDetails.getAdresse());
        site.setVille(siteDetails.getVille());

        return siteRepository.save(site);
    }

    public void delete(Long id) {
        Site site = findById(id);
        siteRepository.delete(site);
    }
}