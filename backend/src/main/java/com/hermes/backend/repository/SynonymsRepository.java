package com.hermes.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hermes.backend.entity.SynonymId;
import com.hermes.backend.entity.Synonyms;

public interface SynonymsRepository extends JpaRepository<Synonyms, SynonymId>{
    List<Synonyms> findById_WordId(Integer wordId);

}
