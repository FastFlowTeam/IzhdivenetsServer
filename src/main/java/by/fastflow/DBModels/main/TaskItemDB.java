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
@Table(name = "task_item", schema = "izh_scheme", catalog = "db")
public class TaskItemDB extends UpdatableDB<TaskItemDB>{
    private long itemId;
    private String title;
    private String description;
    private String cost;
    private long listId;
    private long state;
    private long workingUser;
    private long target;

    @Id
    @Column(name = "item_id", nullable = false)
    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
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
    public long getWorkingUser() {
        return workingUser;
    }

    public TaskItemDB setWorkingUser(long workingUser) {
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

        if (itemId != that.itemId) return false;
        if (state != that.state) return false;
        if (target != that.target) return false;
        if (workingUser != that.workingUser) return false;
        if (listId != that.listId) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (cost != null ? !cost.equals(that.cost) : that.cost != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (itemId ^ (itemId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (int) (state ^ (state >>> 32));
        result = 31 * result + (int) (listId ^ (listId >>> 32));
        result = 31 * result + (int) (workingUser ^ (workingUser >>> 32));
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
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        UserDB.getUser(session, WishListDB.getWishList(session, listId).getUserId(), token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        UserDB.getUser(session, WishListDB.getWishList(session, listId).getUserId(), token);
    }

    @Override
    public TaskItemDB setNextId(Session session) {
        try {
            itemId = ((TaskItemDB) session.createQuery("from TaskItemDB ORDER BY itemId DESC").setMaxResults(1).uniqueResult()).getItemId() + 1;
        } catch (Exception e) {
            itemId = 1;
        }
        return this;
    }

    @Override
    public TaskItemDB validate() throws RestException {
        if ((title == null) || (title.isEmpty())||title.length()>30)
            throw new RestException(ErrorConstants.EMPTY_TASK_TITLE);
        if ((description != null) && description.length()>200)
            throw new RestException(ErrorConstants.LONG_TASK_DESCRIPTION);
        if ((cost == null) || (cost.isEmpty())||cost.length()>30)
            throw new RestException(ErrorConstants.EMPTY_TASK_COST);
        if(!Constants.taskItem_state.contains(state))
            throw new RestException(ErrorConstants.WRONG_TASK_STATE);
        if(!Constants.taskItem_target.contains(target))
            throw new RestException(ErrorConstants.WRONG_TASK_TARGET);
        return this;
    }

    public static TaskItemDB getTaskItem(Session session, long itemId) throws RestException {
        TaskItemDB taskItemDB = ((TaskItemDB) session.get(TaskItemDB.class, itemId));
        if (taskItemDB == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return taskItemDB;
    }
}
