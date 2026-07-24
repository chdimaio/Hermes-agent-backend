package com.hermes.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hermes.backend.entity.Words;
import com.hermes.backend.repository.WordsRepository;

@Service
public class WordsServiceImplementation implements WordsService{

    private final WordsRepository wordsRepository;

    public WordsServiceImplementation(WordsRepository wordsRepository){
        this.wordsRepository = wordsRepository;
    }

    @Override
     public Optional<String> getDefinition(String lemma) {
        return wordsRepository.findByLemma(lemma)
                .map(Words::getDefinition);
    }

}
