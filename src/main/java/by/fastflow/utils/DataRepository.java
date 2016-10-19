package by.fastflow.utils;

import java.util.Set;

/**
 * Created by KuSu on 01.07.2016.
 */
public interface DataRepository<V extends DomainObject> {

    void persist(V object);

    void delete(V object);

    Set<String> getRandomData();

}