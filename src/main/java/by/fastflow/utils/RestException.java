package by.fastflow.utils;

import com.google.gson.JsonObject;

/**
 * Created by KuSu on 01.07.2016.
 */
public class RestException extends Exception {

    int id;

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

    public RestException(int id) {
        super("my error");
        this.id = id;
    }

    public String getErrorJson() {
        JsonObject object = new JsonObject();
        if (id < 1){
            object.addProperty("error_message", getMessage());
            object.addProperty("error_code", ErrorConstants.NOT_TYPED_EXCEPTION);
        }else{
            object.addProperty("error_message", getMyMessage(id));
            object.addProperty("error_code", id);
        }
        return object.toString();
    }

    private String getMyMessage(int id) {
        switch (id){
            default:
            case ErrorConstants.NOT_TYPED_EXCEPTION:
                return "Not typed error";
            case ErrorConstants.NOT_CORRECT_TOKEN:
                return "Not correct token";
            case ErrorConstants.NOT_HAVE_GID:
                return "No such gId";
            case ErrorConstants.NOT_HAVE_ID:
                return "No such user";
            case ErrorConstants.PERMISSION_BY_TOKEN:
                return "Not have permission for this";
            case ErrorConstants.SAME_TYPE:
                return "Both user same type";
            case ErrorConstants.USER_CHAT_NAME:
                return "Not have chat name";
            case ErrorConstants.USER_TYPE:
                return "Not set user type";
            case ErrorConstants.HAVE_SAME_RELATIONSHIP:
                return "You have relationship with this user";
        }
    }
}