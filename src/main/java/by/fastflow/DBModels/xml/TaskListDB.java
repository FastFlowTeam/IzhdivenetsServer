package by.fastflow.DBModels.xml;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "task_list", schema = "izh_scheme", catalog = "db")
public class TaskListDB {
    private long listId;
    private Long userId;
    private long visibility;
    private String name;
    private String description;
    private long canWork;

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
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    @Basic
    @Column(name = "can_work", nullable = false)
    public long getCanWork() {
        return canWork;
    }

    public void setCanWork(long canWork) {
        this.canWork = canWork;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskListDB that = (TaskListDB) o;

        if (listId != that.listId) return false;
        if (visibility != that.visibility) return false;
        if (canWork != that.canWork) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (listId ^ (listId >>> 32));
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (int) (visibility ^ (visibility >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (canWork ^ (canWork >>> 32));
        return result;
    }

//    public void validate() throws RestException {
//        if ((name == null) || (name.isEmpty())||name.length()>30)
//        throw new RestException(ErrorConstants.EMPTY_TASK_LIST_NAME);
//        if((visibility!=Constants.TASK_LIST_ALL)&&(visibility!=Constants.TASK_LIST_NOBODY)&&(visibility!=Constants.TASK_LIST_ALLOWED_USERS))
//            throw new RestException(ErrorConstants.WRONG_TASK_LIST_VISIBILITY);
//        if((canWork!=Constants.TASK_LIST_ALL)&&(canWork!=Constants.TASK_LIST_NOBODY)&&(canWork!=Constants.TASK_LIST_ALLOWED_USERS))
//            throw new RestException(ErrorConstants.WRONG_TASK_LIST_CAN_WORK);
//        if (description.length()>200)
//            throw new RestException(ErrorConstants.WRONG_TASK_LIST_DESCRIPTION);
//    }
}
