package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.*;
import by.fastflow.DBModels.pk.NotReadedSuccessDBPK;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

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
                not = new NotReadedSuccessDB(Constants.convertL(Constants.convertL(objects[0]) == child.getUserId() ? objects[1] : objects[0]), child.getUserId(), 1);
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
            if (item.isNotRead()) {
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
            not = new NotReadedSuccessDB(user, child, 0);
        }
        not.readAll();
        session.beginTransaction();
        session.saveOrUpdate(not);
        session.getTransaction().commit();
    }

    @RequestMapping(value = ADDRESS + "/praised/{user_id}/{success_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> praised(@PathVariable(value = "success_id") Long successId,
                                @PathVariable(value = "user_id") Long userId,
                                @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            SuccessDB successDB = SuccessDB.getSuccess(session, successId);

            UserDB user = UserDB.getUser(session, userId, token);
            UserDB child = UserDB.getUser(session, successDB.getUserId());

            RequestController.haveRelationship(session, user, child);

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

            RequestController.haveRelationship(session, user, child);

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
    String getAllChilds(@PathVariable(value = "user_id") Long userId,
                        @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (!user.isParent())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);

            List<Object[]> list = RequestController.getAllMyAcceptedRelationship(session, userId);
            for (Object[] objects : list) {
                // TODO: 13.11.2016
            }
            return "{}";
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