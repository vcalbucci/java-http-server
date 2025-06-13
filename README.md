# Java HTTP Server

A lightweight, modular HTTP/1.1 server implemented from scratch in Java.  
Designed for learning, experimentation, and extension — fully standards-compliant for core methods.

## Features

- 📜 **HTTP/1.1 support**
- ⚡ **Persistent connections** (keep-alive)
- 🗺️ **Router-based request dispatching**
- 📝 **File serving with GET / POST / DELETE / HEAD**
- 🎭 **Echo handler with optional GZIP compression**
- 🧭 **OPTIONS method with dynamic Allow header**
- 🐾 **Basic User-Agent handler**
- 🧪 **Comprehensive unit and integration test suite (JUnit 5)**

### Architecture Diagram

```mermaid
graph TD
    SRC[src/]
    SRC --> MAINJAVA[main/java/]

    MAINJAVA --> EXCEPTIONS[exceptions/]
    EXCEPTIONS --> HTTPParseException[HTTPParseException.java]

    MAINJAVA --> HANDLERS[handlers/]
    HANDLERS --> EchoHandler[EchoHandler.java]
    HANDLERS --> FileHandler[FileHandler.java]
    HANDLERS --> HTTPHandler[HTTPHandler.java]
    HANDLERS --> NotFoundHandler[NotFoundHandler.java]
    HANDLERS --> UserAgentHandler[UserAgentHandler.java]

    MAINJAVA --> HTTP[http/]
    HTTP --> ContentType[ContentType.java]
    HTTP --> HTTPRequest[HTTPRequest.java]
    HTTP --> HTTPRequestParser[HTTPRequestParser.java]
    HTTP --> HTTPResponse[HTTPResponse.java]
    HTTP --> HTTPResponses[HTTPResponses.java]
    HTTP --> Router[Router.java]

    MAINJAVA --> MAIN[main/]
    MAIN --> Main[Main.java]

    MAINJAVA --> UTIL[util/]
    UTIL --> CompressionUtils[CompressionUtils.java]
    UTIL --> FileUtils[FileUtils.java]
    UTIL --> IOUtils[IOUtils.java]
```

### Supported HTTP methods:

- `GET`
- `POST`
- `HEAD`
- `DELETE`
- `OPTIONS`

### Standards:

- Proper **status codes**
- Correct `Content-Type`, `Content-Length`, and `Content-Encoding` headers
- `204 No Content` responses where appropriate
- Works with standard HTTP clients

## Getting Started

### Prerequisites

- Java 17+ recommended
- Maven 3.6+ installed

### Build the project

```bash
mvn clean package
```

### Run the server

```bash
java -cp target/http-server-1.0-SNAPSHOT.jar main.Main [base_directory] [port]
```

- `base_directory` → (optional) path to serve files from (default: current directory `.`)
- `port` → (optional) port number (default: `1212`)

Example:

```bash
java -cp target/http-server-1.0-SNAPSHOT.jar main.Main ./data 8080
```

### Run tests

```bash
mvn test
```

## Project Structure

```
src/main/java/http          # Core HTTP classes (request, response, router, parser)
src/main/java/handlers      # Modular HTTP handlers (FileHandler, EchoHandler, UserAgentHandler, etc.)
src/main/java/util          # Utility classes (CompressionUtils, IOUtils, FileUtils)
src/main/java/main/Main.java# Server entry point

src/test/java               # Unit and integration tests
```

## License

MIT — free for use and modification.
