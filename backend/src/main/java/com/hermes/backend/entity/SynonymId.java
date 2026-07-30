package com.hermes.backend.entity;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SynonymId implements Serializable {
    private Integer wordId;
    private Integer synonymWordId;

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (!(otherObject instanceof SynonymId otherId)) return false;

        return Objects.equals(wordId, otherId.wordId)
            && Objects.equals(synonymWordId, otherId.synonymWordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordId, synonymWordId);
    }
}