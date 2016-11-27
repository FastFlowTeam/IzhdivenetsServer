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

    public static NotReadedSuccessDB createNew(long l, long userId, int num) {
        return new NotReadedSuccessDB()
                .setChildId(userId)
                .setParentId(l)
                .setNumber(num);
    }

    @Id
    @Column(name = "parent_id", nullable = false)
    public long getParentId() {
        return parentId;
    }

    public NotReadedSuccessDB setParentId(long parentId) {
        this.parentId = parentId;
        return this;
    }

    @Id
    @Column(name = "child_id", nullable = false)
    public long getChildId() {
        return childId;
    }

    public NotReadedSuccessDB setChildId(long childId) {
        this.childId = childId;
        return this;
    }

    @Basic
    @Column(name = "number", nullable = false)
    public long getNumber() {
        return number;
    }

    public NotReadedSuccessDB setNumber(long number) {
        this.number = number;
        return this;
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
