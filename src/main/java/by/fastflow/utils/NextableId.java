package by.fastflow.utils;

import org.hibernate.Session;

/**
 * Created by KuSu on 30.10.2016.
 */
public abstract class NextableId<T> {
    public  abstract T setNextId(Session session);
}
