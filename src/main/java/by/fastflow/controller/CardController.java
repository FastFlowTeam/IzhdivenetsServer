package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.*;
import by.fastflow.DBModels.pk.RelationshipDBPK;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class CardController extends ExceptionHandlerController {
    private static final String ADDRESS = Constants.DEF_SERVER + "card-";

    @RequestMapping(value = ADDRESS + "/get/{user_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> get(@PathVariable(value = "user_id") Long userId,
                            @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            CardDB cardDB = (CardDB) session.createQuery("from CardDB where userId = " + userId).list().get(0);
            session.close();
            return Ajax.successResponse(cardDB);
        } catch (RestException re) {
            throw re;
        } catch (IndexOutOfBoundsException re) {
            throw new RestException(ErrorConstants.NOT_HAVE_CARD);
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/send/{user_id}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> send(@PathVariable(value = "user_id") Long userId,
                             @RequestParam(value = "gId") long gId,
                             @RequestParam(value = "money") long money,
                             @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            List<UserDB> list = session.createQuery("from UserDB where gId = " + gId).list();
            if (list.size() == 0) {
                throw new RestException(ErrorConstants.NOT_HAVE_GID);
            } else {
                RelationshipDB relationshipDB = (RelationshipDB) session.get(RelationshipDB.class, RelationshipDBPK.newKey(up, list.get(0)));
                if (relationshipDB == null)
                    relationshipDB = (RelationshipDB) session.get(RelationshipDB.class, RelationshipDBPK.newKey(up, list.get(0)));
                if (relationshipDB == null)
                    throw new RestException(ErrorConstants.NOT_HAVE_SAME_RELATIONSHIP);
                if (relationshipDB.notAccepted())
                    throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
                CardDB card1 = (CardDB) session.createQuery("from CardDB where userId = " + up.getUserId()).list().get(0);
                CardDB card2 = (CardDB) session.createQuery("from CardDB where userId = " + list.get(0).getUserId()).list().get(0);
                if ((card1.getMoneyAmount() < money) || (money <= 0))
                    throw new RestException(ErrorConstants.NEGATIVE_CARD_MONEY);
                session.beginTransaction();
                session.update(card1.sub(money));
                session.update(card2.add(money));
                session.getTransaction().commit();
                MessageController.generateMessage(session, Constants.MSG_SEND_MONEY, Constants.getStringMoney(money), userId, list.get(0).getUserId());
            }
            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (IndexOutOfBoundsException re) {
            throw new RestException(ErrorConstants.NOT_HAVE_CARD);
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    public static void createCard(Session session, long userId, long type) {
        session.beginTransaction();
        session.save(CardDB
                .createNew(userId, type == Constants.USER_PARENT ? 10000 : 0)
                .setNextId(session));
        session.getTransaction().commit();
    }
}
