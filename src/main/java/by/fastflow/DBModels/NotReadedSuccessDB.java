package by.fastflow.DBModels;

import by.fastflow.DBModels.pk.NotReadedSuccessDBPK;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "not_readed_success", schema = "izh_scheme", catalog = "db")
@IdClass(NotReadedSuccessDBPK.class)
public class NotReadedSuccessDB {
    private long parentId;
    private long childId;
    private long number;

    public NotReadedSuccessDB(UserDB user, UserDB child, int num) {
        parentId = user.getUserId();
        childId = child.getUserId();
        number = num;
    }

    public NotReadedSuccessDB(long l, long userId, int num) {
        parentId = l;
        childId = userId;
        number = num;
    }

    public NotReadedSuccessDB() {
    }

    @Id
    @Column(name = "parent_id", nullable = false)
    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Id
    @Column(name = "child_id", nullable = false)
    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    @Basic
    @Column(name = "number", nullable = false)
    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotReadedSuccessDB that = (NotReadedSuccessDB) o;

        if (parentId != that.parentId) return false;
        if (childId != that.childId) return false;
        if (number != that.number) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (parentId ^ (parentId >>> 32));
        result = 31 * result + (int) (childId ^ (childId >>> 32));
        result = 31 * result + (int) (number ^ (number >>> 32));
        return result;
    }

    public void readAll() {
        number = 0;
    }

    public void moreNotRead() {
        number++;
    }
}
