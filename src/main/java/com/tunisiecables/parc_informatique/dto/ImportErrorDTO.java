package com.tunisiecables.parc_informatique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportErrorDTO {
    private int ligne;
    private String raison;
}