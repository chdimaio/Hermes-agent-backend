package com.hermes.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.hermes.backend.dto.WordCardResponse;
import com.hermes.backend.entity.Synonyms;
import com.hermes.backend.repository.WordsRepository;
import com.hermes.backend.repository.SynonymsRepository;

@Service
public class WordsServiceImplementation implements WordsService {

    private final WordsRepository wordsRepository;
    private final SynonymsRepository synonymsRepository;

    public WordsServiceImplementation(WordsRepository wordsRepository, SynonymsRepository synonymsRepository){
        this.wordsRepository = wordsRepository;
        this.synonymsRepository = synonymsRepository;
    }

    @Override
     public Optional<WordCardResponse> getWordCard(String lemma) {
        return wordsRepository.findByLemma(lemma)
                .map(word -> {
                    List<Synonyms> synonymLinks = synonymsRepository.findById_WordId(word.getId());


                    List<String> synonyms = synonymLinks.stream()
                        .map(link -> link.getSynonymWord().getLemma())
                        .toList();
    
                    return new WordCardResponse(
                    word.getLemma(),
                    word.getDefinition(),
                    word.getTranslationEs(),
                    synonyms
                    );
                 });
    }             
}
