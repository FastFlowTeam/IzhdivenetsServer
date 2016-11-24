package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.TaskListPermissionsDB;
import by.fastflow.DBModels.main.TaskItemDB;
import by.fastflow.DBModels.main.TaskListDB;
import by.fastflow.DBModels.main.UserDB;
import by.fastflow.DBModels.main.WishListDB;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by KuSu on 13.11.2016.
 */
@RestController
public class TaskItemController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "taskitem";

    // TODO: 21.11.2016 обновить разрешения
    // TODO: 21.11.2016 просмотр задач род/реб
    // TODO: 21.11.2016 взять задачу
    // TODO: 21.11.2016 закрыть задачу
    // TODO: 21.11.2016 подтвердить задачу - написать новый метод. Можно наградить без денег и без текста
    // TODO: фиксировать у ребенка кол-во успешных тасок

    // невидима перенести в цели "никто"

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }


    @RequestMapping(value = ADDRESS + "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@RequestHeader(value = "user_id") long userId,
                               @RequestHeader(value = "token") String token,
                               @RequestBody TaskItemDB taskItem) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            if (UserDB.getUser(session, TaskListDB.getTaskList(session, taskItem.getListId()).getUserId(), token) == null)
                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);

            session.beginTransaction();
            session.save(taskItem
                    .validate()
                    .setState(Constants.TASK_ITEM_VISIBLE)
                    .setWorkingUser(-1)
                    .setNextId(session));

            session.close();
            return Ajax.successResponse(taskItem);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/update", method = RequestMethod.PUT)
    public
    @ResponseBody
    Map<String, Object> update(@RequestHeader(value = "user_id") long userId,
                               @RequestHeader(value = "token") String token,
                               @RequestBody TaskItemDB taskItemDB) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            TaskItemDB up = taskItemDB.updateInBDWithToken(session, TaskItemDB.getTaskItem(session, taskItemDB.getItemId()), token);
            session.close();
            return Ajax.successResponse(up);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
//
//    @RequestMapping(value = ADDRESS + "/permission/{user_id}/{tasklist_id}", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String permission(@PathVariable(value = "user_id") long userId,
//                      @PathVariable(value = "tasklist_id") long taskListId,
//                      @RequestHeader(value = "token") String token,
//                      @RequestBody List<Long> gIds) throws RestException {
//        try {
//            Session session = HibernateSessionFactory
//                    .getSessionFactory()
//                    .openSession();
//
//            UserDB up = UserDB.getUser(session, userId, token);
//            TaskListDB list = TaskListDB.getTaskList(session, taskListId);
//            if (list.getUserId() != up.getUserId())
//                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
//            if ((list.getVisibility() != Constants.TASK_LIST_ALLOWED_USERS) && (list.getCanWork() != Constants.TASK_LIST_WORK_ALLOWED_USERS))
//                throw new RestException(ErrorConstants.NOT_CORRECT_TYPE);
//
//            session.beginTransaction();
//            List<Object[]> users = getAllPermissionUsers(session, taskListId);
//            for (Object[] user : users) {
//                long us_gId = Constants.convertL(user[3]);
//                if (gIds.contains(us_gId))
//                    gIds.remove(us_gId);
//                else
//                    session.delete(TaskListPermissionsDB.createNew(list.getListId(), Constants.convertL(user[4])));
//            }
//
//            for (Long gId : gIds) {
//                List<Object[]> usAccept = getUserAccept(session, gId, userId);
//                if (usAccept.size() == 0)
//                    throw new RestException(ErrorConstants.NOT_HAVE_GID);
//                if (Constants.convertL(usAccept.get(0)[0]) != Constants.RELATIONSHIP_ACCEPT)
//                    throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
//                session.saveOrUpdate(TaskListPermissionsDB.createNew(list.getListId(), Constants.convertL(usAccept.get(0)[1])));
//            }
//
//            session.getTransaction().commit();
//
//            List<Object[]> usrs = getAllPermissionUsers(session, taskListId);
//            session.close();
//            return Ajax.successResponseJson(getJsonPermissionUsers(usrs));
//        } catch (RestException re) {
//            throw re;
//        } catch (Exception e) {
//            throw new RestException(e);
//        }
//    }
//
//    @RequestMapping(value = ADDRESS + "/permissionUsers/{user_id}/{tasklist_id}", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String permissionUsers(@PathVariable(value = "user_id") long userId,
//                           @PathVariable(value = "tasklist_id") long taskListId,
//                           @RequestHeader(value = "token") String token) throws RestException {
//        try {
//            Session session = HibernateSessionFactory
//                    .getSessionFactory()
//                    .openSession();
//
//            UserDB up = UserDB.getUser(session, userId, token);
//            TaskListDB list = TaskListDB.getTaskList(session, taskListId);
//            if (list.getUserId() != up.getUserId())
//                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
//            if ((list.getVisibility() != Constants.TASK_LIST_ALLOWED_USERS) && (list.getCanWork() != Constants.TASK_LIST_WORK_ALLOWED_USERS))
//                throw new RestException(ErrorConstants.NOT_CORRECT_TYPE);
//
//            List<Object[]> users = getAllPermissionUsers(session, taskListId);
//
//            session.close();
//            return Ajax.successResponseJson(getJsonPermissionUsers(users));
//        } catch (RestException re) {
//            throw re;
//        } catch (Exception e) {
//            throw new RestException(e);
//        }
//    }
//
//    private JsonArray getJsonPermissionUsers(List<Object[]> list) {
//        JsonArray array = new JsonArray();
//        for (Object[] objects : list) {
//            array.add(UserDB.getJson((String) objects[0], (BigInteger) objects[1], (String) objects[2], (BigInteger) objects[3]));
//        }
//        return array;
//    }
//
//    private List<Object[]> getUserAccept(Session session, Long gId, long userId) {
//        return session.createSQLQuery("SELECT " +
//                "r.state as a0, u.user_id as a1 " +
//                "FROM izh_scheme.relationship r " +
//                "JOIN izh_scheme.user u ON r.recipient_id = u.user_id " +
//                "WHERE r.sender_id = " + userId + " " +
//                "AND u.g_id = " + gId + " " +
//                "UNION " +
//                "SELECT " +
//                "r.state as a0, u.user_id as a1 " +
//                "FROM izh_scheme.relationship r " +
//                "JOIN izh_scheme.user u ON r.sender_id = u.user_id " +
//                "WHERE r.recipient_id = " + userId + " " +
//                "AND u.g_id = " + gId).list();
//    }
//
//    private List<Object[]> getAllPermissionUsers(Session session, long taskListId) {
//        return session.createSQLQuery("SELECT " +
//                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, u.user_id as a4 " +
//                "FROM izh_scheme.task_list_permissions r " +
//                "JOIN izh_scheme.user u ON u.user_id = r.user_id " +
//                "WHERE r.list_id = " + taskListId).list();
//    }

    @RequestMapping(value = ADDRESS + "/delete/{taskitem_id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Map<String, Object> delete(@RequestHeader(value = "token") String token,
                               @RequestHeader(value = "user_id") long userId,
                               @PathVariable(value = "taskitem_id") long taskitemId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            TaskItemDB.getTaskItem(session, taskitemId).delete(session, token);
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
}
