package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.main.UserDB;
import by.fastflow.DBModels.main.WishListDB;
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
import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class WishListController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "wishlist";

    @RequestMapping(value = ADDRESS + "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    String create(@RequestHeader(value = "user_id") long userId,
                               @RequestHeader(value = "token") String token,
                               @RequestBody WishListDB wishlist) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            UserDB up = UserDB.getUser(session, userId, token);
            if (up.isParent())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);

            session.beginTransaction();
            session.save(wishlist
                    .validate()
                    .setUserId(userId)
                    .setListId(null));
            session.getTransaction().commit();
            session.close();
            return Ajax.successResponseJson(wishlist.makeJson());
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/update", method = RequestMethod.PUT)
    public
    @ResponseBody
    String update(@RequestHeader(value = "user_id") long userId,
                               @RequestHeader(value = "token") String token,
                               @RequestBody WishListDB wishListDB) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            WishListDB up = wishListDB.updateInBDWithToken(session, WishListDB.getWishList(session, wishListDB.getListId()), token);
            session.close();
            return Ajax.successResponseJson(up.makeJson());
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/delete/{wishlist_id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Map<String, Object> delete(@RequestHeader(value = "token") String token,
                               @RequestHeader(value = "user_id") long userId,
                               @PathVariable(value = "wishlist_id") long wishlistId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            WishListDB.getWishList(session, wishlistId).delete(session, token);
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/get", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAll(@RequestHeader(value = "user_id") long userId,
                    @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            JsonArray array;
            if (up.isParent()){
                List<Object[]> list = getChildsWishLists(session, userId);
                session.close();
                array = getArrayBigJson(list);
            }else {
                List<Object[]> list = getMyWishLists(session, userId);
                session.close();
                array = getArrayJson(list);
            }
            return Ajax.successResponseJson(array);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private JsonArray getArrayBigJson(List<Object[]> list) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            JsonObject object = new JsonObject();
            object.add("list", WishListDB.makeJson((String) objects[0], (BigInteger) objects[1], (String) objects[2]));
            object.add("user", UserDB.getJson((String) objects[3], (BigInteger) objects[4], (String) objects[5], (BigInteger) objects[6]));
            object.addProperty("count", objects[7] == null ? 0 : Constants.convertL(objects[7]));
            array.add(object);
        }
        return array;
    }

    public static List<Object[]> getMyWishLists(Session session, long childId) {
        return session.createSQLQuery("select " +
                "w_l.name as a0, w_l.list_id as a1, w_l.description as a2, w_l.visibility as a3, " +
                "cou as a4 " +
                "from izh_scheme.wish_list w_l "+
                "left join (select count(item_id) as cou, list_id from izh_scheme.wish_item where visibility = " + Constants.WISH_ITEM_VISIBLE + " group by list_id) t1 " +
                "on t1.list_id = w_l.list_id "+
                "WHERE w_l.user_id = " + childId
        ).list();
    }

    public static List<Object[]> getChildsWishLists(Session session, long parentId) {
        return session.createSQLQuery("select " +
                "w_l.name as a0, w_l.list_id as a1, w_l.description as a2, " +
                "u.chat_name as a3, u.type as a4, u.photo as a5, u.g_id as a6, " +
                "cou as a7 " +
                "from izh_scheme.relationship r " +
                "join izh_scheme.wish_list w_l on user_id = sender_id and visibility = " + Constants.WISH_LIST_VISIBLE + " " +
                "left join (select count(item_id) as cou, list_id from izh_scheme.wish_item where visibility = " + Constants.WISH_ITEM_VISIBLE + " group by list_id) t1 " +
                "on t1.list_id = w_l.list_id " +
                "join izh_scheme.user u on u.user_id = w_l.user_id " +
                "where r.recipient_id = " + parentId + " and r.state = " + Constants.RELATIONSHIP_ACCEPT + " " +
                "union " +
                "select " +
                "w_l.name as a0, w_l.list_id as a1, w_l.description as a2, " +
                "u.chat_name as a3, u.type as a4, u.photo as a5, u.g_id as a6, " +
                "cou as a7 " +
                "from izh_scheme.relationship r " +
                "join izh_scheme.wish_list w_l on user_id = recipient_id and visibility = " + Constants.WISH_LIST_VISIBLE + " "+
                "left join (select count(item_id) as cou, list_id from izh_scheme.wish_item where visibility = " + Constants.WISH_ITEM_VISIBLE + " group by list_id ) t1 " +
                "on t1.list_id = w_l.list_id " +
                "join izh_scheme.user u on u.user_id = w_l.user_id " +
                "where r.sender_id = " + parentId + " and r.state = " + Constants.RELATIONSHIP_ACCEPT).list();
    }

    private JsonArray getArrayJson(List<Object[]> list) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            JsonObject object = new JsonObject();
            object.add("list", WishListDB.makeJson((String) objects[0], (BigInteger) objects[1], (String) objects[2], (BigInteger) objects[3]));
            object.add("user", null);
            object.addProperty("count", objects[4] == null ? 0 : Constants.convertL(objects[4]));
            array.add(object);
        }
        return array;
    }

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}
