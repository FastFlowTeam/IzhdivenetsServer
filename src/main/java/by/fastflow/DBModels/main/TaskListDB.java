package by.fastflow.DBModels.main;

import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import by.fastflow.utils.UpdatableDB;
import com.google.gson.JsonObject;
import org.hibernate.Session;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "task_list", schema = "izh_scheme", catalog = "db")
public class TaskListDB extends UpdatableDB<TaskListDB> {
    private Long listId;
    private long userId;
    private long visibility;
    private String name;
    private String description;

    @Id
    @Column(name = "list_id", nullable = false)
    public Long getListId() {
        return listId;
    }

    public TaskListDB setListId(Long listId) {
        this.listId = listId;
        return this;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public long getUserId() {
        return userId;
    }

    public TaskListDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Basic
    @Column(name = "visibility", nullable = false)
    public long getVisibility() {
        return visibility;
    }

    public void setVisibility(long visibility) {
        this.visibility = visibility;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskListDB that = (TaskListDB) o;

        if (visibility != that.visibility) return false;
        if (userId != that.userId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (listId != null ? !listId.equals(that.listId) : that.listId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (visibility ^ (visibility >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (listId != null ? listId.hashCode() : 0);
        return result;
    }

    @Override
    public TaskListDB validate() throws RestException {
        if ((name == null) || (name.isEmpty()) || name.length() > 30)
            throw new RestException(ErrorConstants.EMPTY_TASK_LIST_NAME);
        if (!Constants.contains(Constants.taskList_visibility, visibility))
            throw new RestException(ErrorConstants.WRONG_TASK_LIST_VISIBILITY);
        if ((description != null) && (description.length() > 200))
            throw new RestException(ErrorConstants.WRONG_TASK_LIST_DESCRIPTION);
        if (description == null)
            description = "";
        return this;
    }

    @Override
    public void updateBy(TaskListDB up) {
        visibility = up.visibility;
        name = up.name;
        description = up.description;
        if (description == null)
            description = "";
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        UserDB.getUser(session, userId, token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        UserDB.getUser(session, userId, token);
    }

    public static TaskListDB getTaskList(Session session, long listId) throws RestException {
        TaskListDB taskListDB = ((TaskListDB) session.get(TaskListDB.class, listId));
        if (taskListDB == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return taskListDB;
    }

    public static JsonObject getJson(BigInteger listId, BigInteger visibility, String name, String description) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("listId", listId);
        jsonObject.addProperty("visibility", visibility);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);
        return jsonObject;
    }

    public JsonObject makeJson() {
        return getJson(
                BigInteger.valueOf(listId),
                BigInteger.valueOf(visibility),
                name,
                description
        );
    }
}
