package by.fastflow.DBModels.main;

import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import by.fastflow.utils.UpdatableDB;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "task_item", schema = "izh_scheme", catalog = "db")
public class TaskItemDB extends UpdatableDB<TaskItemDB> {
    private Long itemId;
    private String title;
    private String description;
    private String cost;
    private long listId;
    private long state;
    private Long workingUser;
    private long target;

    @Id
    @Column(name = "item_id", nullable = false)
    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getItemId() {
        return itemId;
    }

    public TaskItemDB setItemId(Long itemId) {
        this.itemId = itemId;
        return this;
    }

    @Basic
    @Column(name = "title", nullable = false, length = 30)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "cost", nullable = true, length = 200)
    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    @Basic
    @Column(name = "list_id", nullable = true)
    public long getListId() {
        return listId;
    }

    public TaskItemDB setListId(long listId) {
        this.listId = listId;
        return this;
    }

    @Basic
    @Column(name = "state", nullable = false)
    public long getState() {
        return state;
    }

    public TaskItemDB setState(long state) {
        this.state = state;
        return this;
    }

    @Basic
    @Column(name = "working_user", nullable = true)
    public Long getWorkingUser() {
        return workingUser;
    }

    public TaskItemDB setWorkingUser(Long workingUser) {
        this.workingUser = workingUser;
        return this;
    }

    @Basic
    @Column(name = "target", nullable = false)
    public long getTarget() {
        return target;
    }

    public void setTarget(long target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskItemDB that = (TaskItemDB) o;

        if (state != that.state) return false;
        if (target != that.target) return false;
        if (listId != that.listId) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (workingUser != null ? !workingUser.equals(that.workingUser) : that.workingUser != null) return false;
        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null) return false;
        if (cost != null ? !cost.equals(that.cost) : that.cost != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (int) (state ^ (state >>> 32));
        result = 31 * result + (int) (listId ^ (listId >>> 32));
        result = 31 * result + (workingUser != null ? workingUser.hashCode() : 0);
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        result = 31 * result + (int) (target ^ (target >>> 32));
        return result;
    }

    @Override
    public void updateBy(TaskItemDB up) {
        title = up.title;
        description = up.description;
        cost = up.cost;
        state = up.state;
        target = up.target;
        if (description == null)
            description = "";
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        UserDB.getUser(session, TaskListDB.getTaskList(session, listId).getUserId(), token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        UserDB.getUser(session, TaskListDB.getTaskList(session, listId).getUserId(), token);
    }

    @Override
    public TaskItemDB validate() throws RestException {
        if ((title == null) || (title.isEmpty()) || title.length() > 30)
            throw new RestException(ErrorConstants.EMPTY_TASK_TITLE);
        if ((description != null) && description.length() > 200)
            throw new RestException(ErrorConstants.LONG_TASK_DESCRIPTION);
        if ((cost == null) || (cost.isEmpty()) || cost.length() > 30)
            throw new RestException(ErrorConstants.EMPTY_TASK_COST);
        if (!Constants.contains(Constants.taskItem_state, state))
            throw new RestException(ErrorConstants.WRONG_TASK_STATE);
        if (!Constants.contains(Constants.taskItem_target, target))
            throw new RestException(ErrorConstants.WRONG_TASK_TARGET);
        if (description == null)
            description = "";
        return this;
    }

    public static TaskItemDB getTaskItem(Session session, long itemId) throws RestException {
        TaskItemDB taskItemDB = ((TaskItemDB) session.get(TaskItemDB.class, itemId));
        if (taskItemDB == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return taskItemDB;
    }

    public static JsonElement getJson(BigInteger itemId, String title, String description, String cost, BigInteger listId, BigInteger state, BigInteger target) {
        JsonObject obj = new JsonObject();
        obj.addProperty("itemId", itemId);
        obj.addProperty("title", title);
        obj.addProperty("description", description);
        obj.addProperty("cost", cost);
        obj.addProperty("listId", listId);
        obj.addProperty("state", state);
        obj.addProperty("target", target);
        return obj;
    }

    public JsonElement makeJson() {
        return getJson(BigInteger.valueOf(itemId),
                title,
                description,
                cost,
                BigInteger.valueOf(listId),
                BigInteger.valueOf(state),
                BigInteger.valueOf(target));
    }

}
