package by.fastflow.utils;

import org.hibernate.Session;

/**
 * Created by KuSu on 30.10.2016.
 */
public interface NextableId {
    void setNextId(Session session);
}
