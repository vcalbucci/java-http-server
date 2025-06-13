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
```mindmap
  root((src/)):::root
    main/java/:::main
      exceptions/:::folder
      handlers/:::folder
      http/:::folder
      main/:::folder
      util/:::folder

%% Custom styles
classDef root fill:#e5ffe5,stroke:#222,stroke-width:3px,font-size:20px,font-weight:bold;
classDef main fill:#f2f2ff,stroke:#4a4aaf,stroke-width:2px,font-size:17px,font-weight:bold;
classDef folder fill:#f9f9f9,stroke:#333,stroke-width:2px,font-size:15px,font-weight:bold;

%% Assign styles
class root root;
class "main/java/" main;
class exceptions,handlers,http,main,util folder;
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
