package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.AuthDB;
import by.fastflow.DBModels.RelationshipDB;
import by.fastflow.DBModels.RelationshipDBPK;
import by.fastflow.DBModels.UserDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.account.UserSettings;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 01.07.2016.
 */
@RestController
public class RequestController extends ExceptionHandlerController<UserDB> {

    private static final String ADDRESS = Constants.DEF_SERVER + "request";

    @RequestMapping(value = ADDRESS + "/create/{user_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@RequestHeader(value = "token") String token, @PathVariable(value = "user_id") Long userId, @RequestParam(value = "secondUser") long gId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB userF = UserDB.getUser(session, userId, token);
            UserDB userS = null;
            List<UserDB> list = session.createQuery("from UserDB where gId = " + gId).list();
            if (list.size() == 0) {
                throw new RestException(ErrorConstants.NOT_HAVE_GID);
            } else {
                if (userF.getType() == list.get(0).getType())
                    throw new RestException(ErrorConstants.SAME_TYPE);
                if (session.load(RelationshipDB.class, RelationshipDBPK.newKey(userF, list.get(0)))!= null)
                    throw new RestException(ErrorConstants.HAVE_SAME_RELATIONSHIP);
                if (session.load(RelationshipDB.class, RelationshipDBPK.newKey(list.get(0), userF))!= null)
                    throw new RestException(ErrorConstants.HAVE_SAME_RELATIONSHIP);
                session.beginTransaction();
                session.save(RelationshipDB.createNew(userF, list.get(0)));
                session.getTransaction().commit();
            }
            //// TODO: 02.11.2016 getListRequests
            session.close();
            return Ajax.emptyResponse();
        } catch (Exception e) {
            throw new RestException(e);
        }
    }


    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }
}