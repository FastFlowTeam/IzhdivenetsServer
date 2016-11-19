package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.*;
import by.fastflow.DBModels.pk.RelationshipDBPK;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class WishListController extends ExceptionHandlerController {

    // TODO: 18.11.2016 получить мои списки - родитель.

    private static final String ADDRESS = Constants.DEF_SERVER + "wishlist";

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@PathVariable(value = "user_id") long userId,
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
                    .setNextId(session));

            session.close();
            return Ajax.successResponse(wishlist);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/update/{user_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> update(@PathVariable(value = "user_id") long userId,
                               @RequestHeader(value = "token") String token,
                               @RequestBody WishListDB wishListDB) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB user = UserDB.getUser(session, userId, token);
            WishListDB up = wishListDB.updateInBDWithToken(session, WishListDB.getWishList(session, wishListDB.getListId()), token);
            session.close();
            return Ajax.successResponse(up);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/delete/{user_id}/{wishlist_id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Map<String, Object> delete(@RequestHeader(value = "token") String token,
                               @PathVariable(value = "user_id") long userId,
                               @PathVariable(value = "wishlist_id") long wishlistId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB userF = UserDB.getUser(session, userId, token);
            WishListDB.getWishList(session,wishlistId).delete(session,token);
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/getMy/{user_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getAllMy(@PathVariable(value = "user_id") long userId,
                    @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            if (up.isParent())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);
            List<WishListDB> list = session.createQuery("from WishListDB where userId = "+userId).list();
            session.close();
            return Ajax.successResponseJson(getArrayJson(list));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/getNotMy/{user_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    String getAllNotMy(@PathVariable(value = "user_id") long userId,
                    @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            if (up.isChild())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);
            // TODO: 18.11.2016 получить мои списки - родитель. Можно сначала получить все accepted связи, и потом для каждого списки.
            session.close();
            return Ajax.successResponseJson(getArrayJson(new ArrayList<>()));
        } catch (RestException re) {
            throw re;
        } catch (IndexOutOfBoundsException re) {
            throw new RestException(ErrorConstants.NOT_HAVE_CARD);
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private JsonArray getArrayJson(List<WishListDB> list) {
        JsonArray array = new JsonArray();
        for (WishListDB item : list){
            array.add(item.makeJson());
        }
        return array;
    }

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}
