package by.fastflow.utils;

import java.util.Set;

/**
 * Created by KuSu on 01.07.2016.
 */
public interface DataService {
    public boolean persist(String problem);
    public Set<String> getRandomData();
}
