package ru.spring.core.project;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class Resource {
    private final String segmentPath;
    private JsonObject data;
    private HashMap<String,Resource> childResources;
    private boolean flagRecourceIsExist;
    public Resource(String segmentPath){
        this.segmentPath = segmentPath;
        this.data = new JsonObject();
        childResources  = new HashMap<>();
        flagRecourceIsExist=false;
    }
    public Resource(String segmentPath, JsonObject data){
        this.segmentPath = segmentPath;
        this.data = data;
        childResources  = new HashMap<>();
        flagRecourceIsExist=false;
    }


    public String getSegmentPath() {
        return segmentPath;
    }

    public JsonObject getData() {
        return data;
    }
    public void setData(JsonObject data) {
        this.data = data;
        flagRecourceIsExist=true;
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
        return flagRecourceIsExist;
    }
    public void deleteResource(){
        this.flagRecourceIsExist = false;
        this.data = new JsonObject();
    }

}
