package ru.spring.core.project;

import com.google.gson.JsonObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private Resource rootResource;  // root of tree resources
    public Router(){
        JsonObject data = new JsonObject();
        data.addProperty("data","Initial data");
        rootResource = new Resource("",data);
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

    public void createUrl(String url){
        Resource currResource = getResourceByUrl(url);
    }
    public void deleteResourceByUrl(String url){
        Resource currResource = getResourceByUrl(url);
        currResource.deleteResource();
    }
    public Resource getResourceByUrl(String url){
       /* while(!url.isEmpty() && url.charAt(0)=='/' ){
            url = url.substring(1);
        }
        while(!url.isEmpty() &&  url.charAt(url.length()-1)=='/'  ){
            url = url.substring(0,url.length()-1);
        }*/
        String[] segments = url.split("/");
        Resource currResource = rootResource;
        for(var segment:segments){
            if(!segment.isEmpty())
                currResource = currResource.getChildResourceBySegmentPath(segment);//
        }
        return currResource;
    }


}
