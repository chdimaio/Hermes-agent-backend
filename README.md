# Hermes — Backend

A Spring Boot API that serves word definitions, Spanish translations, and synonyms for the Hermes bilingual dictionary chatbot.

## Stack

- **Java 21** — language
- **Spring Boot 3.5.16** — application framework
- **Maven** — build tool
- **PostgreSQL** — production database
- **H2** — in-memory database for tests
- **Spring Data JPA** — persistence
- **Lombok** — boilerplate reduction
- **Apache Commons CSV** — seed data parsing

## Getting started

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`. Requires a PostgreSQL database named `hermes` (connection configured in `application.properties` and `application-local.properties`).

## Project structure

```
src/main/java/com/hermes/backend/
├── BackendApplication.java      # Spring Boot entry point
├── controller/
│   └── WordsController.java     # REST endpoints
├── dto/
│   └── WordCardResponse.java    # API response shape
├── entity/
│   ├── Words.java               # word table (lemma, definition, translation_es)
│   ├── Synonyms.java            # self-referencing many-to-many join entity
│   └── SynonymId.java           # composite key for synonyms
├── repository/
│   ├── WordsRepository.java     # JPA repository
│   └── SynonymsRepository.java  # JPA repository
├── seed/
│   └── DatabaseSeeder.java      # loads CSV data on first run
└── service/
    ├── WordsService.java                # interface
    └── WordsServiceImplementation.java   # implementation
```

## API

### `GET /api/v1/words/card?lemma={word}`

Returns a word card with its definition, Spanish translation, and list of synonyms. Returns `404` if the word is not found.

**Example response:**

```json
{
  "lemma": "Abode",
  "definition": "A place in which one lives; one's home.",
  "translationEs": "Morada",
  "synonyms": []
}
```

## Seed data

- `words.csv` — 278 entries with lemma, definition, and Spanish translation
- `synonyms.csv` — 49 entries mapping source words to their synonym lists

Data is loaded automatically on first startup when the `words` table is empty.

