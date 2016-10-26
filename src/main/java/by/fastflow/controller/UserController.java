package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.UserDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.RestException;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by KuSu on 01.07.2016.
 */
@RestController
public class UserController extends ExceptionHandlerController {

    private static final Logger LOG = Logger.getLogger(UserController.class);
    private static final String USER_ADDRESS = Constants.DEF_SERVER + "user";

    @RequestMapping(value = USER_ADDRESS + "/add", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@RequestBody UserDB user) throws RestException {
        try {
            user.validate();
            user.setToken(user.hashCode() + "");
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            user.setUserNextId(session);
            user.updateToken();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            session.close();
            return Ajax.successResponse(user);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = USER_ADDRESS + "/update/{userId}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> update(@RequestBody UserDB user) throws RestException {
        try {
            user.validate();
            user.setToken(user.hashCode() + "");
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            user.setUserNextId(session);
            user.updateToken();
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            session.close();
            return Ajax.successResponse(user);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }


    @RequestMapping(USER_ADDRESS + "/asd/")
    String home() {
        return "Hello World!";
    }
}