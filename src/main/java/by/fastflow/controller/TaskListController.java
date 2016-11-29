package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.main.TaskItemDB;
import by.fastflow.DBModels.main.TaskListDB;
import by.fastflow.DBModels.TaskListPermissionsDB;
import by.fastflow.DBModels.main.UserDB;
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
public class TaskListController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "tasklist";

    // TODO: 21.11.2016 просмотр списков - ребенок, получить еще количества все/выполняются/выполнено/отмечено, + родителя
    // TODO: 21.11.2016 просмотр списков - родитель, получить еще количества все/выполняются/выполнено/отмечено

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }


    @RequestMapping(value = ADDRESS + "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> create(@RequestHeader(value = "user_id") long userId,
                               @RequestHeader(value = "token") String token,
                               @RequestBody TaskListDB taskList) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            UserDB up = UserDB.getUser(session, userId, token);
            if (up.isChild())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);

            session.beginTransaction();
            session.save(taskList
                    .validate()
                    .setUserId(userId)
                    .setNextId(session));

            session.close();
            return Ajax.successResponse(taskList);
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
                               @RequestBody TaskListDB taskList) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();
            TaskListDB listDB = TaskListDB.getTaskList(session, taskList.getListId());
            if (listDB.getVisibility() != taskList.getVisibility())
                if (taskList.getVisibility() != Constants.TASK_LIST_ALL)
                    permissionInProgresInList(session, taskList.getListId());
            TaskListDB up = taskList.updateInBDWithToken(session, listDB, token);
            session.close();
            return Ajax.successResponse(up);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private void permissionInProgresInList(Session session, long listId) throws RestException {
        if (session.createQuery("from TaskItemDB where listId = " + listId + " and state " + Constants.TASK_ITEM_IN_PROGRESS).list().size() > 0)
            throw new RestException(ErrorConstants.TASK_IN_PROGRESS);
    }

    @RequestMapping(value = ADDRESS + "/permission/{tasklist_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    String permission(@RequestHeader(value = "user_id") long userId,
                      @PathVariable(value = "tasklist_id") long taskListId,
                      @RequestHeader(value = "token") String token,
                      @RequestBody List<Long> gIds) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            TaskListDB list = TaskListDB.getTaskList(session, taskListId);
            UserDB up = UserDB.getUser(session, list.getUserId(), token);

            if (list.getVisibility() != Constants.TASK_LIST_ALLOWED_USERS)
                throw new RestException(ErrorConstants.WRONG_TASK_LIST_VISIBILITY);

            List<TaskItemDB> items = session.createQuery("from TaskItemDB where listId = " + taskListId + " and state = " + Constants.TASK_ITEM_IN_PROGRESS).list();

            session.beginTransaction();
            List<Object[]> users = getAllPermissionUsers(session, taskListId);
            for (Object[] user : users) {
                long us_gId = Constants.convertL(user[3]);
                if (gIds.contains(us_gId))
                    gIds.remove(us_gId);
                else {
                    long temp = Constants.convertL(user[4]);
                    for (TaskItemDB item : items)
                        if (item.getWorkingUser() == temp)
                            throw new RestException(ErrorConstants.TASK_IN_PROGRESS);
                    session.delete(TaskListPermissionsDB.createNew(list.getListId(), temp));
                }
            }

            for (Long gId : gIds) {
                List<Object[]> usAccept = getUserAccept(session, gId, userId);
                if (usAccept.size() == 0)
                    throw new RestException(ErrorConstants.NOT_HAVE_GID);
                if (Constants.convertL(usAccept.get(0)[2]) != Constants.USER_CHILD)
                    throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);
                if (Constants.convertL(usAccept.get(0)[0]) != Constants.RELATIONSHIP_ACCEPT)
                    throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
                session.merge(TaskListPermissionsDB.createNew(list.getListId(), Constants.convertL(usAccept.get(0)[1])));
            }

            session.getTransaction().commit();

            List<Object[]> usrs = getAllPermissionUsers(session, taskListId);
            session.close();
            return Ajax.successResponseJson(getJsonPermissionUsers(usrs));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/permissionUsers/{tasklist_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String permissionUsers(@RequestHeader(value = "user_id") long userId,
                           @PathVariable(value = "tasklist_id") long taskListId,
                           @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            TaskListDB list = TaskListDB.getTaskList(session, taskListId);
            UserDB up = UserDB.getUser(session, list.getUserId(), token);

            List<Object[]> users = getAllPermissionUsers(session, taskListId);

            session.close();
            return Ajax.successResponseJson(getJsonPermissionUsers(users));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private JsonArray getJsonPermissionUsers(List<Object[]> list) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            array.add(UserDB.getJson((String) objects[0], (BigInteger) objects[1], (String) objects[2], (BigInteger) objects[3]));
        }
        return array;
    }

    private List<Object[]> getUserAccept(Session session, Long gId, long userId) {
        return session.createSQLQuery("SELECT " +
                "r.state as a0, u.user_id as a1, u.type as a2 " +
                "FROM izh_scheme.relationship r " +
                "JOIN izh_scheme.user u ON r.recipient_id = u.user_id " +
                "WHERE r.sender_id = " + userId + " " +
                "AND u.g_id = " + gId + " " +
                "UNION " +
                "SELECT " +
                "r.state as a0, u.user_id as a1, u.type as a2 " +
                "FROM izh_scheme.relationship r " +
                "JOIN izh_scheme.user u ON r.sender_id = u.user_id " +
                "WHERE r.recipient_id = " + userId + " " +
                "AND u.g_id = " + gId).list();
    }

    private List<Object[]> getAllPermissionUsers(Session session, long taskListId) {
        return session.createSQLQuery("SELECT " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, u.user_id as a4 " +
                "FROM izh_scheme.task_list_permissions r " +
                "JOIN izh_scheme.user u ON u.user_id = r.user_id " +
                "WHERE r.list_id = " + taskListId).list();
    }

    @RequestMapping(value = ADDRESS + "/delete/{tasklist_id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Map<String, Object> delete(@RequestHeader(value = "token") String token,
                               @RequestHeader(value = "user_id") long userId,
                               @PathVariable(value = "tasklist_id") long tasklistId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            TaskListDB listDB = TaskListDB.getTaskList(session, tasklistId);
            permissionInProgresInList(session, tasklistId);
            listDB.delete(session, token);

            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }
}
