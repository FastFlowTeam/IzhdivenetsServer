package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.*;
import by.fastflow.DBModels.pk.InDialogDBPK;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import org.apache.logging.log4j.message.Message;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
public class MessageController extends ExceptionHandlerController {
    private static final String ADDRESS = Constants.DEF_SERVER + "message";

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.PUT)
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
            message.setUserId(up.getUserId());
            message.setNextId(session);

            session.beginTransaction();
            session.save(message);
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
        MessageDB messageDB = MessageDB.createNew(msg_type, userId, dialogId, name);
        messageDB.setNextId(session);
        session.beginTransaction();
        session.save(messageDB);
        session.getTransaction().commit();

        updateNotReadedMessages(session, dialogId, userId);
    }

    private static void updateNotReadedMessages(Session session, long dialogId, long userId) {
        session.beginTransaction();
        // TODO: 14.11.2016  обновить не прочитанные сообщения у всех кроме меня
        session.getTransaction().commit();
    }
}
