package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.*;
import by.fastflow.DBModels.pk.NotReadedSuccessDBPK;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 01.07.2016.
 */
@RestController
public class SuccessController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "success";

    @RequestMapping(value = ADDRESS + "/update/{success_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> update(@PathVariable(value = "success_id") long successId,
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

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@PathVariable(value = "user_id") long userId,
                               @RequestBody SuccessDB success,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (!user.isChild())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);

            session.beginTransaction();
            session.save(success
                    .validate()
                    .setUserId(userId)
                    .setState(Constants.SUCCESS_NOT_READED)
                    .setNextId(session));
            session.getTransaction().commit();

            updateNotReaded(session, user);

            session.close();
            return Ajax.successResponse(success);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private void updateNotReaded(Session session, UserDB child) {
        List<Object[]> list = RequestController.getAllMyAcceptedRelationship(session, child.getUserId());
        for (Object[] objects : list) {
            NotReadedSuccessDB not = (NotReadedSuccessDB) session.get(NotReadedSuccessDB.class, new NotReadedSuccessDBPK(
                    Constants.convertL(Constants.convertL(objects[0]) == child.getUserId() ? objects[1] : objects[0]), child.getUserId()));
            if (not == null) {
                not = NotReadedSuccessDB
                        .createNew(Constants.convertL(Constants.convertL(objects[0]) == child.getUserId() ? objects[1] : objects[0]), child.getUserId(), 1);
            } else {
                not.moreNotRead();
            }
            session.beginTransaction();
            session.saveOrUpdate(not);
            session.getTransaction().commit();
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

    private String allNotMy(Session session, UserDB user, UserDB child, int START_NUM) {
        List<SuccessDB> list = session.createQuery("from SuccessDB order by successId DESC where userId = " + child.getUserId()).setFirstResult(START_NUM).setMaxResults(Constants.PAGE_RESULT).list();
        JsonArray array = new JsonArray();
        for (SuccessDB item : list) {
            array.add(SuccessDB.getJson(item));
            if (item.notRead()) {
                item.read();
                session.beginTransaction();
                session.update(item);
                session.getTransaction().commit();
            }
        }
        ReadAll(session, user, child);
        session.close();
        return Ajax.successResponseJson(array);
    }

    private void ReadAll(Session session, UserDB user, UserDB child) {
        NotReadedSuccessDB not = (NotReadedSuccessDB) session.get(NotReadedSuccessDB.class, new NotReadedSuccessDBPK(user, child));
        if (not == null) {
            not = NotReadedSuccessDB
                    .createNew(user.getUserId(), child.getUserId(), 0);
        }
        not.readAll();
        session.beginTransaction();
        session.saveOrUpdate(not);
        session.getTransaction().commit();
    }

    @RequestMapping(value = ADDRESS + "/praised/{user_id}/{success_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> praised(@PathVariable(value = "success_id") long successId,
                                @PathVariable(value = "user_id") long userId,
                                @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            SuccessDB successDB = SuccessDB.getSuccess(session, successId);

            UserDB user = UserDB.getUser(session, userId, token);
            UserDB child = UserDB.getUser(session, successDB.getUserId());

            RequestController.haveAcceptedRelationship(session, user, child);

            session.beginTransaction();
            successDB.praised();
            session.update(successDB);
            session.getTransaction().commit();

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

    @RequestMapping(value = ADDRESS + "/getAll/{user_id}/{page}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAllMy(@PathVariable(value = "user_id") long userId,
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
    String getAllNotMy(@PathVariable(value = "user_id") long userId,
                       @PathVariable(value = "child_id") long childId,
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

            RequestController.haveAcceptedRelationship(session, user, child);

            return allNotMy(session, user, child, page * Constants.PAGE_RESULT);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/getAllChilds/{user_id}/", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAllChilds(@PathVariable(value = "user_id") long userId,
                        @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (!user.isParent())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);

            List<Object[]> list = getAllChildSuccess(session, user.getUserId());

            return Ajax.successResponseJson(getJsonArray(list));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private JsonArray getJsonArray(List<Object[]> list) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            JsonObject object = new JsonObject();
            object.addProperty("number", (BigInteger) objects[0]);
            object.add("user", UserDB.getJson((String) objects[1], (BigInteger) objects[2], (String) objects[3], (BigInteger) objects[4]));
            object.add("success", SuccessDB.getJson((BigInteger) objects[5], (String) objects[6], (String) objects[7], (String) objects[8], (String) objects[9], (BigInteger) objects[20]));
            array.add(object);
        }
        return array;
    }

    @RequestMapping(value = ADDRESS + "/delete/{success_id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Map<String, Object> delete(@PathVariable(value = "success_id") long successId,
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

    public static List<Object[]> getAllChildSuccess(Session session, long parent_id) {
        return session.createSQLQuery("select not_readed_success.number as a0, " +
                "u.chat_name as a1, u.type as a2, u.photo as a3, u.g_id as a4, " +
                "s.success_id as a5, s.title as a6, s.description as a7, s.photo as a8, s.link as a9, s.state as a10 " +
                "from izh_scheme.relationship r " +
                "join izh_scheme.not_readed_success on parent_id = " + parent_id + " and child_id = sender_id " +
                "join izh_scheme.user as u on u.user_id = sender_id " +
                "join izh_scheme.success as s on s.user_id = sender_id and s.success_id = " +
                "(select max(success_id) from izh_scheme.success s where s.user_id = sender_id) " +
                "where r.recipient_id = " + parent_id + " AND r.state = " + Constants.RELATIONSHIP_ACCEPT +
                "union " +
                "select not_readed_success.number as a0, " +
                "u.chat_name as a1, u.type as a2, u.photo as a3, u.g_id as a4, " +
                "s.success_id as a5, s.title as a6, s.description as a7, s.photo as a8, s.link as a9, s.state as a10 " +
                "from izh_scheme.relationship r " +
                "join izh_scheme.not_readed_success on parent_id = 1 and child_id = recipient_id " +
                "join izh_scheme.user as u on u.user_id = recipient_id " +
                "join izh_scheme.success as s on s.user_id = recipient_id and s.success_id = " +
                "(select max(success_id) from izh_scheme.success s where s.user_id = recipient_id) " +
                "where r.sender_id = " + parent_id + " AND r.state = " + Constants.RELATIONSHIP_ACCEPT).list();
    }

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}