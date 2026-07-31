package com.hermes.backend.seed;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hermes.backend.entity.SynonymId;
import com.hermes.backend.entity.Synonyms;
import com.hermes.backend.entity.Words;
import com.hermes.backend.repository.SynonymsRepository;
import com.hermes.backend.repository.WordsRepository;

@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final WordsRepository wordsRepository;
    private final SynonymsRepository synonymsRepository;

    public DatabaseSeeder(WordsRepository wordsRepository, SynonymsRepository synonymsRepository) {
        this.wordsRepository = wordsRepository;
        this.synonymsRepository = synonymsRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments applicationArguments) {
        if (wordsRepository.count() > 0) {
            return;
        }

        Map<String, Words> wordsByNormalizedLemma = seedWordsFromCsvOnce();
        seedSynonymsFromCsv(wordsByNormalizedLemma);
    }

    private String normalizeLemma(String s) {
        if (s == null) return null;
        
        String normalized = s.trim().replaceAll("\\s+", " ");
        return normalized.toLowerCase(Locale.ROOT);
    }

    private Map<String, Words> seedWordsFromCsvOnce() {
        List<Words> wordEntitiesToInsert = new ArrayList<>();

        try (Reader reader = new InputStreamReader(
                new ClassPathResource("seed/words.csv").getInputStream(),
                StandardCharsets.UTF_8)) {

            
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);

            int rowNum = 1; 
            for (CSVRecord r : csvParser) {
                rowNum++;

                String lemmaRaw = r.get("lemma");
                String definitionRaw = r.get("definition");
                String translationEsRaw = r.get("translationEs");

                String lemma = lemmaRaw == null ? "" : lemmaRaw.trim();
                String definition = definitionRaw == null ? "" : definitionRaw.trim();
                String translationEs = translationEsRaw == null ? "" : translationEsRaw.trim();

                if (lemma.isBlank()) {
                    System.out.println("Skipping words row " + rowNum + " (blank lemma).");
                    continue;
                }

                Words wordEntity = new Words();
                wordEntity.setLemma(lemma);
                wordEntity.setDefinition(definition);
                wordEntity.setTranslationEs(translationEs);

                wordEntitiesToInsert.add(wordEntity);
            }

        } catch (Exception exception) {
            throw new RuntimeException("Failed to seed Words from seed/words.csv", exception);
        }

        List<Words> persistedWords = wordsRepository.saveAll(wordEntitiesToInsert);

        Map<String, Words> wordsEntitiesByNormalizedLemma = new LinkedHashMap<>();
        for (Words persistedWordEntity : persistedWords) {
            String normalized = normalizeLemma(persistedWordEntity.getLemma());
            if (normalized == null || normalized.isBlank()) continue;

            if (!wordsEntitiesByNormalizedLemma.containsKey(normalized)) {
                wordsEntitiesByNormalizedLemma.put(normalized, persistedWordEntity);
            } else {
                System.out.println("Warning: duplicate lemma after normalization: '" + persistedWordEntity.getLemma() + "'");
            }
        }

        System.out.println("Seeded words: " + persistedWords.size());
        return wordsEntitiesByNormalizedLemma;
    }

    private void seedSynonymsFromCsv(Map<String, Words> wordsEntitiesByNormalizedLemma) {
        List<Synonyms> synonymLinksToInsert = new ArrayList<>();
        Set<SynonymId> seenLinks = new HashSet<>();

        int skippedLinks = 0;
        int insertedLinks = 0;

        try (Reader reader = new InputStreamReader(
                new ClassPathResource("seed/synonyms.csv").getInputStream(),
                StandardCharsets.UTF_8)) {

            
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);

            int rowNum = 1; 
            for (CSVRecord r : csvParser) {
                rowNum++;

                String sourceLemmaRaw = r.get("source");
                String sourceLemmaNormalized = normalizeLemma(sourceLemmaRaw);

                if (sourceLemmaNormalized == null || sourceLemmaNormalized.isBlank()) {
                    skippedLinks++;
                    System.out.println("Skipping synonyms row " + rowNum + " (blank source).");
                    continue;
                }

                Words sourceWordEntity = wordsEntitiesByNormalizedLemma.get(sourceLemmaNormalized);
                if (sourceWordEntity == null) {
                    skippedLinks++;
                    System.out.println("Skipping synonyms row " + rowNum + " (missing source): '" + sourceLemmaRaw + "'");
                    continue;
                }

                String synonymFieldRaw = r.get("synonym");
                if (synonymFieldRaw == null || synonymFieldRaw.trim().isBlank()) {
                    skippedLinks++;
                    System.out.println("Skipping synonyms row " + rowNum + " (blank synonym field for source: '" + sourceLemmaRaw + "').");
                    continue;
                }

                String[] synonymLemmasRaw = synonymFieldRaw.trim().split("\\s*,\\s*");

                for (String synonymLemmaRaw : synonymLemmasRaw) {
                    if (synonymLemmaRaw == null) continue;

                    String synonymLemmaNormalized = normalizeLemma(synonymLemmaRaw);
                    if (synonymLemmaNormalized == null || synonymLemmaNormalized.isBlank()) continue;

                    Words synonymWordEntity = wordsEntitiesByNormalizedLemma.get(synonymLemmaNormalized);
                    if (synonymWordEntity == null) {
                        skippedLinks++;
                        System.out.println(
                                "Skipping synonym link in row " + rowNum
                                        + " (missing synonym): '" + synonymLemmaRaw
                                        + "' (source: '" + sourceLemmaRaw + "')"
                        );
                        continue;
                    }

                    Integer sourceId = sourceWordEntity.getId();
                    Integer synonymId = synonymWordEntity.getId();
                    if (sourceId == null || synonymId == null) {
                        skippedLinks++;
                        System.out.println("Skipping synonym link in row " + rowNum + " due to null ids.");
                        continue;
                    }

                    SynonymId linkId = new SynonymId(sourceId, synonymId);

                    if (seenLinks.add(linkId)) {
                        Synonyms link = new Synonyms();
                        link.setId(linkId);
                        link.setWord(sourceWordEntity);
                        link.setSynonymWord(synonymWordEntity);

                        synonymLinksToInsert.add(link);
                        insertedLinks++;
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed Synonyms from seed/synonyms.csv", e);
        }

        synonymsRepository.saveAll(synonymLinksToInsert);
        System.out.println("Seeded synonyms links: " + insertedLinks + " (skipped: " + skippedLinks + ")");
    }
}
