package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.AuthDB;
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
    Map<String, Object> create(@PathVariable(value = "user_id") Long userId,
                               @RequestHeader(value = "token") String token, @RequestParam(value = "recipient") long gId) throws RestException {
        try {

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