package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.DialogDB;
import by.fastflow.DBModels.InDialogDB;
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
public class DialogController extends ExceptionHandlerController {
    private static final String ADDRESS = Constants.DEF_SERVER + "dialog";

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> create(@PathVariable(value = "user_id") Long userId,
                               @RequestBody List<Long> userGId,
                               @RequestHeader(value = "token") String token,
                               @RequestParam(value = "name") String name) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            DialogDB dialog = DialogDB.createNew(name);
            dialog.setNextId(session);

            session.beginTransaction();
            session.save(dialog);
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

            InDialogDB inDialogDB;
            session.beginTransaction();
            for (Long user : users) {
                inDialogDB = new InDialogDB(user, dialog.getDialogId());
                session.save(inDialogDB);
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

    @RequestMapping(value = ADDRESS + "/out/{user_id}/{dialog_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> out(@PathVariable(value = "user_id") Long userId,
                            @PathVariable(value = "dialog_id") Long dialogId,
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
            session.beginTransaction();
            session.delete(inDialogDB);
            session.getTransaction().commit();

            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/add/{user_id}/{dialog_id}", method = RequestMethod.PUT)
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

            InDialogDB inDialogDB = new InDialogDB(list.get(0).getUserId(), dialogId);
            session.beginTransaction();
            session.save(inDialogDB);
            session.getTransaction().commit();

            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }


    @RequestMapping(value = ADDRESS + "/update/{user_id}/{dialog_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> update(@PathVariable(value = "user_id") long userId,
                               @PathVariable(value = "dialog_id") long dialogId,
                               @RequestHeader(value = "token") String token,
                               @RequestParam(value = "name") String name) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            DialogDB up = DialogDB.createNew(name).updateInBDWithToken(session, DialogDB.getDialog(session, dialogId), token);

            MessageController.generateMessage(session, Constants.MSG_UPDATE, userId, dialogId, name);
            session.close();
            return Ajax.successResponse(up);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/getAll/{user_id}/{dialog_id}", method = RequestMethod.PUT)
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
}
