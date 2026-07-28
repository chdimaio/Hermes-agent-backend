package com.hermes.backend.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "words")
@Data
public class Words {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String lemma;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(name = "translation_es", columnDefinition = "TEXT")
    private String translationEs;
}
