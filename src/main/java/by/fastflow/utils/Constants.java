package by.fastflow.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by KuSu on 24.10.2016.
 */
public class Constants {
    public final static String DEF_SERVER = "/paychi/";

    public final static int USER_PARENT = 1;
    public final static int USER_CHILD = 2;

    public static final int RELATIONSHIP_CREATE = 1;
    public static final int RELATIONSHIP_CANCEL = 2;
    public static final int RELATIONSHIP_ACCEPT = 3;

    public final static int LOGIN_TYPE_VK = 1;
    public final static int LOGIN_TYPE_FB = 2;

    public final static int DEVICE_TYPE_ANDROID = 1;
    public final static int DEVICE_TYPE_IOS = 2;

    public final static int MESSAGE_TYPE_USER = 1;
    public final static int MESSAGE_TYPE_SYSTEM = 2;

    public final static int SUCCESS_NOT_READED = 1;
    public final static int SUCCESS_READED = 2;
    public final static int SUCCESS_PRAISED = 3;

    public final static int TASK_ITEM_VISIBLE = 1;
    public final static int TASK_ITEM_IN_PROGRESS = 2;
    public final static int TASK_ITEM_DONE = 3;
    public final static int TASK_ITEM_PRAISED = 4;

    public final static int TASK_ITEM_WORK_ALL = 1;
    public final static int TASK_ITEM_WORK_NOBODY = 2;
    public final static int TASK_ITEM_WORK_ALLOWED_USERS = 3;

    public final static int TASK_LIST_ALL = 1;
    public final static int TASK_LIST_NOBODY = 2;
    public final static int TASK_LIST_ALLOWED_USERS = 3;

    public final static int WISH_ITEM_VISIBLE = 1;
    public final static int WISH_ITEM_INVISIBLE = 2;

    public final static int WISH_LIST_VISIBLE = 1;
    public final static int WISH_LIST_INVISIBLE = 2;

    public final static HashSet<Integer> device_types = new HashSet<Integer>(Arrays.asList(DEVICE_TYPE_ANDROID, DEVICE_TYPE_IOS));
    public final static HashSet<Integer> relationship_types = new HashSet<Integer>(Arrays.asList(RELATIONSHIP_ACCEPT, RELATIONSHIP_CANCEL));
    public final static HashSet<Integer> user_types = new HashSet<Integer>(Arrays.asList(USER_PARENT, USER_CHILD));
    public final static HashSet<Integer> message_types = new HashSet<Integer>(Arrays.asList(MESSAGE_TYPE_SYSTEM, MESSAGE_TYPE_USER));
    public final static HashSet<Integer> success_types = new HashSet<Integer>(Arrays.asList(SUCCESS_NOT_READED, SUCCESS_READED, SUCCESS_PRAISED));

    public final static HashSet<Integer> wishList_visibility = new HashSet<Integer>(Arrays.asList(WISH_LIST_INVISIBLE, WISH_LIST_VISIBLE));
    public final static HashSet<Integer> wishItem_visibility = new HashSet<Integer>(Arrays.asList(WISH_ITEM_INVISIBLE, WISH_ITEM_VISIBLE));
    public final static HashSet<Integer> wish_rates = new HashSet<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5));

    public final static HashSet<Integer> taskList_visibility = new HashSet<Integer>(Arrays.asList(TASK_LIST_ALL, TASK_LIST_ALLOWED_USERS, TASK_LIST_NOBODY));

    public final static HashSet<Integer> taskItem_state = new HashSet<Integer>(Arrays.asList(TASK_ITEM_VISIBLE, TASK_ITEM_IN_PROGRESS, TASK_ITEM_DONE, TASK_ITEM_PRAISED));
    public final static HashSet<Integer> taskItem_target = new HashSet<Integer>(Arrays.asList(TASK_ITEM_WORK_ALL, TASK_ITEM_WORK_ALLOWED_USERS, TASK_ITEM_WORK_NOBODY));

    public static final int PAGE_RESULT = 20;
    public static final int PAGE_RESULT_MESSAGE = 20;

    public static final int MSG_UPDATE = 1;
    public static final int MSG_CREATE = 2;
    public static final int MSG_SEND_MONEY = 3;
    public static final int MSG_NEW_USER = 4;
    public static final int MSG_OUT_ME = 5;
    public static final int MSG_CREATE_RELATIONSHIP = 6;
    public static final int MSG_DELETE_RELATIONSHIP = 7;
    public static final int MSG_UPDATE_RELATIONSHIP = 8;
    public static final int MSG_SEND_MONEY_FOR_ITEM = 9;
    public static final int MSG_CHANGE_WORK_STATE = 10;
    public static final int MSG_PRAISED = 11;

    public static long convertL(Object object) {
        return ((BigInteger) object).longValue();
    }

    public static String getMSG(int msg_type, LIST L) {
        switch (msg_type) {
            default:
            case MSG_CREATE:
                return "Create dialog \"" + L.get(0) + "\"";
            case MSG_UPDATE:
                return "Update dialog to \"" + L.get(0) + "\"";
            case MSG_SEND_MONEY:
                return "I send you " + L.get(0) + getMsg(L.get(1));
            case MSG_SEND_MONEY_FOR_ITEM:
                return "I send you " + L.get(0) + " for wish \"" + L.get(1) + "\"" + getMsg(L.get(2));
            case MSG_NEW_USER:
                return "I invite \"" + L.get(0) + "\"";
            case MSG_OUT_ME:
                return "I go out";
            case MSG_CREATE_RELATIONSHIP:
                return "I want create relationship " + getMsg(L.get(0));
            case MSG_UPDATE_RELATIONSHIP:
                return "I update relationship to state " + L.get(0);
            case MSG_DELETE_RELATIONSHIP:
                return "I delete relationship";
            case MSG_CHANGE_WORK_STATE:
                return "I change state for task \"" + L.get(0) + "\" from " + L.get(1) + " to " + L.get(2) + getMsg(L.get(3));
            case MSG_PRAISED:
                return "Good work with task \"" + L.get(0) + "\"" + getMsg(L.get(1));
        }
    }

    private static String getMsg(String inf) {
        if ((inf == null) || (inf.isEmpty()))
            return "";
        return "\n[" + inf + "]";
    }

    public static String getStringMoney(long money) {
        return getWithSpace(money / 100) + "." + get00(money % 100) + " BYN";
    }

    private static String getWithSpace(long l) {
        String res = "";
        String temp = "" + l;
        int i = 0;
        int j = temp.length() - 1;
        while (j >= 0) {
            if (i == 3) {
                i = 0;
                res = " " + res;
            }
            res = temp.charAt(j) + res;
            j--;
        }
        return res;
    }

    private static String get00(long l) {
        return l < 10 ? (l == 0 ? "" : "0" + l) : "" + l;
    }

    public static String getRelationshipMethod(int state) {
        switch (state) {
            case RELATIONSHIP_CANCEL:
                return "CANCEL";
            case RELATIONSHIP_ACCEPT:
                return "ACCEPT";
        }
        return "";
    }
}