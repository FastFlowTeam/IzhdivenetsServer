package by.fastflow.utils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by KuSu on 24.10.2016.
 */
public class Constants {
    public final static String DEF_SERVER = "/paychi/";

    public final static int USER_PARENT = 1;
    public final static int USER_CHILD = 2;

    public static final long RELATIONSHIP_CREATE = 1;
    public static final long RELATIONSHIP_CANCEL = 2;
    public static final long RELATIONSHIP_ACCEPT = 3;

    public final static int LOGIN_TYPE_VK = 1;
    public final static int LOGIN_TYPE_FB = 2;

    public final static int MESSAGE_TYPE_USER = 1;
    public final static int MESSAGE_TYPE_SYSTEM = 2;

    public final static int SUCCESS_NOT_READED = 1;
    public final static int SUCCESS_READED = 2;

    public final static int TASK_ITEM_VISIBLE = 1;
    public final static int TASK_ITEM_INVISIBLE = 2;
    public final static int TASK_ITEM_IN_PROGRESS = 3;
    public final static int TASK_ITEM_DONE = 4;

    public final static int TASK_LIST_ALL = 1;
    public final static int TASK_LIST_NOBODY = 2;
    public final static int TASK_LIST_ALLOWED_USERS = 1;

    public final static int WISH_ITEM_VISIBLE = 1;
    public final static int WISH_ITEM_INVISIBLE = 1;

    public final static HashSet<int> wish_rates = new HashSet<int>(Arrays.asList(0,1,2,3,4,5));

    public final static HashSet<int> device_types = new HashSet<int>(Arrays.asList(1,2,3));


}