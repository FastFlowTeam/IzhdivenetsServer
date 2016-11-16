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

    public final static int MESSAGE_TYPE_USER = 1;
    public final static int MESSAGE_TYPE_SYSTEM = 2;

    public final static int SUCCESS_NOT_READED = 1;
    public final static int SUCCESS_READED = 2;
    public final static int SUCCESS_PRAISED = 3;

    public final static int TASK_ITEM_VISIBLE = 1;
    public final static int TASK_ITEM_INVISIBLE = 2;
    public final static int TASK_ITEM_IN_PROGRESS = 3;
    public final static int TASK_ITEM_DONE = 4;

    public final static int TASK_LIST_ALL = 1;
    public final static int TASK_LIST_NOBODY = 2;
    public final static int TASK_LIST_ALLOWED_USERS = 1;

    public final static int WISH_ITEM_VISIBLE = 1;
    public final static int WISH_ITEM_INVISIBLE = 1;

    public final static HashSet<Integer> wish_rates = new HashSet<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5));
    public final static HashSet<Integer> device_types = new HashSet<Integer>(Arrays.asList(1, 2, 3));
    public final static HashSet<Integer> relationship_types = new HashSet<Integer>(Arrays.asList(RELATIONSHIP_ACCEPT, RELATIONSHIP_CANCEL, RELATIONSHIP_CREATE));
    public final static HashSet<Integer> user_types = new HashSet<Integer>(Arrays.asList(USER_PARENT, USER_CHILD));
    public final static HashSet<Integer> message_types = new HashSet<Integer>(Arrays.asList(MESSAGE_TYPE_SYSTEM, MESSAGE_TYPE_USER));
    public final static HashSet<Integer> success_types = new HashSet<Integer>(Arrays.asList(SUCCESS_NOT_READED, SUCCESS_READED, SUCCESS_PRAISED));


    public static final int PAGE_RESULT = 20;
    public static final int PAGE_RESULT_MESSAGE = 20;

    public static final int MSG_UPDATE = 1;
    public static final int MSG_CREATE = 2;
    public static final int MSG_SEND_MONEY = 3;
    public static final int MSG_NEW_USER = 4;
    public static final int MSG_OUT_ME = 5;

    public static long convertL(Object object) {
        return ((BigInteger) object).longValue();
    }

    public static String getMSG(int msg_type, String inf) {
        switch (msg_type) {
            default:
            case MSG_CREATE:
                return "Create dialog \"" + inf + "\"";
            case MSG_UPDATE:
                return "Update dialog to \"" + inf + "\"";
            case MSG_SEND_MONEY:
                return "I send you money: " + inf;
            case MSG_NEW_USER:
                return "I invite \"" + inf+"\"";
            case MSG_OUT_ME:
                return "I go out";
        }
    }

    public static String getStringMoney(long money) {
        return getWithSpace(money / 100) + "." + get00(money % 100) + " BYN";
    }

    private static String getWithSpace(long l) {
        String res = "";
        String temp = "" + l;
        int i = 0;
        int j = temp.length()-1;
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
}