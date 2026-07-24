package com.hermes.backend.repository;
import com.hermes.backend.entity.Words;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WordsRepository extends JpaRepository<Words, Integer> {
    Optional<Words> findByLemma(String lemma);

}
