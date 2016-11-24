package by.fastflow.DBModels;

import by.fastflow.DBModels.pk.TaskListPermissionsDBPK;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "task_list_permissions", schema = "izh_scheme", catalog = "db")
@IdClass(TaskListPermissionsDBPK.class)
public class TaskListPermissionsDB {
    private long userId;
    private long listId;

    @Id
    @Column(name = "user_id", nullable = false)
    public long getUserId() {
        return userId;
    }

    public TaskListPermissionsDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Id
    @Column(name = "list_id", nullable = false)
    public long getListId() {
        return listId;
    }

    public TaskListPermissionsDB setListId(long listId) {
        this.listId = listId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskListPermissionsDB that = (TaskListPermissionsDB) o;

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

    public static TaskListPermissionsDB createNew(long listId, long userId) {
        return new TaskListPermissionsDB()
                .setListId(listId)
                .setUserId(userId);
    }
}
