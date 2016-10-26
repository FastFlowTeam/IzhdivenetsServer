package by.fastflow.DBModels;

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
    private long successId;
    private long number;

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

    @Id
    @Column(name = "success_id", nullable = false)
    public long getSuccessId() {
        return successId;
    }

    public void setSuccessId(long successId) {
        this.successId = successId;
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
        if (successId != that.successId) return false;
        if (number != that.number) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (parentId ^ (parentId >>> 32));
        result = 31 * result + (int) (childId ^ (childId >>> 32));
        result = 31 * result + (int) (successId ^ (successId >>> 32));
        result = 31 * result + (int) (number ^ (number >>> 32));
        return result;
    }
}
