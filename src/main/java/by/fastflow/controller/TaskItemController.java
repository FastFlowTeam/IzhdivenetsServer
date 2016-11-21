package by.fastflow.controller;

import by.fastflow.utils.Constants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class TaskItemController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "taskitem";

    // TODO: 21.11.2016 создание задачи
    // TODO: 21.11.2016 обновить задачу
    // TODO: 21.11.2016 обновить разрешения
    // TODO: 21.11.2016 удалить задачу
    // TODO: 21.11.2016 просмотр задач
    // TODO: 21.11.2016 взять задачу
    // TODO: 21.11.2016 закрыть задачу
    // TODO: 21.11.2016 наградить за задачу

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}
