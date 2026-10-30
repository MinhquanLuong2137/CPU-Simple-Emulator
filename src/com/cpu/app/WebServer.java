package com.cpu.app;

import com.cpu.assembler.SimpleAssembler;
import com.cpu.hardware.CPU;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class WebServer {
    private static final int PORT = 8080;
    private static final Path WEB_ROOT = Path.of("web").toAbsolutePath().normalize();
    private static final Object CPU_LOCK = new Object();
    private static final int PORT_FALLBACK_LIMIT = 10;
    private static CPU cpu = new CPU();

    public static void main(String[] args) throws IOException {
        HttpServer server = createServer();
        server.createContext("/api/assemble", WebServer::assemble);
        server.createContext("/api/step", WebServer::step);
        server.createContext("/api/reset", WebServer::reset);
        server.createContext("/", WebServer::serveStaticFile);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        int actualPort = server.getAddress().getPort();
        System.out.println("CPU web interface listening at http://localhost:" + actualPort);
    }

    private static HttpServer createServer() throws IOException {
        BindException lastBindException = null;
        for (int port = PORT; port < PORT + PORT_FALLBACK_LIMIT; port++) {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                if (port != PORT) {
                    System.out.println("Port " + PORT + " is already in use; using port " + port + " instead.");
                }
                return server;
            } catch (BindException e) {
                lastBindException = e;
            }
        }
        throw new IOException(
                "Ports " + PORT + "-" + (PORT + PORT_FALLBACK_LIMIT - 1)
                        + " are already in use. Stop another server or free one of these ports.",
                lastBindException);
    }

    private static void assemble(HttpExchange exchange) throws IOException {
        if (!requireMethod(exchange, "POST")) {
            return;
        }

        try {
            String source = new String(readRequestBody(exchange), StandardCharsets.UTF_8);
            int[] program = SimpleAssembler.assemble(source);
            if (program.length > 256) {
                sendJson(exchange, 400, "{\"error\":\"Program exceeds the 256-byte RAM capacity.\"}");
                return;
            }

            synchronized (CPU_LOCK) {
                cpu = new CPU();
                cpu.loadProgram(program, 0);
                sendState(exchange);
            }
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } catch (IOException e) {
            sendJson(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private static void step(HttpExchange exchange) throws IOException {
        if (!requireMethod(exchange, "POST")) {
            return;
        }
        synchronized (CPU_LOCK) {
            cpu.step();
            sendState(exchange);
        }
    }

    private static void reset(HttpExchange exchange) throws IOException {
        if (!requireMethod(exchange, "POST")) {
            return;
        }
        synchronized (CPU_LOCK) {
            cpu = new CPU();
            sendState(exchange);
        }
    }

    private static void sendState(HttpExchange exchange) throws IOException {
        StringBuilder json = new StringBuilder(1600);
        json.append('{');
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append("\"R").append(i).append("\":")
                    .append(cpu.getReg().getRegister(i));
        }
        json.append(",\"PC\":").append(cpu.getReg().getProgramCounter())
                .append(",\"ZeroFlag\":").append(cpu.getReg().isZeroFlag())
                .append(",\"running\":").append(cpu.isRunning())
                .append(",\"RAM\":[");
        for (int i = 0; i < 256; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(cpu.getMemory().read(i));
        }
        json.append("]}");
        sendJson(exchange, 200, json.toString());
    }

    private static boolean requireMethod(HttpExchange exchange, String method) throws IOException {
        if (method.equalsIgnoreCase(exchange.getRequestMethod())) {
            return true;
        }
        exchange.getResponseHeaders().set("Allow", method);
        sendJson(exchange, 405, "{\"error\":\"Method not allowed.\"}");
        return false;
    }

    private static byte[] readRequestBody(HttpExchange exchange) throws IOException {
        byte[] body = exchange.getRequestBody().readNBytes(1_048_577);
        if (body.length > 1_048_576) {
            throw new IllegalArgumentException("Request body exceeds the 1 MiB limit.");
        }
        return body;
    }

    private static void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static void serveStaticFile(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())
                && !"HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET, HEAD");
            sendJson(exchange, 405, "{\"error\":\"Method not allowed.\"}");
            return;
        }

        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }
        Path file = WEB_ROOT.resolve(requestPath.substring(1)).normalize();
        if (!file.startsWith(WEB_ROOT) || !Files.isRegularFile(file)) {
            sendJson(exchange, 404, "{\"error\":\"Not found.\"}");
            return;
        }

        String contentType = Files.probeContentType(file);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        if (file.getFileName().toString().endsWith(".js")) {
            contentType = "text/javascript; charset=utf-8";
        } else if (file.getFileName().toString().endsWith(".html")) {
            contentType = "text/html; charset=utf-8";
        }

        byte[] contents = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.sendResponseHeaders(200, "HEAD".equalsIgnoreCase(exchange.getRequestMethod()) ? -1 : contents.length);
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseBody().write(contents);
        }
        exchange.close();
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "Unexpected error.";
        }
        StringBuilder escaped = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
            }
        }
        return escaped.toString();
    }
}
