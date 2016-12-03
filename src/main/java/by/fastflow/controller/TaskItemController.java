package by.fastflow.controller;

import by.fastflow.Ajax;
import by.fastflow.DBModels.TaskListPermissionsDB;
import by.fastflow.DBModels.TaskPermissionsDB;
import by.fastflow.DBModels.main.*;
import by.fastflow.DBModels.pk.TaskListPermissionsDBPK;
import by.fastflow.repository.HibernateSessionFactory;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.LIST;
import by.fastflow.utils.RestException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
public class TaskItemController extends ExceptionHandlerController {

    private static final String ADDRESS = Constants.DEF_SERVER + "taskitem";
    // TODO: фиксировать у ребенка кол-во успешных тасок

    @RequestMapping(ADDRESS + "/test/")
    String home() {
        return "Hello World! " + ADDRESS;
    }

    @RequestMapping(value = ADDRESS + "/work/{taskitem_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> working(@RequestHeader(value = "user_id") long userId,
                                @PathVariable(value = "taskitem_id") long taskitemId,
                                @RequestParam(value = "state") long state,
                                @RequestParam(value = "message") String message,
                                @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            UserDB up = UserDB.getUser(session, userId, token);
            if (up.isParent())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);

            TaskItemDB taskItemDB = TaskItemDB.getTaskItem(session, taskitemId);
            long sta = taskItemDB.getState();
            if (state == Constants.TASK_ITEM_IN_PROGRESS) {
                if (taskItemDB.getState() != Constants.TASK_ITEM_VISIBLE)
                    throw new RestException(ErrorConstants.WRONG_TASK_STATE);

                session.beginTransaction();
                session.saveOrUpdate(
                        taskItemDB
                                .setState(state)
                                .setWorkingUser(userId)
                );
                session.getTransaction().commit();
            } else {
                if (state == Constants.TASK_ITEM_DONE) {
                    if ((taskItemDB.getState() != Constants.TASK_ITEM_IN_PROGRESS) || (taskItemDB.getWorkingUser() != userId))
                        throw new RestException(ErrorConstants.WRONG_TASK_STATE);
                    if (taskItemDB.getWorkingUser() != userId)
                        throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
                    session.beginTransaction();
                    session.saveOrUpdate(
                            taskItemDB.setState(state)
                    );
                    session.getTransaction().commit();
                } else
                    throw new RestException(ErrorConstants.WRONG_TASK_STATE);
            }

            MessageController.generateMessage(session,
                    Constants.MSG_CHANGE_WORK_STATE,
                    userId,
                    DialogController.getTwainDialogId(session, userId, TaskListDB.getTaskList(session, taskItemDB.getListId()).getUserId()),
                    new LIST()
                            .add(taskItemDB.getTitle())
                            .add(getTextState(sta))
                            .add(getTextState(state))
                            .add(message));

            session.close();
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }


    @RequestMapping(value = ADDRESS + "/praise/{taskitem_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> praise(@RequestHeader(value = "user_id") long userId,
                               @PathVariable(value = "taskitem_id") long taskitemId,
                               @RequestParam(value = "money") long money,
                               @RequestParam(value = "message") String message,
                               @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            UserDB up = UserDB.getUser(session, userId, token);
            if (up.isChild())
                throw new RestException(ErrorConstants.NOT_CORRECT_USER_TYPE);

            TaskItemDB taskItemDB = TaskItemDB.getTaskItem(session, taskitemId);
            if ((taskItemDB.getState() != Constants.TASK_ITEM_DONE) || (taskItemDB.getState() != Constants.TASK_ITEM_PRAISED))
                throw new RestException(ErrorConstants.WRONG_TASK_STATE);
            if (TaskListDB.getTaskList(session, taskItemDB.getListId()).getUserId() != userId)
                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);

            session.beginTransaction();
            session.saveOrUpdate(
                    taskItemDB
                            .setState(Constants.TASK_ITEM_PRAISED)
            );
            session.getTransaction().commit();

            MessageController.generateMessage(session,
                    Constants.MSG_PRAISED,
                    userId,
                    DialogController.getTwainDialogId(session, userId, TaskListDB.getTaskList(session, taskItemDB.getListId()).getUserId()),
                    new LIST()
                            .add(taskItemDB.getTitle())
                            .add(message));

            if (money > 0) {
                CardDB card1 = (CardDB) session.createQuery("from CardDB where userId = " + up.getUserId()).list().get(0);
                CardDB card2 = (CardDB) session.createQuery("from CardDB where userId = " + taskItemDB.getWorkingUser()).list().get(0);
                if ((card1.getMoneyAmount() < money) || (money <= 0))
                    throw new RestException(ErrorConstants.NEGATIVE_CARD_MONEY);
                session.beginTransaction();
                session.update(card1.sub(money));
                session.update(card2.add(money));
                session.getTransaction().commit();

                MessageController.generateMessage(session,
                        Constants.MSG_SEND_MONEY,
                        userId,
                        DialogController.getTwainDialogId(session, userId, taskItemDB.getWorkingUser()),
                        new LIST()
                                .add(Constants.getStringMoney(money))
                );
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

    private String getTextState(long sta) {
        switch ((int) sta) {
            case Constants.TASK_ITEM_DONE:
                return "DONE";
            case Constants.TASK_ITEM_IN_PROGRESS:
                return "IN PROGRESS";
            case Constants.TASK_ITEM_VISIBLE:
                return "VISIBLE";
            case Constants.TASK_ITEM_PRAISED:
                return "PRAISED";
        }
        return "";
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
                    .setState(Constants.TASK_ITEM_VISIBLE)
                    .validate()
                    .setWorkingUser(null)
                    .setNextId(session));

            session.close();
            return Ajax.successResponse(taskItem);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/my/{tasklist_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getMy(@RequestHeader(value = "user_id") long userId,
                 @RequestHeader(value = "token") String token,
                 @PathVariable(value = "tasklist_id") long taskListId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            UserDB.getUser(session, TaskListDB.getTaskList(session, taskListId).getUserId(), token);
            List<Object[]> list = getParentTasksSee(session, taskListId);
            session.close();
            return Ajax.successResponseJson(getJsonArray(list));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/parent/{tasklist_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getParents(@RequestHeader(value = "user_id") long userId,
                      @RequestHeader(value = "token") String token,
                      @PathVariable(value = "tasklist_id") long taskListId) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            UserDB userDB = UserDB.getUser(session, userId, token);
            TaskListDB taskListDB = TaskListDB.getTaskList(session, taskListId);
            if (taskListDB.getVisibility() == Constants.TASK_LIST_NOBODY)
                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
            if (taskListDB.getVisibility() == Constants.TASK_LIST_ALLOWED_USERS) {
                if (session.get(TaskListPermissionsDB.class, TaskListPermissionsDBPK.createKey(userId, taskListId)) == null)
                    throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
            }
            List<Object[]> list = getUserTasksSee(session, taskListId, userId);

            session.close();
            return Ajax.successResponseJson(getJsonArray(list));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    private JsonElement getJsonArray(List<Object[]> list) {
        JsonArray array = new JsonArray();
        for (Object[] objects : list) {
            JsonObject obj = new JsonObject();
            obj.add("workingUser", objects[0] == null ? null : UserDB.getJson((String) objects[0], (BigInteger) objects[1], (String) objects[2], (BigInteger) objects[3]));
            obj.add("taskItem", TaskItemDB.makeJson((BigInteger) objects[4], (String) objects[5], (String) objects[6], (String) objects[7], (BigInteger) objects[8], (BigInteger) objects[9], (BigInteger) objects[10]));
            array.add(obj);
        }
        return array;
    }

    private List<Object[]> getParentTasksSee(Session session, long taskListId) {
        return session.createSQLQuery("select " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, " +
                "ti.item_id as a4, ti.title as a5, ti.description as a6, ti.cost as a7, ti.list_id as a8, ti.state as a9, ti.target as a10 " +
                "from izh_scheme.task_item ti " +
                "left join izh_scheme.user u on u.user_id = ti.working_user " +
                "where ti.list_id = " + taskListId).list();
    }

    private List<Object[]> getUserTasksSee(Session session, long taskListId, long userId) {
        return session.createSQLQuery("select " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, " +
                "ti.item_id as a4, ti.title as a5, ti.description as a6, ti.cost as a7, ti.list_id as a8, ti.state as a9, ti.target as a10 " +
                "from izh_scheme.task_item ti " +
                "left join izh_scheme.user u on u.user_id = ti.working_user " +
                "where ti.list_id = " + taskListId + " " +
                "and ti.target = " + Constants.TASK_ITEM_WORK_ALL + " " +
                "or (ti.target = " + Constants.TASK_ITEM_WORK_ALLOWED_USERS + " " +
                "and (select count(*) from izh_scheme.task_permissions pe where pe.user_id = " + userId + " and pe.item_id = ti.item_id) != 0)").list();
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
            TaskItemDB taskItemFromDB = TaskItemDB.getTaskItem(session, taskItemDB.getItemId());
            if (taskItemFromDB.getWorkingUser() != -1)
                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
            TaskItemDB up = taskItemDB.updateInBDWithToken(session, taskItemDB, token);
            session.close();
            return Ajax.successResponse(up);
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/permission/{taskitem_id}", method = RequestMethod.POST)
    public
    @ResponseBody
    String permission(@RequestHeader(value = "user_id") long userId,
                      @PathVariable(value = "taskitem_id") long taskitemId,
                      @RequestHeader(value = "token") String token,
                      @RequestBody List<Long> gIds) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            UserDB up = UserDB.getUser(session, userId, token);
            TaskItemDB taskItemDB = TaskItemDB.getTaskItem(session, taskitemId);
            TaskListDB taskListDB = TaskListDB.getTaskList(session, taskItemDB.getListId());

            if (taskListDB.getUserId() != up.getUserId())
                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
            if (taskItemDB.getTarget() != Constants.TASK_LIST_ALLOWED_USERS)
                throw new RestException(ErrorConstants.NOT_CORRECT_TYPE);
            if (taskItemDB.getWorkingUser() != -1)
                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);

            session.beginTransaction();
            List<Object[]> users = getAllPermissionUsers(session, taskItemDB.getItemId());
            for (Object[] user : users) {
                long us_gId = Constants.convertL(user[3]);
                if (gIds.contains(us_gId))
                    gIds.remove(us_gId);
                else
                    session.delete(TaskPermissionsDB.createNew(taskItemDB.getItemId(), Constants.convertL(user[4])));
            }

            for (Long gId : gIds) {
                List<Object[]> usAccept = getUserAccept(session, gId, userId);
                if (usAccept.size() == 0)
                    throw new RestException(ErrorConstants.NOT_HAVE_GID);
                if (Constants.convertL(usAccept.get(0)[0]) != Constants.RELATIONSHIP_ACCEPT)
                    throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
                session.merge(TaskPermissionsDB.createNew(taskItemDB.getItemId(), Constants.convertL(usAccept.get(0)[1])));
            }

            session.getTransaction().commit();

            List<Object[]> usrs = getAllPermissionUsers(session, taskitemId);
            session.close();
            return Ajax.successResponseJson(getJsonPermissionUsers(usrs));
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    @RequestMapping(value = ADDRESS + "/permissionUsers/{taskitem_id}", method = RequestMethod.GET)
    public
    @ResponseBody
    String permissionUsers(@RequestHeader(value = "user_id") long userId,
                           @PathVariable(value = "taskitem_id") long taskitemId,
                           @RequestHeader(value = "token") String token) throws RestException {
        try {
            Session session = HibernateSessionFactory
                    .getSessionFactory()
                    .openSession();

            UserDB up = UserDB.getUser(session, userId, token);
            TaskItemDB taskItemDB = TaskItemDB.getTaskItem(session, taskitemId);
            TaskListDB taskListDB = TaskListDB.getTaskList(session, taskItemDB.getListId());

            if (taskListDB.getUserId() != up.getUserId())
                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);

            List<Object[]> users = getAllPermissionUsers(session, taskItemDB.getItemId());

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
                "r.state as a0, u.user_id as a1 " +
                "FROM izh_scheme.relationship r " +
                "JOIN izh_scheme.user u ON r.recipient_id = u.user_id " +
                "WHERE r.sender_id = " + userId + " " +
                "AND u.g_id = " + gId + " " +
                "UNION " +
                "SELECT " +
                "r.state as a0, u.user_id as a1 " +
                "FROM izh_scheme.relationship r " +
                "JOIN izh_scheme.user u ON r.sender_id = u.user_id " +
                "WHERE r.recipient_id = " + userId + " " +
                "AND u.g_id = " + gId).list();
    }

    private List<Object[]> getAllPermissionUsers(Session session, long taskItemId) {
        return session.createSQLQuery("SELECT " +
                "u.chat_name as a0, u.type as a1, u.photo as a2, u.g_id as a3, u.user_id as a4 " +
                "FROM izh_scheme.task_permissions r " +
                "JOIN izh_scheme.user u ON u.user_id = r.user_id " +
                "WHERE r.item_id = " + taskItemId).list();
    }

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
            TaskItemDB taskItemDB = TaskItemDB.getTaskItem(session, taskitemId);
            if ((taskItemDB.getState() == Constants.TASK_ITEM_IN_PROGRESS) || (taskItemDB.getState() == Constants.TASK_ITEM_DONE))
                throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
            taskItemDB.delete(session, token);
            return Ajax.emptyResponse();
        } catch (RestException re) {
            throw re;
        } catch (Exception e) {
            throw new RestException(e);
        }
    }

    /*
    select title,description,cost,state,user_id,photo,chat_name from izh_scheme.task_item ti
left join izh_scheme."user" u on u.user_id = ti.working_user
where ti.list_id = 1
and target = 1 or (target = 3 and (select count(*) from izh_scheme.task_permissions where user_id = 1 and item_id = ti.item_id) != 0)
инфа по таскам и работающему над ними юзеру глазами ребенка
     */

    /*
    select title,description,cost,state,target,user_id,photo,chat_name from izh_scheme.task_item ti
left join izh_scheme."user" u on u.user_id = ti.working_user
where ti.list_id = 1
список тасок внутри листа глазами родителя, создавшего список (тоже наллами заполняет)
     */
}
