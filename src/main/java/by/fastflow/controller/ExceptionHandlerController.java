package by.fastflow.controller;

import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.UpdatableDB;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import by.fastflow.utils.RestException;

/**
 * Created by KuSu on 01.07.2016.
 */
@Controller
public abstract class ExceptionHandlerController {

    private static final Logger LOG = Logger.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(RestException.class)
    public
    @ResponseBody
    String handleException(RestException e) {
        LOG.error("Ошибка: " + e.getMessage(), e);
        return e.getErrorJson();
    }
}