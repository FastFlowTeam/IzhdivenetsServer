package by.fastflow.utils;

import org.hibernate.Session;

/**
 * Created by KuSu on 30.10.2016.
 */
public abstract class Validatable<T> {
    public abstract T validate() throws RestException;
}
