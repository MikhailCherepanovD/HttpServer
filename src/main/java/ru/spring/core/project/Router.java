package ru.spring.core.project;

import com.google.gson.JsonObject;

public class Router {
    private Resource rootResource;  // root of tree resources
    public Router(){
        JsonObject data = new JsonObject();
        data.addProperty("data","Initial data");
        rootResource = new Resource("",data);
        rootResource.addDefaultHandler();
    }
    public boolean urlIsExist(String url){
        String[] segments = url.split("/");
        Resource currResource = rootResource;
        for(var segment:segments){
            if(!segment.equals("")){
                if(currResource.resourceIsExistBySegmentPath(segment)) {
                    currResource = currResource.getChildResourceBySegmentPath(segment);
                }
                else{
                    return false;
                }
            }
        }
        return true;
    }

    public JsonObject getDataByUrl(String url){
        Resource currResource = getResourceByUrl(url);
        return currResource.getData();
    }
    public void setDataByUrl(String url, JsonObject data){
        Resource currResource = getResourceByUrl(url);
        currResource.setData(data);
    }

    public void createUrlDefaultHandler(String url){
        Resource currResource = getResourceByUrl(url);
        currResource.addDefaultHandler();
    }

    public void createUrlCustomHandler(String url,HttpHandler handler){
        Resource currResource = getResourceByUrl(url);
        currResource.addCustomHandler(handler);
    }
    public void deleteResourceByUrl(String url){
        Resource currResource = getResourceByUrl(url);
        currResource.deleteResource();
    }
    public Resource getResourceByUrl(String url){

        String[] segments = url.split("/");
        Resource currResource = rootResource;
        for(var segment:segments){
            if(!segment.isEmpty())
                currResource = currResource.getChildResourceBySegmentPath(segment);//
        }
        return currResource;
    }

    public HttpResponse handle(HttpRequest request){
        String url = request.getUrl();
        HttpResponse response = new HttpResponse();
        if(!HttpMethod.contains(request.getMethod().toString())){
            response.setStatusCode(HttpStatus.BAD_REQUEST.statusCode);
            response.setStatus(HttpStatus.BAD_REQUEST.statusDescription);
            return response;
        }
        if(!urlIsExist(url)) { // указанного URL не существует
            response.setStatusCode(HttpStatus.NOT_FOUND.statusCode);
            response.setStatus(HttpStatus.NOT_FOUND.statusDescription);
            return response;
        }
        Resource resource = getResourceByUrl(url);
        return resource.getHandler().handle(request,resource);
    }
}
