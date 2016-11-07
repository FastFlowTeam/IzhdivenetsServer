package by.fastflow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KuSu on 01.07.2016.
 */
public class Ajax {
    public static Map<String, Object> successResponse(Object object) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("result", "success");
        response.put("data", object);
        return response;
    }


    public static String successResponseJson(JsonElement object) {
        JsonObject response = new JsonObject();
        response.addProperty("result", "success");
        response.add("data", object);
        return response.toString();
    }

    public static Map<String, Object> emptyResponse() {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("result", "success");
        return response;
    }
}
