package com.hermes.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordCardResponse {
    private String lemma;
    private String definition;
    private String translationEs;
    private List<String> synonyms;

}
