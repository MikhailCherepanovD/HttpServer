package ru.spring.core.project;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final static String DELIMITER= "\r\n\r\n";
    private final static String NEW_LINE= "\r\n";
    private final static String HEADER_DELIMITER= ":";
    private final String message;
    private final HttpMethod method;
    private final String url;
    private final Map<String, String> headers;
    private final int bodyLength;
    private final String body;


    public HttpRequest(String message){
        this.message = message;
        String[] parts = message.split(DELIMITER);// две части будут: header и body
        String head = parts[0];
        String[] headers = head.split(NEW_LINE);

        String[] firstLine =   headers[0].split(" ");
        method = HttpMethod.valueOf(firstLine[0]);// сопоставит значение перечисления по его имени

        url = firstLine[1];


        Map<String, String> tempMap = new HashMap<>();
        for (int i = 1; i < headers.length; i++) {
            String[] headerPart = headers[i].split(HEADER_DELIMITER, 2);
            tempMap.put(headerPart[0].trim(), headerPart[1].trim());
        }
        this.headers = Collections.unmodifiableMap(tempMap);
        String tempLength = this.headers.get("Content-Length");
        this.bodyLength = tempLength==null ?0: Integer.parseInt(tempLength);
        this.body = parts.length >1 ? parts[1].trim().substring(0,bodyLength) : "";

    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
    public String getMessage() {
        return message;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
