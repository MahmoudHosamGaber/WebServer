# HTTP Server from Scratch (Java)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![HTTP/1.1](https://img.shields.io/badge/HTTP%2F1.1-Standard-blue?style=for-the-badge)
![Multithreading](https://img.shields.io/badge/Concurrency-Multithreaded-green?style=for-the-badge)
![GZIP](https://img.shields.io/badge/Compression-GZIP-blue?style=for-the-badge)

## Project Overview

This project is a lightweight, multi-threaded **HTTP/1.1 Server** built entirely from scratch in Java. 

The goal was to **demystify web frameworks** (like Spring Boot or Express) by implementing the core networking and protocol logic manually. This server handles TCP connections, parses raw HTTP byte streams, manages concurrent clients, and serves dynamic and static content. Express was the inspiration for request handling syntax as shown in [Routing System](#custom-routing-system).

---

## How to Run

1. **Compile the project:**
   ```bash
   javac -d target src/main/java/*.java
   ```

2. **Start the server:**
   ```bash
   java -cp target Main --directory ./files
   ```

3. **Test it:**
   ```bash
   curl -v http://localhost:4221/echo/hello-world
   ```

## Key Technical Learnings

### 1. Network Programming & TCP/IP
- **Socket Management:** Implemented raw `ServerSocket` and `Socket` handling to establish and manage TCP connections.
- **Stream Processing:** Managed `InputStream` and `OutputStream` directly to read request bytes and write response payloads, ensuring proper encoding (UTF-8) and buffer management.

### 2. HTTP Protocol Implementation
- **Request Parsing:** Wrote a custom parser to decompose raw HTTP requests into Methods (GET, POST), Headers, Paths, and Body content.
- **Response Formatting:** Manually constructed valid HTTP/1.1 responses, including Status Lines, Headers (Content-Type, Content-Length), and Body.
- **Compression:** Implemented **GZIP compression** for bandwidth optimization, dynamically encoding responses based on the client's `Accept-Encoding` header.

### 3. Software Architecture
- **Router Pattern:** Designed a flexible `Router` class using functional interfaces (`BiConsumer<Request, Response>`) to map URL paths to handler logic cleanly.
- **Separation of Concerns:** Decoupled networking logic (`Main`), protocol parsing (`Request`/`Response`), and business logic (`RequestHandler`), resulting in a maintainable and testable codebase.

---

## Features

- **Dynamic Routing:** Support for static paths and parameterized routes (e.g., `/echo/:text`).
- **File Server:** Capabilities to read and write files to the server's local storage via API.
- **GZIP Compression:** Automatic compression of response bodies when supported by the client.

---

## Code Highlights

### Custom Routing System
Instead of hardcoding `if/else` blocks, I implemented a functional routing system:

```java
// Main.java
Router.get("/echo/:text", (req, res) -> {
    res.text(req.getParams("text"));
});

Router.post("/files/:fileName", (req, res) -> {
    FileHandler.writeFile(req.getParams("fileName"), req.getBody());
    res.setStatusCode(201);
});
```

### Request Handling Lifecycle
The `RequestHandler` orchestrates the lifecycle of a connection:
1. Accepts the `Socket`.
2. Parses the `InputStream` into a `Request` object.
3. Matches the route.
4. Executes the logic.
5. Writes the `Response` to the `OutputStream`.

---

## Future Improvements & Limitations

While this server is functional, it is designed for educational purposes. In a production environment, I would address the following:

- **Thread Pooling:** Currently, the server creates a new `Thread` for every request. To prevent resource exhaustion under high load, I would implement a `ExecutorService` (Thread Pool) to reuse threads.
- **Memory Efficiency:** Large files are currently read entirely into memory before sending. Implementing **Chunked Transfer Encoding** or streaming the file directly to the socket would allow serving files larger than available RAM.
- **Security:** The server currently runs over plain HTTP. Adding **TLS/SSL** support (via `SSLServerSocket`) would be critical for secure communication.
- **HTTP/2 Support:** Upgrading to HTTP/2 would improve performance through multiplexing, though it requires a significantly more complex framing layer.
