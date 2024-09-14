package ru.spring.core.project;

public interface HttpHandler {
    HttpResponse handle(HttpRequest request);
}
