package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.RelationshipDB;
import by.fastflow.DBModels.pk.RelationshipDBPK;
import by.fastflow.DBModels.UserDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by KuSu on 01.07.2016.
 */
@RestController
public class RequestController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "request";

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    String create(@RequestHeader(value = "token") String token, @PathVariable(value = "user_id") Long userId, @RequestParam(value = "gId") long gId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB userF = UserDB.getUser(session, userId, token);
            List<UserDB> list = session.createQuery("from UserDB where gId = " + gId).list();
            if (list.size() == 0) {
                throw new RestException(ErrorConstants.NOT_HAVE_GID);
            } else {
                if (userF.getType() == list.get(0).getType())
                    throw new RestException(ErrorConstants.SAME_TYPE);
                if (session.get(RelationshipDB.class, RelationshipDBPK.newKey(userF, list.get(0))) != null)
                    throw new RestException(ErrorConstants.HAVE_SAME_RELATIONSHIP);
                if (session.get(RelationshipDB.class, RelationshipDBPK.newKey(list.get(0), userF)) != null)
                    throw new RestException(ErrorConstants.HAVE_SAME_RELATIONSHIP);
                session.beginTransaction();
                session.save(RelationshipDB.createNew(userF, list.get(0)));
                session.getTransaction().commit();
            }
            return all(session, userF);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private String all(Session session, UserDB userF) {
        List<Object[]> list = getAllMyRelationshipForJSON(session, userF.getUserId());
        session.close();
        return Ajax.successResponseJson(generateJson(list));
    }

    @RequestMapping(value = ADDRESS + "/my/{user_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getMy(@RequestHeader(value = "token") String token, @PathVariable(value = "user_id") Long userId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB userF = UserDB.getUser(session, userId, token);
            return all(session, userF);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/update/{user_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    String update(@RequestHeader(value = "token") String token, @PathVariable(value = "user_id") Long userId, @RequestParam(value = "secondUser") long gId, @RequestParam(value = "state") int state) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB userF = UserDB.getUser(session, userId, token);
            List<UserDB> list = session.createQuery("from UserDB where gId = " + gId).list();
            if (list.size() == 0) {
                throw new RestException(ErrorConstants.NOT_HAVE_GID);
            } else {
                RelationshipDB relationshipDB = (RelationshipDB) session.get(RelationshipDB.class, RelationshipDBPK.newKey(list.get(0), userF));
                if (relationshipDB == null)
                    throw new RestException(ErrorConstants.NOT_HAVE_SAME_RELATIONSHIP);
                RelationshipDB.createNew(list.get(0), userF, state).updateInBDWithToken(session, relationshipDB, token);
            }
            return all(session, userF);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/delete/{user_id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    String delete(@RequestHeader(value = "token") String token, @PathVariable(value = "user_id") Long userId, @RequestParam(value = "secondUser") long gId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB userF = UserDB.getUser(session, userId, token);
            List<UserDB> list = session.createQuery("from UserDB where gId = " + gId).list();
            if (list.size() == 0) {
                throw new RestException(ErrorConstants.NOT_HAVE_GID);
            } else {
                RelationshipDB relationshipDB1 = (RelationshipDB) session.get(RelationshipDB.class, RelationshipDBPK.newKey(list.get(0), userF));
                RelationshipDB relationshipDB2 = (RelationshipDB) session.get(RelationshipDB.class, RelationshipDBPK.newKey(list.get(0), userF));
                if (relationshipDB1 == null) {
                    if (relationshipDB2 == null)
                        throw new RestException(ErrorConstants.NOT_HAVE_SAME_RELATIONSHIP);
                    else
                        relationshipDB2.delete(session, token);
                } else {
                    relationshipDB1.delete(session, token);
                }
            }
            return all(session, userF);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }


    private JsonArray generateJson(List<Object[]> list) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("state", (BigInteger) objects[8]);
            jsonObject.add("sender", UserDB.getJson((String) objects[0], (BigInteger) objects[1], (String) objects[2], (BigInteger) objects[3]));
            jsonObject.add("recipient", UserDB.getJson((String) objects[4], (BigInteger) objects[5], (String) objects[6], (BigInteger) objects[7]));
            array.add(jsonObject);
        }
        return array;
    }

    public static List<Object[]> getAllMyRelationshipForJSON(Session session, long userId) {
        return session.createSQLQuery("SELECT " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, " +
                "s.chat_name as a4, s.type as a5, s.photo as a6, s.g_id as a7, " +
                "r.state as a8 " +
                "FROM izh_scheme.relationship r " +
                "JOIN izh_scheme.user u ON u.user_id = recipient_id " +
                "JOIN izh_scheme.user s ON s.user_id = sender_id " +
                "WHERE r.sender_id = " + userId + " OR r.recipient_id = " + userId)
                .list();
    }

    public static List<Object[]> getAllMyAcceptedRelationship(Session session, long userId) {
        return session.createSQLQuery("SELECT " +
                "u.user_id as a0, " +
                "s.user_id as a1 " +
                "u.g_id as a2, " +
                "s.g_id as a3 " +
                "FROM izh_scheme.relationship r " +
                "JOIN izh_scheme.user u ON u.user_id = recipient_id " +
                "JOIN izh_scheme.user s ON s.user_id = sender_id " +
                "WHERE r.state = " + Constants.RELATIONSHIP_ACCEPT +
                " AND ( r.sender_id = " + userId + " OR r.recipient_id = " + userId + " )")
                .list();
    }

    public static void haveRelationship(Session session, UserDB user, UserDB child) throws RestException {
        RelationshipDB relationship = (RelationshipDB) session.get(RelationshipDB.class, RelationshipDBPK.newKey(user, child));
        if (relationship == null)
            relationship = (RelationshipDB) session.get(RelationshipDB.class, RelationshipDBPK.newKey(child, user));
        if (relationship == null)
            throw new RestException(ErrorConstants.HAVE_SAME_RELATIONSHIP);
        if (relationship.notAccepted())
            throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
    }

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}