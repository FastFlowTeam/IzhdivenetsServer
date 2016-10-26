package by.fastflow.utils;

import com.google.gson.JsonObject;

/**
 * Created by KuSu on 01.07.2016.
 */
public class RestException extends Exception {

    int id;
    String message;

    public RestException() {
    }

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException(Throwable cause) {
        super(cause);
    }

    public RestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RestException(String message, int id) {
        super(message);
        this.message = message;
        this.id = id;
    }

    public String getErrorJson() {
        JsonObject object = new JsonObject();
        if ((message == null)||(message.isEmpty())) {
            object.addProperty("error_message", getMessage());
            object.addProperty("error_code", ErrorConstants.NOT_TYPED_EXCEPTION);
        }else {
            object.addProperty("error_message", message);
            object.addProperty("error_code", id);
        }
        return object.toString();
    }
}