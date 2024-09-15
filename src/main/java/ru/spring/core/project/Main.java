package ru.spring.core.project;

public class Main {
    public static void main(String[] args) {

        HttpServer server = new HttpServer("127.0.0.1",8081);
        server.getRouter().createUrlDefaultHandler("api/user");
        server.bootstrap();

    }
}