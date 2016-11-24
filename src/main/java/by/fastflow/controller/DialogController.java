package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.main.DialogDB;
import by.fastflow.DBModels.InDialogDB;
import by.fastflow.DBModels.InDialogTwainDB;
import by.fastflow.DBModels.pk.InDialogDBPK;
import by.fastflow.DBModels.main.UserDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class DialogController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "dialog";

    @RequestMapping(value = ADDRESS + "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@RequestHeader(value = "user_id") long userId,
                               @RequestBody List<Long> userGId,
                               @RequestHeader(value = "token") String token,
                               @RequestParam(value = "name") String name) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            DialogDB dialog = DialogDB.createNew(name);

            session.beginTransaction();
            session.save(dialog
                    .setNextId(session));
            session.getTransaction().commit();

            HashSet<Long> users = new HashSet<>();
            List<Object[]> list = RequestController.getAllMyAcceptedRelationship(session, up.getUserId());
            for (Object[] objects : list) {
                if (userGId.contains(Constants.convertL(objects[2])))
                    users.add(Constants.convertL(objects[0]));
                if (userGId.contains(Constants.convertL(objects[3])))
                    users.add(Constants.convertL(objects[1]));
            }
            users.add(up.getUserId());

            session.beginTransaction();
            for (Long user : users) {
                session.save(InDialogDB
                        .createNew(user, dialog.getDialogId()));
            }
            session.getTransaction().commit();
            MessageController.generateMessage(session, Constants.MSG_CREATE, userId, dialog.getDialogId(), name);

            session.close();
            return Ajax.successResponse(dialog);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/out/{dialog_id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Map<String, Object> out(@RequestHeader(value = "user_id") long userId,
                            @PathVariable(value = "dialog_id") long dialogId,
                            @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);

            InDialogDB inDialogDB = (InDialogDB) session.get(DialogDB.class, new InDialogDBPK(userId, dialogId));
            if (inDialogDB == null) {
                throw new RestException(ErrorConstants.NOT_HAVE_ID);
            }

            if (isDialogTwain(session, dialogId))
                throw new RestException(ErrorConstants.TWAIN_DIALOG);

            session.beginTransaction();
            session.delete(inDialogDB);
            session.getTransaction().commit();

            MessageController.generateMessage(session, Constants.MSG_OUT_ME, userId, dialogId, up.getChatName());


            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/add/{dialog_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> addHim(@RequestHeader(value = "user_id") long userId,
                               @PathVariable(value = "dialog_id") long dialogId,
                               @RequestParam(value = "gId") long gId,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            DialogDB dialogDB = DialogDB.getDialog(session, dialogId);
            dialogDB.havePermissionToModify(session, token);

            List<UserDB> list = session.createQuery("from UserDB where gId = " + gId).list();
            if (list.size() == 0) {
                throw new RestException(ErrorConstants.NOT_HAVE_GID);
            }

            if (isDialogTwain(session, dialogId))
                throw new RestException(ErrorConstants.TWAIN_DIALOG);

            session.beginTransaction();
            session.save(InDialogDB
                    .createNew(list.get(0).getUserId(), dialogId));
            session.getTransaction().commit();

            MessageController.generateMessage(session, Constants.MSG_NEW_USER, userId, dialogId, list.get(0).getChatName());

            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    public static boolean isDialogTwain(Session session, long dialogId) {
        if (getTwainDialog(session, dialogId).size() > 0)
            return true;
        else
            return false;
    }


    @RequestMapping(value = ADDRESS + "/update", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> update(@RequestHeader(value = "user_id") long userId,
                               @RequestBody DialogDB dialogDB,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            UserDB user = UserDB.getUser(session, userId, token);
            DialogDB up = dialogDB.updateInBDWithToken(session, DialogDB.getDialog(session, dialogDB.getDialogId()), token);
            MessageController.generateMessage(session, Constants.MSG_UPDATE, userId, dialogDB.getDialogId(), dialogDB.getName());

            session.close();
            return Ajax.successResponse(up);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/getAll/{dialog_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAll(@RequestHeader(value = "user_id") long userId,
                  @PathVariable(value = "dialog_id") long dialogId,
                  @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            if (session.get(InDialogDB.class, new InDialogDBPK(userId, dialogId)) == null)
                throw new RestException(ErrorConstants.NOT_HAVE_ID);
            List<Object[]> list = getAllUserInDialog(session, dialogId);
            session.close();
            return Ajax.successResponseJson(generateJson(list));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/getAll", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAllDialogs(@RequestHeader(value = "user_id") long userId,
                         @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);

            //последняя колонка возвращает или null, или 1 и отвечает за то, личный ли это диалог (если 1 - личный)
            List<Object[]> list = getAllDialogs(session, userId);

            session.close();
            return Ajax.successResponseJson(getArrayDialogsJson(list, user));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private JsonArray getArrayDialogsJson(List<Object[]> list, UserDB user) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", (String) objects[0]);
            obj.addProperty("dialogId", (BigInteger) objects[1]);
            obj.addProperty("not_readed", (BigInteger) objects[2]);
            obj.add("photos", generateJsonPhotos((String) objects[4], user, (objects[5] != null), Constants.convertL(objects[3])));
            obj.addProperty("count", (BigInteger) objects[3]);
            obj.addProperty("is_private", (objects[5] != null));
            obj.addProperty("text", (String) objects[6]);
            obj.addProperty("my", Constants.convertL(objects[7]) == user.getUserId());
            array.add(obj);
        }
        return array;
    }

    private JsonArray generateJsonPhotos(String s, UserDB user, boolean isPrivate, long count) {
        String[] strings = s.split(";");
        JsonArray array = new JsonArray();
        if (isPrivate) {
            if (strings.length > 0) {
                array.add(strings[0].equals(user.getPhoto()) ? strings[1] : strings[0]);
            } else {
                array.add("");
            }
        } else {
            if (strings.length < 4) {
                for (String str : strings)
                    array.add(str);
                for (int i = 0; i < 4 - strings.length; i++)
                    array.add("");
            } else {
                Random random = new Random();
                int j = 0;
                while (j != 4) {
                    int t = random.nextInt(strings.length);
                    if (strings[t].equals("===")) {
                    } else {
                        j++;
                        array.add(strings[t]);
                        strings[t] = new String("===");
                    }
                }
            }
        }
        return array;
    }

    public static List<Object[]> getAllDialogs(Session session, long userId) {
        return session.createSQLQuery("select d.name as a0, " +
                "i_d.dialog_id as a1, i_d.not_readed_messages as a2, " +
                "d2.count as a3, photos as a4, is_private as a5, " +
                "m.text as a6, m.user_id as a7 " +
                "from izh_scheme.dialog as d " +
                "join izh_scheme.in_dialog as i_d on d.dialog_id = i_d.dialog_id and user_id = " + userId + " " +
                "join izh_scheme.message as m on m.dialog_id = d.dialog_id and m.user_id = " + userId + " and m.message_id = " +
                "(select max(message_id) from izh_scheme.message m where m.user_id = " + userId + " and m.dialog_id = d.dialog_id) " +
                "join (select dialog_id, count(user_id) as count from izh_scheme.in_dialog group by dialog_id) d2 on d2.dialog_id = i_d.dialog_id " +
                "left join (select count(first_user) is_private, dialog_id from in_dialog_twain group by dialog_id) d4 on d4.dialog_id = i_d.dialog_id " +
                "join (select string_agg(photo,';') photos, dialog_id from izh_scheme.user u " +
                "join izh_scheme.in_dialog i_d on i_d.user_id = u.user_id group by dialog_id) d3 on d3.dialog_id = i_d.dialog_id ").list();
    }

    private List<Object[]> getAllUserInDialog(Session session, long dialogId) {
        return session.createSQLQuery("SELECT " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3 " +
                "FROM izh_scheme.in_dialog r " +
                "JOIN izh_scheme.user u ON u.user_id = r.user_id " +
                "WHERE r.dialog_id = " + dialogId)
                .list();
    }

    private JsonArray generateJson(List<Object[]> list) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            array.add(UserDB.getJson((String) objects[0], (BigInteger) objects[1], (String) objects[2], (BigInteger) objects[3]));
        }
        return array;
    }

    public static long getTwainDialogId(Session session, long userFId, long userSId) {
        List<Object[]> list = getTwainDialog(session, userFId, userSId);
        if (list.size() == 0) {
            session.beginTransaction();
            DialogDB dialog = DialogDB.createNew("").setNextId(session);
            session.save(dialog);
            session.save(InDialogTwainDB.createNew(userFId, userSId, dialog.getDialogId()));
            session.save(InDialogTwainDB.createNew(userSId, userFId, dialog.getDialogId()));
            session.getTransaction().commit();
            return dialog.getDialogId();
        } else
            return Constants.convertL(list.get(0)[0]);
    }

    private static List<Object[]> getTwainDialog(Session session, long userFId, long userSId) {
        return session.createSQLQuery("SELECT " +
                "r.dialog_id as a0, r.first_user as a1, r.second_user as a2 " +
                "FROM izh_scheme.in_dialog_twain r " +
                "WHERE r.first_user = " + userFId + " AND r.second_user = " + userSId)
                .list();
    }

    public static List<Object[]> getTwainDialog(Session session, long dialogId) {
        return session.createSQLQuery("SELECT " +
                "r.dialog_id as a0, r.first_user as a1, r.second_user as a2 " +
                "FROM izh_scheme.in_dialog_twain r " +
                "WHERE r.dialog_id = " + dialogId)
                .list();
    }

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}
