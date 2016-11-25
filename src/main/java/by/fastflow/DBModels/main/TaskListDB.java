package by.fastflow.DBModels.main;

import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import by.fastflow.utils.UpdatableDB;
import org.hibernate.Session;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "task_list", schema = "izh_scheme", catalog = "db")
public class TaskListDB extends UpdatableDB<TaskListDB>{
    private long listId;
    private long userId;
    private long visibility;
    private String name;
    private String description;

    @Id
    @Column(name = "list_id", nullable = false)
    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
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

        if (listId != that.listId) return false;
        if (visibility != that.visibility) return false;
        if (userId != that.userId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (listId ^ (listId >>> 32));
        result = 31 * result + (int) (visibility ^ (visibility >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public TaskListDB validate() throws RestException {
        if ((name == null) || (name.isEmpty()) || name.length() > 30)
            throw new RestException(ErrorConstants.EMPTY_TASK_LIST_NAME);
        if (!Constants.taskList_visibility.contains(visibility))
            throw new RestException(ErrorConstants.WRONG_TASK_LIST_VISIBILITY);
        if ((description!= null) && (description.length() > 200))
            throw new RestException(ErrorConstants.WRONG_TASK_LIST_DESCRIPTION);
        return this;
    }

    @Override
    public void updateBy(TaskListDB up) {
        visibility = up.visibility;
        name = up.name;
        description = up.description;
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        UserDB.getUser(session, userId, token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        UserDB.getUser(session, userId, token);
    }

    @Override
    public TaskListDB setNextId(Session session) {
        try {
            listId = ((TaskListDB) session.createQuery("from TaskListDB ORDER BY listId DESC").setMaxResults(1).uniqueResult()).getUserId() + 1;
        } catch (Exception e) {
            listId = 1;
        }
        return this;
    }

    public static TaskListDB getTaskList(Session session, long listId) throws RestException {
        TaskListDB taskListDB = ((TaskListDB) session.get(TaskListDB.class, listId));
        if (taskListDB == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return taskListDB;
    }
}
