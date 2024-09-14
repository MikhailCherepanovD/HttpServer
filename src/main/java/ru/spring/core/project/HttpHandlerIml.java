package ru.spring.core.project;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.Map;

public class HttpHandlerIml implements HttpHandler{
    Router router;
    public HttpHandlerIml(Router router){
        this.router = router;
    }

    private HttpResponse handleGet(HttpRequest request){
        HttpResponse response = new HttpResponse();
        String url = request.getUrl();
        if(!router.urlIsExist(url)) { // указанного URL не существует
            response.setStatusCode(HttpStatus.NOT_FOUND.statusCode);
            response.setStatus(HttpStatus.NOT_FOUND.statusDescription);
            return response;
        }
        JsonObject jsonObject = router.getDataByUrl(url);
        if(jsonObject.size() != 0){
            response.setStatusCode(HttpStatus.OK.statusCode);
            response.setStatus(HttpStatus.OK.statusDescription);
            response.setBody(jsonObject.toString());
        }else{
            response.setStatusCode(HttpStatus.NO_CONTENT.statusCode);
            response.setStatus(HttpStatus.NO_CONTENT.statusDescription);
        }
        return response;
    }
    private HttpResponse handlePost(HttpRequest request){
        HttpResponse response = new HttpResponse();
        String url = request.getUrl();
        String data = request.getBody();

        JsonObject jsonObjectFromRequest;
        try{
            JsonElement jsonElement = JsonParser.parseString(data);
            jsonObjectFromRequest = jsonElement.getAsJsonObject();
        }
        catch (JsonSyntaxException e){
            response.setStatusCode(HttpStatus.BAD_REQUEST.statusCode);
            response.setStatus(HttpStatus.BAD_REQUEST.statusDescription);
            return response;
        }

        if(!router.urlIsExist(url)) {
            response.setStatusCode(HttpStatus.NOT_FOUND.statusCode);
            response.setStatus(HttpStatus.NOT_FOUND.statusDescription);
            return response;
        }

        if(router.getResourceByUrl(url).resourceIsExist()){
            response.setStatusCode(HttpStatus.OK.statusCode);
            response.setStatus(HttpStatus.OK.statusDescription);
        }else{
            response.setStatusCode(HttpStatus.CREATED.statusCode);
            response.setStatus(HttpStatus.CREATED.statusDescription);
        }

        router.setDataByUrl(url,jsonObjectFromRequest);
        response.setBody(jsonObjectFromRequest.toString());
        response.addHeader("Location",url);

        return response;
    }


    private HttpResponse handlePut(HttpRequest request){
       return handlePost(request);// как я понял в моей реализации методы выполняются одинакого.
    }

    private JsonObject updateJsonObject(JsonObject jsonObjectOld, JsonObject jsonObjectFromRequest) {
        for (Map.Entry<String, JsonElement> entry : jsonObjectFromRequest.entrySet()) {
            jsonObjectOld.add(entry.getKey(), entry.getValue());
        }
        return jsonObjectOld;
    }

    private HttpResponse handlePatch(HttpRequest request){
        HttpResponse response = new HttpResponse();
        String url = request.getUrl();
        String data = request.getBody();

        JsonObject jsonObjectFromRequest;
        try{
            JsonElement jsonElement = JsonParser.parseString(data);
            jsonObjectFromRequest = jsonElement.getAsJsonObject();
        }
        catch (JsonSyntaxException e){
            response.setStatusCode(HttpStatus.BAD_REQUEST.statusCode);
            response.setStatus(HttpStatus.BAD_REQUEST.statusDescription);
            return response;
        }

        if(!router.urlIsExist(url)) {
            response.setStatusCode(HttpStatus.NOT_FOUND.statusCode);
            response.setStatus(HttpStatus.NOT_FOUND.statusDescription);
            return response;
        }

        JsonObject jsonObjectOld = router.getDataByUrl(url);
        if(router.getResourceByUrl(url).resourceIsExist()){
            response.setStatusCode(HttpStatus.OK.statusCode);
            response.setStatus(HttpStatus.OK.statusDescription);
            jsonObjectFromRequest  = updateJsonObject(jsonObjectOld,jsonObjectFromRequest);
        }else{
            response.setStatusCode(HttpStatus.CREATED.statusCode);
            response.setStatus(HttpStatus.CREATED.statusDescription);
        }
        router.setDataByUrl(url,jsonObjectFromRequest);
        response.setBody(jsonObjectFromRequest.toString());
        response.addHeader("Location",url);
        return response;
    }

    private HttpResponse handleDelete(HttpRequest request){
        HttpResponse response = new HttpResponse();
        String url = request.getUrl();
        if(!router.urlIsExist(url)) { // указанного URL не существует
            response.setStatusCode(HttpStatus.NOT_FOUND.statusCode);
            response.setStatus(HttpStatus.NOT_FOUND.statusDescription);
            return response;
        }
        router.deleteResourceByUrl(url);
        response.setStatusCode(HttpStatus.NO_CONTENT.statusCode);
        response.setStatus(HttpStatus.NO_CONTENT.statusDescription);
        return response;
    }

    public HttpResponse handle(HttpRequest request) throws RuntimeException{
        HttpResponse retResponse;
        switch (request.getMethod()){
            case GET -> retResponse = handleGet(request);
            case PUT -> retResponse = handlePut(request);
            case POST -> retResponse = handlePost(request);
            case PATCH -> retResponse = handlePatch(request);
            case DELETE -> retResponse = handleDelete(request);
            default -> throw new RuntimeException("Unknown command");
        }
        return retResponse;
    }
}


