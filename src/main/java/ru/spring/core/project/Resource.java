package ru.spring.core.project;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class Resource {
    private final String segmentPath;
    private JsonObject data;
    private HashMap<String,Resource> childResources;
    private boolean flagResourceIsExist;

    public HttpHandler getHandler() {
        return handler;
    }

    HttpHandler handler;

    public Resource(String segmentPath){
        this.segmentPath = segmentPath;
        this.data = new JsonObject();
        childResources  = new HashMap<>();
        flagResourceIsExist =false;
        handler = new HttpHandlerDefault();
    }
    public Resource(String segmentPath, JsonObject data){
        this.segmentPath = segmentPath;
        this.data = data;
        childResources  = new HashMap<>();
        handler = new HttpHandlerDefault();
        flagResourceIsExist =true;
    }


    public String getSegmentPath() {
        return segmentPath;
    }

    public JsonObject getData() {
        return data;
    }
    public void setData(JsonObject data) {
        this.data = data;
        flagResourceIsExist = true;
    }

    boolean resourceIsExistBySegmentPath( String segmentPath ){
        return childResources.containsKey(segmentPath);
    }
    Resource getChildResourceBySegmentPath(String segmentPath){
        if(childResources.containsKey(segmentPath)){
            return childResources.get(segmentPath);
        }
        Resource newResource = new Resource(segmentPath);
        childResources.put(segmentPath,newResource);
        return newResource;
    }
    public boolean resourceIsExist(){
        return flagResourceIsExist;
    }
    public void deleteResource(){
        this.flagResourceIsExist = false;
        this.data = new JsonObject();
    }
    public void addDefaultHandler(){
        handler = new HttpHandlerDefault();
    }
    public void addCustomHandler(HttpHandler handler){
        this.handler = handler;
    }
}
