package by.fastflow.DBModels;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by KuSu on 22.10.2016.
 */
public class NotReadedSuccessDBPK implements Serializable {
    private long parentId;
    private long childId;
    private long successId;

    @Column(name = "parent_id", nullable = false)
    @Id
    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Column(name = "child_id", nullable = false)
    @Id
    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    @Column(name = "success_id", nullable = false)
    @Id
    public long getSuccessId() {
        return successId;
    }

    public void setSuccessId(long successId) {
        this.successId = successId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotReadedSuccessDBPK that = (NotReadedSuccessDBPK) o;

        if (parentId != that.parentId) return false;
        if (childId != that.childId) return false;
        if (successId != that.successId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (parentId ^ (parentId >>> 32));
        result = 31 * result + (int) (childId ^ (childId >>> 32));
        result = 31 * result + (int) (successId ^ (successId >>> 32));
        return result;
    }
}
