package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.PushDB;
import by.fastflow.DBModels.UserDB;
import by.fastflow.DBModels.WishListDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class PushController extends ExceptionHandlerController {
    private static final String ADDRESS = Constants.DEF_SERVER + "push";

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@PathVariable(value = "user_id") long userId,
                               @RequestHeader(value = "token") String token,
                               @RequestParam(value = "device") int type,
                               @RequestBody String push_token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);

            if (!Constants.device_types.contains(type))
                throw new RestException(ErrorConstants.WRONG_DEVICE);

            session.beginTransaction();
            session.saveOrUpdate(PushDB.createNew(userId, type, push_token));
            session.getTransaction().commit();
            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}
