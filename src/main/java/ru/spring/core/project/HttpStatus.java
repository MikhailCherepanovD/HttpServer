package ru.spring.core.project;

public enum HttpStatus {
    OK(200,"Ok"),
    CREATED(201,"Created"),
    NO_CONTENT(204,"No Content"),
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401,"Unauthorized"),
    NOT_FOUND(404,"Not found"),
    INTERNAL_SERVER_ERROR(404,"Internal server error");

    public final int statusCode;
    public final String statusDescription;
    HttpStatus(int statusCode, String statusDescription){
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
    }
}
