package com.hermes.backend.entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "synonyms")
@Data

public class Synonyms {

    @EmbeddedId
    private SynonymId id = new SynonymId();

    @ManyToOne
    @MapsId("wordId")
    @JoinColumn(name = "word_id", nullable = false)
    private Words word;

    @ManyToOne
    @MapsId("synonymWordId")
    @JoinColumn(name = "synonym_word_id", nullable = false)
    private Words synonymWord;
}
