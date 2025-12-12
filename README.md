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

## Sample Requests/Responses

### Ingest (multipart)
```bash
curl -X POST http://localhost:8080/api/documents \
  -F "file=@/path/to/sample.txt"
```
Response:
```json
{
  "documentId": "4b7e93c0-1c9f-4e9f-9b02-2e06d3e6f6c2",
  "chunksIndexed": 3
}
```

### List documents
```bash
curl http://localhost:8080/api/documents
```
Response:
```json
[
  {
    "id": "4b7e93c0-1c9f-4e9f-9b02-2e06d3e6f6c2",
    "title": "sample",
    "originalFileName": "sample.txt",
    "contentType": "text/plain",
    "sizeBytes": 1200,
    "createdAt": "2025-12-11T21:00:00Z",
    "updatedAt": "2025-12-11T21:00:00Z",
    "chunkCount": 3
  }
]
```

### Get chunks for a document
```bash
curl http://localhost:8080/api/documents/4b7e93c0-1c9f-4e9f-9b02-2e06d3e6f6c2/chunks
```
Response:
```json
[
  {
    "id": "7d1f7a3e-5bf7-4b71-8a7d-65b42d5f8e93",
    "chunkIndex": 0,
    "text": "Chunk text ..."
  },
  {
    "id": "b3e8949f-1e1d-4e62-8bb1-3b5c9a5f2b1e",
    "chunkIndex": 1,
    "text": "Chunk text ..."
  }
]
```

### RAG query
```bash
curl -X POST http://localhost:8080/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"question":"What does the sample document say?","topK":3}'
```
Response:
```json
{
  "question": "What does the sample document say?",
  "answer": "It explains the content of the sample document ...",
  "context": [
    "Chunk text ...",
    "Another chunk ..."
  ]
}
```

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
