package by.fastflow.DBModels.pk;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by KuSu on 22.10.2016.
 */
public class NotReadedMessagesDBPK implements Serializable {
    private long userId;
    private long dialogId;

    public NotReadedMessagesDBPK(long userId, long dialogId) {
        this.userId = userId;
        this.dialogId = dialogId;
    }

    @Column(name = "user_id", nullable = false)
    @Id
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Column(name = "dialog_id", nullable = false)
    @Id
    public long getDialogId() {
        return dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotReadedMessagesDBPK that = (NotReadedMessagesDBPK) o;

        if (userId != that.userId) return false;
        if (dialogId != that.dialogId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (dialogId ^ (dialogId >>> 32));
        return result;
    }
}
