package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.SuccessDB;
import by.fastflow.DBModels.UserDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import com.vk.api.sdk.objects.users.UserLim;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 01.07.2016.
 */
@RestController
public class SuccessController extends ExceptionHandlerController<SuccessDB> {

    private static final String ADDRESS = Constants.DEF_SERVER + "success";

    @RequestMapping(value = ADDRESS + "/update/{success_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> update(@PathVariable(value = "success_id") Long successId,
                               @RequestBody SuccessDB success,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            SuccessDB up = success.updateInBDWithToken(session, SuccessDB.getSuccess(session, successId), token);
            session.close();
            return Ajax.successResponse(up);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> create(@PathVariable(value = "user_id") Long userId,
                               @RequestBody SuccessDB success,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (!user.isChild())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);
            success.validate();
            success.setUserId(userId);
            success.setState(Constants.SUCCESS_NOT_READED);
            success.setNextId(session);

            session.beginTransaction();
            session.save(success);
            session.getTransaction().commit();
            session.close();
            return Ajax.successResponse(success);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private String allMy(Session session, UserDB user, int START_NUM) {
        List<SuccessDB> list = session.createQuery("from SuccessDB order by successId DESC where userId = " + user.getUserId()).setFirstResult(START_NUM).setMaxResults(Constants.PAGE_RESULT).list();
        JsonArray array = new JsonArray();
        for (SuccessDB item : list)
            array.add(SuccessDB.getJson(item));
        session.close();
        return Ajax.successResponseJson(array);
    }

    private String allNotMy(Session session, UserDB user, int START_NUM) {
        List<SuccessDB> list = session.createQuery("from SuccessDB order by successId DESC where userId = " + user.getUserId()).setFirstResult(START_NUM).setMaxResults(Constants.PAGE_RESULT).list();
        JsonArray array = new JsonArray();
        for (SuccessDB item : list) {
            array.add(SuccessDB.getJson(item));
            if (item.isNotRead()) {
                item.read();
                session.beginTransaction();
                session.update(item);
                session.getTransaction().commit();
            }
        }
        // TODO: 10.11.2016 прочитать их все в нот рид
        session.close();
        return Ajax.successResponseJson(array);
    }

//    private String allNotMy(Session session, UserDB user, int START_NUM) {
//        List<Object[]> objects = getMyChild(session, user.getUserId());
//        List<SuccessDB> list = new ArrayList<>();
//        if (objects.size() > 0)
//            list.addAll(session.createQuery(
//                    "from SuccessDB order by successId DESC where userId = " + getStringIds(objects, user.getUserId()))
//                    .setFirstResult(START_NUM).setMaxResults(Constants.PAGE_RESULT).list());
//        JsonArray array = new JsonArray();
//        for (SuccessDB item : list)
//            array.add(SuccessDB.getJson(item));
//        session.close();
//        // TODO: 10.11.2016 прочитать их все и обновить в этой табле и в нот рид
//        return Ajax.successResponseJson(array);
//    }
//
//    private String getStringIds(List<Object[]> objects, long userId) {
//        String res = getId(objects.get(0), userId) + "";
//        for (int i = 1; i < objects.size(); i++)
//            res += " or userId = " + getId(objects.get(i), userId);
//        return res;
//    }
//
//    private long getId(Object[] objects, long userId) {
//        if (convertToLong(objects[0]) == userId)
//            return convertToLong(objects[1]);
//        else
//            return convertToLong(objects[0]);
//    }
//
//    private long convertToLong(Object object) {
//        return ((BigInteger) object).longValue();
//    }
//
//    private List<Object[]> getMyChild(Session session, long userId) {
//        return session.createSQLQuery("SELECT " +
//                "u.user_id as a0, " +
//                "s.user_id as a1 " +
//                "FROM izh_scheme.relationship r " +
//                "JOIN izh_scheme.user u ON u.user_id = recipient_id " +
//                "JOIN izh_scheme.user s ON s.user_id = sender_id " +
//                "WHERE r.sender_id = " + userId + " OR r.recipient_id = " + userId)
//                .list();
//    }

    @RequestMapping(value = ADDRESS + "/getAll/{user_id}/{page}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAllMy(@PathVariable(value = "user_id") Long userId,
                    @PathVariable(value = "page") int page,
                    @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (!user.isChild())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);
            return allMy(session, user, page * Constants.PAGE_RESULT);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/getAll/{user_id/{child_id}/{page}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAllNotMy(@PathVariable(value = "user_id") Long userId,
                       @PathVariable(value = "child_id") Long childId,
                       @PathVariable(value = "page") int page,
                       @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            UserDB child = UserDB.getUser(session, childId);
            if (!user.isParent())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);

            // TODO: 11.11.2016 проверить возможность
            return allNotMy(session, child, page * Constants.PAGE_RESULT);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/delete/{success_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> delete(@PathVariable(value = "success_id") Long successId,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            SuccessDB successDB = SuccessDB.getSuccess(session, successId);
            successDB.delete(session, token);
            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (ObjectNotFoundException e) {
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}