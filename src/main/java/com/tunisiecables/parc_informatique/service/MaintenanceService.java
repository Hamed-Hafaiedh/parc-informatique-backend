package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Maintenance;
import com.tunisiecables.parc_informatique.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    public List<Maintenance> findAll() {
        return maintenanceRepository.findAll();
    }

    public Maintenance findById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance introuvable avec id : " + id));
    }

    public Maintenance create(Maintenance maintenance) {
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance update(Long id, Maintenance maintenanceDetails) {
        Maintenance maintenance = findById(id);

        maintenance.setDateDeclaration(maintenanceDetails.getDateDeclaration());
        maintenance.setDateResolution(maintenanceDetails.getDateResolution());
        maintenance.setStatut(maintenanceDetails.getStatut());
        maintenance.setDescription(maintenanceDetails.getDescription());
        maintenance.setRapport(maintenanceDetails.getRapport());
        maintenance.setMateriel(maintenanceDetails.getMateriel());
        maintenance.setTechnicien(maintenanceDetails.getTechnicien());

        return maintenanceRepository.save(maintenance);
    }

    public void delete(Long id) {
        Maintenance maintenance = findById(id);
        maintenanceRepository.delete(maintenance);
    }
}