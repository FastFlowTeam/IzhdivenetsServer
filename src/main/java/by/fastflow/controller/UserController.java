package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.main.AuthDB;
import by.fastflow.DBModels.main.UserDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Authorisation;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.account.UserSettings;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 01.07.2016.
 */
@RestController
public class UserController extends ExceptionHandlerController {

    // TODO: 18.11.2016 загрузка фото

    private static final String ADDRESS = Constants.DEF_SERVER + "user";

    @RequestMapping(value = ADDRESS + "/loginCrypto", method = RequestMethod.POST)
    public
    @ResponseBody
    String loginCrypto(@RequestHeader(value = "token") String token) throws RestException {
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Authorisation auth = gson.fromJson(decodeString(new BigInteger(token, 16).toByteArray()), Authorisation.class);
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            auth.validate();
            List<AuthDB> list = session.createQuery("from AuthDB where type = " + auth.getAuth() + " and token = '" + (auth.getId() + "'")).list();

            if (list.size() == 0) {
                throw new RestException(ErrorConstants.NOT_HAVE_ID);
            }

            session.beginTransaction();
            UserDB userDB = UserDB.getUser(session, list.get(0).getUserId());
            userDB.updateToken();
            session.update(userDB);
            session.getTransaction().commit();
            session.close();

            return Ajax.successResponseJson(userDB.makeFullJson());
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private String decodeString(byte[] text) {
        byte[] result = new byte[text.length];
        byte[] keyarr = "dymgoVEso9Vfjneyc".getBytes();
        for (int i = 0; i < text.length; i++) {
            result[i] = (byte) (text[i] ^ keyarr[i % keyarr.length]);
        }
        return new String(result);
    }

    @RequestMapping(value = ADDRESS + "/registerCrypto", method = RequestMethod.POST)
    public
    @ResponseBody
    String registerCrypto(@RequestHeader(value = "token") String token) throws RestException {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Authorisation auth = gson.fromJson(decodeString(new BigInteger(token, 16).toByteArray()), Authorisation.class);

            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            auth.validate();
            List<AuthDB> list = session.createQuery("from AuthDB where type = " + auth.getAuth() + " and token = '" + (auth.getId() + "'")).list();
            UserDB userDB = null;
            session.beginTransaction();
            if (!Constants.contains(Constants.user_types, auth.getType()))
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);
            if (list.size() == 0) {
                userDB = UserDB
                        .createNew(auth.getName(), auth.getType())
                        .validate()
                        .setUserId(null);
                session.save(userDB);
                session.saveOrUpdate(AuthDB
                        .createNew(Constants.LOGIN_TYPE_VK, auth.getId() + "", userDB.getUserId()));
                session.saveOrUpdate(CardController.createCard(userDB));
            } else {
                userDB = UserDB.getUser(session, list.get(0).getUserId())
                        .updateToken();
                session.update(userDB);
            }
            session.getTransaction().commit();
            session.close();
            return Ajax.successResponseJson(userDB.makeFullJson());
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
                  @RequestBody UserDB user,
                  @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = user.updateInBDWithToken(session, UserDB.getUser(session, userId), token);
            session.close();
            return Ajax.successResponseJson(up.makeFullJson());
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/delete", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Map<String, Object> delete(@RequestHeader(value = "user_id") long userId,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB.getUser(session, userId, token).delete(session, token);
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
/*
    @RequestMapping(value = ADDRESS + "/getUserInfo/{user_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> getUser(@PathVariable(value = "user_id") long userId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            return Ajax.successResponse((UserDB.getUser(session, userId)).anonimize());
        } catch (RestException re) {
            throw re;
        } catch (ObjectNotFoundException e) {
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        } catch (Exception e) {
            throw new RestException(e);
        }
    }*/


    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}