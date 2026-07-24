package com.hermes.backend.service;

import java.util.Optional;

public interface WordsService {

    Optional<String> getDefinition(String lemma);

    
}
