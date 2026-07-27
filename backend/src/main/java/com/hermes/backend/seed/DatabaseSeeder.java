package com.hermes.backend.seed;

import java.util.List;
import java.util.Map;
import com.hermes.backend.BackendApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.hermes.backend.entity.Synonyms;
import com.hermes.backend.entity.Words;
import com.hermes.backend.repository.SynonymsRepository;
import com.hermes.backend.repository.WordsRepository;

@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final BackendApplication backendApplication;
    private final WordsRepository wordsRepository;
    private final SynonymsRepository synonymsRepository;

    public DatabaseSeeder(WordsRepository wordsRepository, SynonymsRepository synonymsRepository) {
        this.wordsRepository = wordsRepository;
        this.synonymsRepository = synonymsRepository;
    }


@Override
public void run(ApplicationArguments args) {
    if (wordsRepository.count() > 0) return;
    List<Words> wordsToInsert = List.of(


    );

    List<Words> insertedWords = wordsRepository.saveAll(wordsToInsert);

    Map<String, Long> idByLemma = insertedWords.stream()
        .collect(java.util.stream.Collectors.toMap(
            word -> words.getLemma(),
            Words::getId
        ));
    
    List<Synonyms> synonymsToInsert = List.of();

    synonymsRepository.saveAll(synonymsToInsert);
    }

    private Synonyms createLink(Long wordId, Long synonymWordId) {
        Synonyms s = new Synonyms();
        