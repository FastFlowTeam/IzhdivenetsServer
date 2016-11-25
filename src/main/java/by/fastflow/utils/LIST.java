package by.fastflow.utils;

import java.util.ArrayList;

/**
 * Created by KuSu on 25.11.2016.
 */
public class LIST {
    ArrayList<String> list = new ArrayList<>();

    public LIST add(String s) {
        list.add(s);
        return this;
    }

    public String get(int i) {
        if (i < list.size())
            return list.get(i);
        return "";
    }
}
