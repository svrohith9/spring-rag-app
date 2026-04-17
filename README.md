# Spring RAG App

A Retrieval-Augmented Generation (RAG) service built on Spring Boot 3 and LangChain4j. Ingest PDFs and text files, embed them into a local vector store, and ask grounded questions against them — all running locally through Ollama, no external API keys required.

## What it does

- **Ingest** documents (PDF / TXT) with automatic chunking and overlap
- **Embed** chunks using an Ollama embedding model (default: `nomic-embed-text`)
- **Retrieve** the top-K most relevant chunks per query via semantic search
- **Generate** contextual answers from a local chat model through Ollama
- **Manage** documents through a simple REST API

## Stack

| Layer | Choice |
|---|---|
| Framework | Spring Boot 3 (Java 17+) |
| RAG orchestration | LangChain4j |
| LLM runtime | Ollama (local, `http://localhost:11434`) |
| Embeddings | `nomic-embed-text` |
| Vector store | H2 (file-based at `./data/ragdb`) |
| Build | Maven |

## Prerequisites

- Java 17+
- Maven
- [Ollama](https://ollama.ai) running locally
- Pull the models you want:
  ```bash
  ollama pull nomic-embed-text
  ollama pull llama2        # or mistral, or any chat-capable model
  ```

## Run

```bash
mvn spring-boot:run
# or
mvn clean package && java -jar target/spring-rag-app-0.0.1-SNAPSHOT.jar
```

The app starts on `http://localhost:8080`. H2 file DB is at `./data/ragdb` (console at `/h2-console`, user `sa`, password `password`).

## Configure

Edit `src/main/resources/application.yml`:

- `rag.ollama.base-url`, `rag.ollama.model` (chat), `rag.ollama.embedding-model`
- Chunking: `rag.chunking.max-tokens`, `rag.chunking.overlap`
- Retrieval: `rag.top-k`

## REST API

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/documents` | Ingest a PDF or TXT file (multipart `file`) |
| `GET` | `/api/documents` | List all ingested documents |
| `GET` | `/api/documents/{id}` | Document metadata |
| `GET` | `/api/documents/{id}/chunks` | View the chunks for a document |
| `POST` | `/api/rag/query` | Ask a question (JSON: `{ "question": "...", "topK": 3 }`) |

### Examples

**Ingest**
```bash
curl -X POST http://localhost:8080/api/documents \
  -F "file=@/path/to/sample.txt"
```
```json
{
  "documentId": "4b7e93c0-1c9f-4e9f-9b02-2e06d3e6f6c2",
  "chunksIndexed": 3
}
```

**List documents**
```bash
curl http://localhost:8080/api/documents
```
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

**Get chunks**
```bash
curl http://localhost:8080/api/documents/4b7e93c0-1c9f-4e9f-9b02-2e06d3e6f6c2/chunks
```
```json
[
  { "id": "7d1f7a3e-...", "chunkIndex": 0, "text": "Chunk text ..." },
  { "id": "b3e8949f-...", "chunkIndex": 1, "text": "Chunk text ..." }
]
```

**RAG query**
```bash
curl -X POST http://localhost:8080/api/rag/query \
  -H "Content-Type: application/json" \
  -d '{"question":"What does the sample document say?","topK":3}'
```
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

## Quick HTTP tests

Use `src/test/resources/http/rag-api.http` with the IntelliJ HTTP Client (or any `.http` runner) to exercise the full flow:

1. Ingest sample text
2. List docs and capture an `id`
3. Get chunks
4. POST a RAG query

## Project structure

```
src/
├── main/
│   ├── java/       # Controllers, services, RAG pipeline
│   └── resources/  # application.yml
└── test/
data/               # Local H2 vector store (gitignored)
```

## Troubleshooting

- Make sure Ollama is running and the embedding model is pulled (`ollama list`).
- H2 lock errors: stop the app and delete `./data/` — the DB will re-init on next run.
- SQL logs are set to `warn` by default; run with `--debug` to see more.

## Why this exists

A minimal, fully local reference for wiring up RAG in a Spring ecosystem — useful for prototyping before committing to a managed vector DB or a hosted LLM provider.

## License

MIT
