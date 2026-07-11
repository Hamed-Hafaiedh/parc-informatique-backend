package com.tunisiecables.parc_informatique.service;

import com.tunisiecables.parc_informatique.entity.CharteInformatique;
import com.tunisiecables.parc_informatique.entity.Employe;
import com.tunisiecables.parc_informatique.repository.CharteInformatiqueRepository;
import com.tunisiecables.parc_informatique.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CharteInformatiqueService {

    private final CharteInformatiqueRepository charteRepository;
    private final EmployeRepository employeRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<CharteInformatique> findAll() {
        return charteRepository.findAll();
    }

    public CharteInformatique findById(Long id) {
        return charteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charte introuvable avec id : " + id));
    }

    public CharteInformatique upload(MultipartFile file, Long employeId, LocalDate dateSignature) {
        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String originalName = file.getOriginalFilename();
            String uniqueName = UUID.randomUUID() + "_" + originalName;
            Path filePath = dirPath.resolve(uniqueName);
            Files.copy(file.getInputStream(), filePath);

            Employe employe = employeRepository.findById(employeId)
                    .orElseThrow(() -> new RuntimeException("Employe introuvable avec id : " + employeId));

            CharteInformatique charte = CharteInformatique.builder()
                    .nomFichier(originalName)
                    .cheminFichier(filePath.toString())
                    .dateUpload(LocalDate.now())
                    .dateSignature(dateSignature)
                    .employe(employe)
                    .build();

            return charteRepository.save(charte);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier", e);
        }
    }

    public Resource loadFileAsResource(Long id) {
        CharteInformatique charte = findById(id);
        try {
            Path filePath = Paths.get(charte.getCheminFichier());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Fichier introuvable : " + charte.getNomFichier());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur lors du chargement du fichier", e);
        }
    }

    public void delete(Long id) {
        CharteInformatique charte = findById(id);
        try {
            Files.deleteIfExists(Paths.get(charte.getCheminFichier()));
        } catch (IOException e) {
            // On continue meme si le fichier physique n'a pas pu etre supprime
        }
        charteRepository.delete(charte);
    }
}