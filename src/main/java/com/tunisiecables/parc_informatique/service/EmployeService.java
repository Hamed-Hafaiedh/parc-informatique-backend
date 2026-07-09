package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.Employe;
import com.tunisiecables.parc_informatique.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository employeRepository;

    public List<Employe> findAll() {
        return employeRepository.findAll();
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

    public void delete(Long id) {
        Employe employe = findById(id);
        employeRepository.delete(employe);
    }
}