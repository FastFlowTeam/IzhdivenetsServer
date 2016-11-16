package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.*;
import by.fastflow.DBModels.pk.InDialogDBPK;
import by.fastflow.DBModels.pk.NotReadedMessagesDBPK;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.message.Message;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class MessageController extends ExceptionHandlerController {
    private static final String ADDRESS = Constants.DEF_SERVER + "message";

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@PathVariable(value = "user_id") Long userId,
                               @RequestBody MessageDB message,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            if (session.get(InDialogDB.class, new InDialogDBPK(up.getUserId(), message.getDialogId())) == null)
                throw new RestException(ErrorConstants.NOT_HAVE_ID);

            message.validate();
            session.beginTransaction();
            session.save(message
                    .setUserId(up.getUserId())
                    .setNextId(session));
            session.getTransaction().commit();

            updateNotReadedMessages(session, message.getDialogId(), userId);

            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    public static void generateMessage(Session session, int msg_type, long userId, long dialogId, String name) {
        session.beginTransaction();
        session.save(MessageDB
                .createNew(msg_type, userId, dialogId, name)
                .setNextId(session));
        session.getTransaction().commit();

        updateNotReadedMessages(session, dialogId, userId);
    }

    @RequestMapping(value = ADDRESS + "/getFirst/{user_id}/{dialog_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getFirst(@PathVariable(value = "user_id") long userId,
                    @PathVariable(value = "dialog_id") long dialogId,
                    @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (session.get(InDialogDB.class, new InDialogDBPK(userId, dialogId)) == null)
                throw new RestException(ErrorConstants.NOT_HAVE_ID);

            List<Object[]> list;
            NotReadedMessagesDB notReaded = (NotReadedMessagesDB) session.get(NotReadedMessagesDB.class, new NotReadedMessagesDBPK(userId, dialogId));
            if (notReaded != null) {
                list = getFirstMessages(session, dialogId, Math.max(notReaded.getNumber(), Constants.PAGE_RESULT_MESSAGE));
                session.beginTransaction();
                session.save(notReaded.readAll());
                session.getTransaction().commit();
            } else {
                list = getFirstMessages(session, dialogId, Constants.PAGE_RESULT_MESSAGE);
            }
            session.close();
            return Ajax.successResponseJson(getJson(list));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private JsonArray getJson(List<Object[]> list) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("message", MessageDB.getJson((BigInteger) objects[4], (BigInteger) objects[5], (BigInteger) objects[6], (String) objects[7], (String) objects[8]));
            jsonObject.add("user", UserDB.getJson((String) objects[0], (BigInteger) objects[1], (String) objects[2], (BigInteger) objects[3]));
            array.add(jsonObject);
        }
        return array;
    }

    public static List<Object[]> getFirstMessages(Session session, long dialogId, int num) {
        return session.createSQLQuery("SELECT " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, " +
                "r.date as a4, r.type as a5, r.message_id as a6, r.text as a7, r.link as a8 " +
                "FROM izh_scheme.message r " +
                "JOIN izh_scheme.user u ON u.user_id = r.user_id " +
                "WHERE r.dialog_id = " + dialogId + " ORDER BY r.message_id DESC").setMaxResults(num)
                .list();
    }

    @RequestMapping(value = ADDRESS + "/getNext/{user_id}/{dialog_id}/{message_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getNext(@PathVariable(value = "user_id") long userId,
                   @PathVariable(value = "dialog_id") long dialogId,
                   @PathVariable(value = "message_id") long messageId,
                   @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (session.get(InDialogDB.class, new InDialogDBPK(userId, dialogId)) == null)
                throw new RestException(ErrorConstants.NOT_HAVE_ID);

            List<Object[]> list;
            NotReadedMessagesDB notReaded = (NotReadedMessagesDB) session.get(NotReadedMessagesDB.class, new NotReadedMessagesDBPK(userId, dialogId));
            if (notReaded != null) {
                list = getNextMessages(session, dialogId, Math.max(notReaded.getNumber(), Constants.PAGE_RESULT_MESSAGE), messageId);
                session.beginTransaction();
                session.save(notReaded.readAll());
                session.getTransaction().commit();
            } else {
                list = getNextMessages(session, dialogId, Constants.PAGE_RESULT_MESSAGE, messageId);
            }
            session.close();
            return Ajax.successResponseJson(getJson(list));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/getPrev/{user_id}/{dialog_id}/{message_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getPrev(@PathVariable(value = "user_id") long userId,
                   @PathVariable(value = "dialog_id") long dialogId,
                   @PathVariable(value = "message_id") long messageId,
                   @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (session.get(InDialogDB.class, new InDialogDBPK(userId, dialogId)) == null)
                throw new RestException(ErrorConstants.NOT_HAVE_ID);
            List<Object[]> list = getPrevMessages(session, dialogId, messageId);
            session.close();
            return Ajax.successResponseJson(getJson(list));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private List<Object[]> getPrevMessages(Session session, long dialogId, long messageId) {
        return session.createSQLQuery("SELECT " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, " +
                "r.date as a4, r.type as a5, r.message_id as a6, r.text as a7, r.link as a8 " +
                "FROM izh_scheme.message r " +
                "JOIN izh_scheme.user u ON u.user_id = r.user_id " +
                "WHERE r.dialog_id = " + dialogId + " AND r.message_id < " + messageId + " ORDER BY r.message_id DESC").setMaxResults(Constants.PAGE_RESULT_MESSAGE)
                .list();
    }

    private List<Object[]> getNextMessages(Session session, long dialogId, int num, long messageId) {
        return session.createSQLQuery("SELECT " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, " +
                "r.date as a4, r.type as a5, r.message_id as a6, r.text as a7, r.link as a8 " +
                "FROM izh_scheme.message r " +
                "JOIN izh_scheme.user u ON u.user_id = r.user_id " +
                "WHERE r.dialog_id = " + dialogId + " AND r.message_id > " + messageId + " ORDER BY r.message_id ASC").setMaxResults(num)
                .list();
    }

    private static List<Object[]> getInDialog(Session session, long dialogId) {
        return session.createSQLQuery("SELECT " +
                "r.user_id as a0, r.dialog_id as a1 " +
                "FROM izh_scheme.in_dialog r " +
                "WHERE r.dialog_id = " + dialogId).list();
    }

    private static void updateNotReadedMessages(Session session, long dialogId, long userId) {
        session.beginTransaction();
        List<Object[]> list = getInDialog(session, dialogId);
        for (Object[] objects : list) {
            long id = Constants.convertL(objects[0]);
            if (id != userId) {
                NotReadedMessagesDB notReaded = (NotReadedMessagesDB) session.get(NotReadedMessagesDB.class, new NotReadedMessagesDBPK(userId, dialogId));
                if (notReaded != null)
                    session.saveOrUpdate(notReaded.next());
                else
                    session.saveOrUpdate(NotReadedMessagesDB.createNew(dialogId, userId, 1));
            }
        }
        session.getTransaction().commit();
    }

    public static void generateMessage(Session session, int msg_type, String text, Long userId, long userId1) {
        /*session.get(In)

        session.beginTransaction();
        session.save(MessageDB
                .createNew(msg_type, userId, dialogId, name)
                .setNextId(session));
        session.getTransaction().commit();

        updateNotReadedMessages(session, dialogId, userId);*/
    }
}
