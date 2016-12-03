package by.fastflow.DBModels.pk;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by KuSu on 22.10.2016.
 */
public class TaskListPermissionsDBPK implements Serializable {
    private long userId;
    private long listId;

    @Column(name = "user_id", nullable = false)
    @Id
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "list_id", nullable = false)
    @Id
    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskListPermissionsDBPK that = (TaskListPermissionsDBPK) o;

        if (userId != that.userId) return false;
        if (listId != that.listId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (listId ^ (listId >>> 32));
        return result;
    }

    public static TaskListPermissionsDBPK createKey(long userId, long listId){
        TaskListPermissionsDBPK taskListPermissionsDBPK = new TaskListPermissionsDBPK();
        taskListPermissionsDBPK.setListId(listId);
        taskListPermissionsDBPK.setUserId(userId);
        return taskListPermissionsDBPK;
    }
}
