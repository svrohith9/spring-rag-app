# Spring RAG API

Spring Boot 3 RAG service using LangChain4j, Ollama (chat + embeddings), and H2 for vector storage.

## Prereqs
- Java 17+
- Maven
- Ollama running locally (default `http://localhost:11434`)
  - Pull models: `ollama pull llama2` (or `mistral`), `ollama pull nomic-embed-text`

## Run
```bash
mvn spring-boot:run
# or
mvn clean package && java -jar target/spring-rag-app-0.0.1-SNAPSHOT.jar
```
H2 file DB is at `./data/ragdb` (console at `/h2-console`, user `sa`, password `password`).

## Config
Edit `src/main/resources/application.yml`:
- `rag.ollama.base-url`, `rag.ollama.model` (chat), `rag.ollama.embedding-model`
- Chunking: `rag.chunking.max-tokens`, `rag.chunking.overlap`
- Retrieval: `rag.top-k`

## REST Endpoints
- POST `/api/documents` (multipart `file`): ingest PDF/TXT, auto-embeds chunks
- GET `/api/documents` : list docs
- GET `/api/documents/{id}` : doc metadata
- GET `/api/documents/{id}/chunks` : chunks
- POST `/api/rag/query` (JSON `{question, topK}`) : RAG answer with context

## Quick HTTP Tests (IntelliJ HTTP Client)
Use `src/test/resources/http/rag-api.http` and run requests against `http://localhost:8080`.
1) Ingest sample text
2) List docs, capture `id`
3) Get chunks
4) POST RAG query

## Notes / Troubleshooting
- Ensure Ollama server is up and the embedding model exists (`ollama list`).
- If H2 lock issues, stop the app and delete `data/` locally (DB will re-init). The folder is gitignored.
- Logs set to warn for SQL; enable debug with `--debug` if needed.
