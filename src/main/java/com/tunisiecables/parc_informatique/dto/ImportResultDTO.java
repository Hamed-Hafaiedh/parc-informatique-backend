package com.tunisiecables.parc_informatique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ImportResultDTO {
    private int totalLignes;
    private int importees;
    private int rejetees;
    private int employesCrees;
    private int affectationsCreees;
    private List<ImportErrorDTO> erreurs;
}