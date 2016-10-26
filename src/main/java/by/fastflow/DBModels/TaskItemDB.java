package by.fastflow.DBModels;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "task_item", schema = "izh_scheme", catalog = "db")
public class TaskItemDB {
    private long itemId;
    private String title;
    private String description;
    private String cost;
    private Long listId;
    private long state;
    private Long workingUser;
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
    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    @Basic
    @Column(name = "state", nullable = false)
    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    @Basic
    @Column(name = "working_user", nullable = true)
    public Long getWorkingUser() {
        return workingUser;
    }

    public void setWorkingUser(Long workingUser) {
        this.workingUser = workingUser;
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
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (cost != null ? !cost.equals(that.cost) : that.cost != null) return false;
        if (listId != null ? !listId.equals(that.listId) : that.listId != null) return false;
        if (workingUser != null ? !workingUser.equals(that.workingUser) : that.workingUser != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (itemId ^ (itemId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (listId != null ? listId.hashCode() : 0);
        result = 31 * result + (int) (state ^ (state >>> 32));
        result = 31 * result + (workingUser != null ? workingUser.hashCode() : 0);
        result = 31 * result + (int) (target ^ (target >>> 32));
        return result;
    }
}
