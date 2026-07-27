package com.hermes.backend.controller;


import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hermes.backend.dto.WordCardResponse;
import com.hermes.backend.service.WordsService;


@RestController
@RequestMapping("/api/v1/words")

public class WordsController {
    private final WordsService wordsService;

    public WordsController(WordsService wordsService){
        this.wordsService = wordsService;
    }

    @GetMapping("/card")
    public ResponseEntity<WordCardResponse> findWordCard(@RequestParam String lemma) {
           return wordsService.getWordCard(lemma)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }    

}

