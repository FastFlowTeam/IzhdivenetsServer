package by.fastflow.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by KuSu on 21.12.2016.
 */
public class Authorisation {
    String id;
    int auth;
    String salt;
    String name;
    int type;
    long time;

    public int getAuth() {
        return auth;
    }

    public String getId() {
        return id;
    }

    public String getSalt() {
        return salt;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public void validate() throws RestException {
        if (Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() + Constants.DELTA_TIME > getTime())
            throw new RestException(ErrorConstants.INVALIDE_TIME);
        if (!getSalt().equals("fasti"))
            throw new RestException(ErrorConstants.INVALIDE_SALT);
    }
}
