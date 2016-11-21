package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.UserDB;
import by.fastflow.DBModels.WishListDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class TaskListController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "tasklist";

    // TODO: 21.11.2016 создание списка
    // TODO: 21.11.2016 обновить список
    // TODO: 21.11.2016 обновить разрешения
    // TODO: 21.11.2016 удалить список
    // TODO: 21.11.2016 просмотр списков - ребенок
    // TODO: 21.11.2016 просмотр списков - родитель

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}
