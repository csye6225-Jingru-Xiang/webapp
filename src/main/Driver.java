package src.main;

import java.io.*;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.spi.HttpServerProvider;
import com.sun.net.httpserver.*;

public class Driver{
    public static void serverStart() throws IOException {
        HttpServerProvider provider = HttpServerProvider.provider();
        HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(8083), 100);

        httpserver.createContext("/", httpExchange -> {
            Headers responseHeaders = httpExchange.getResponseHeaders();
            responseHeaders.set("content-type", "application/json;charset=utf-8");
            httpExchange.sendResponseHeaders(200, -1);
            httpExchange.close();
        });
        httpserver.setExecutor(null);
        httpserver.start();
        System.out.println("server started");
    }

    public static void main(String[] args) throws IOException {
        serverStart();
    }
}