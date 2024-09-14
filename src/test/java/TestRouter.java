import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import ru.spring.core.project.HttpServer;
import ru.spring.core.project.Router;

import java.io.*;

public class TestRouter {
    @Test
    public void testRouter1(){
        Router router = new Router();
        router.createUrl("/aaa/bbb/ccc");
        router.createUrl("/api/mvp");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("data","data in url /aaa/bbb");

        router.setDataByUrl("/aaa/bbb",jsonObject);
        jsonObject.addProperty("data","data in url /aaa/bbb/ccc");
        router.setDataByUrl("/aaa/bbb/ccc",jsonObject);
        System.out.println(router.getDataByUrl("").toString());
        System.out.println(router.getDataByUrl("aaa").toString());
        System.out.println(router.getDataByUrl("aaa/bbb").toString());
        System.out.println(router.getDataByUrl("aaa/bbb/ccc").toString());
        System.out.println(router.getDataByUrl("api").toString());
        System.out.println(router.getDataByUrl("api/mvp").toString());
        jsonObject.addProperty("data","minimal viable project");
        router.setDataByUrl("api/mvp",jsonObject);
        System.out.println(router.getDataByUrl("api/mvp").toString());

    }
}
