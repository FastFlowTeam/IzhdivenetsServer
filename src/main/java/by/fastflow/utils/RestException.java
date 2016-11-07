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
            case ErrorConstants.EMPTY_TOKEN:
                return "Token is empty or too long";
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
                return "User chat name is empty or too long";
            case ErrorConstants.USER_TYPE:
                return "Not set user type";
            case ErrorConstants.HAVE_SAME_RELATIONSHIP:
                return "You have relationship with this user";
            case ErrorConstants.EMPTY_CARD_MONEY:
                return "Card money amount is empty";
            case ErrorConstants.NEGATIVE_CARD_MONEY:
                return "Card money amount is negative or too long";
            case ErrorConstants.AUTH_TYPE:
                return "Wrong auth type";
            case ErrorConstants.EMPTY_DIALOG_NAME:
                return "Dialog name is empty or too long";
            case ErrorConstants.MESSAGE_DATE_CONFLICTS:
                return "Message date and time are from the future";
            case ErrorConstants.EMPTY_MESSAGE:
                return "Message is empty";
            case ErrorConstants.MESSAGE_TYPE:
                return "Message type is not defined correctly";
            case ErrorConstants.TOO_LONG_MESSAGE:
                return "Message contains more then 500 symbols";
            case ErrorConstants.EMPTY_SUCCESS_TITLE:
                return "Success title is empty or too long";
            case ErrorConstants.WRONG_SUCCESS_STATE:
                return "Success state is not defined correctly";
            case ErrorConstants.EMPTY_TASK_TITLE:
                return "Task title is empty or too long";
            case ErrorConstants.EMPTY_TASK_COST:
                return "Task cost is empty or too long";
            case ErrorConstants.WRONG_TASK_STATE:
                return "Task state is not defined correctly";
            case ErrorConstants.EMPTY_TASK_LIST_NAME:
                return "Task list name is empty or too long";
            case ErrorConstants.WRONG_TASK_LIST_VISIBILITY:
                return "Task list visibility is not defined correctly";
            case ErrorConstants.WRONG_TASK_LIST_CAN_WORK:
                return "Task list working permission type is not defined correctly";
            case ErrorConstants.EMPTY_WISH_ITEM_TITLE:
                return "Wish title is empty or too long";
            case ErrorConstants.WRONG_WISH_ITEM_VISIBILITY:
                return "Wish visibility is not defined correctly";
            case ErrorConstants.WRONG_WISH_COST:
                return "Wish cost is not defined correctly";
            case ErrorConstants.WRONG_WANT_RATE:
                return "Wish rate is not defined correctly";
            case ErrorConstants.EMPTY_WISH_LIST_NAME:
                return "Wish list name is empty or too long";
            case ErrorConstants.WRONG_WISH_LIST_VISIBILITY:
                return "Wish list visibility is not defined correctly";
            case ErrorConstants.EMPTY_PUSH_TOKEN:
                return "Push token is empty";
            case ErrorConstants.WRONG_DEVICE:
                return "Device type is not defined correctly";
            case ErrorConstants.EMPTY_NOT_READED_SUCCESS_NUMBER:
                return "Successes number is empty";
            case ErrorConstants.WRONG_NOT_READED_SUCCESS_NUMBER:
                return "Successes number is negative or too big";
            case ErrorConstants.EMPTY_NOT_READED_MESSAGES_NUMBER:
                return "Messages number is empty";
            case ErrorConstants.WRONG_NOT_READED_MESSAGES_NUMBER:
                return "Messages number is negative or too big";
            case ErrorConstants.WRONG_TEXT_SIZE:
                return "The string is too long";
            case ErrorConstants.LONG_MESSAGE_LINK:
                return "Message link is too long";
            case ErrorConstants.LONG_TOKEN:
                return "Token is too long";
            case ErrorConstants.LONG_SUCCESS_DESCRIPTION:
                return "Success description is too long";
            case ErrorConstants.LONG_SUCCESS_LINK:
                return "Success link is too long";
            case ErrorConstants.LONG_SUCCESS_PHOTO:
                return "Success photo link is too long";
            case ErrorConstants.LONG_TASK_DESCRIPTION:
                return "Task description is too long";
            case ErrorConstants.LONG_TASK_LIST_DESCRIPTION:
                return "Task list description is too long";
            case ErrorConstants.LONG_USER_PHOTO:
                return "User photo link is too long";
            case ErrorConstants.LONG_WISH_COMMENT:
                return "Wish comment is too long";
            case ErrorConstants.LONG_WISH_LINK:
                return "Wish link is too long";
            case ErrorConstants.LONG_WISH_PHOTO:
                return "Wish photo link is too long";
            case ErrorConstants.LONG_WISH_LIST_DESCRIPTION:
                return "Wish list description is too long";
        }
    }
}