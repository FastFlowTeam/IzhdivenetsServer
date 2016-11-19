package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.DialogDB;
import by.fastflow.DBModels.InDialogDB;
import by.fastflow.DBModels.InDialogTwainDB;
import by.fastflow.DBModels.pk.InDialogDBPK;
import by.fastflow.DBModels.UserDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class DialogController extends ExceptionHandlerController {

    // TODO: 18.11.2016 получить мои диалоги

    private static final String ADDRESS = Constants.DEF_SERVER + "dialog";

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@PathVariable(value = "user_id") long userId,
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

    @RequestMapping(value = ADDRESS + "/out/{user_id}/{dialog_id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Map<String, Object> out(@PathVariable(value = "user_id") long userId,
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

    @RequestMapping(value = ADDRESS + "/add/{user_id}/{dialog_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> addHim(@PathVariable(value = "user_id") long userId,
                               @PathVariable(value = "dialog_id") long dialogId,
                               @RequestParam(value = "gId") long gId,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
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


    @RequestMapping(value = ADDRESS + "/update/{user_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> update(@PathVariable(value = "user_id") long userId,
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

    @RequestMapping(value = ADDRESS + "/getAll/{user_id}/{dialog_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAll(@PathVariable(value = "user_id") long userId,
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
