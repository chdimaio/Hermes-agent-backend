package com.hermes.backend.service;

import java.util.Optional;

import com.hermes.backend.dto.WordCardResponse;

public interface WordsService {

    Optional<WordCardResponse> getWordCard(String lemma);

    
}
