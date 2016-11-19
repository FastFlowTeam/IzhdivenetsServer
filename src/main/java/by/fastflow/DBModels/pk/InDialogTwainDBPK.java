package by.fastflow.DBModels.pk;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by KuSu on 22.10.2016.
 */
public class InDialogTwainDBPK implements Serializable {
    private long dialogId;
    private long userFId;
    private long userSId;

    public InDialogTwainDBPK(long userSId, long userFId, long dialogId) {
        this.userSId = userSId;
        this.userFId = userFId;
        this.dialogId = dialogId;
    }

    @Column(name = "dialog_id", nullable = false)
    @Id
    public long getDialogId() {
        return dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    @Column(name = "first_user", nullable = false)
    @Id
    public long getUserFId() {
        return userFId;
    }

    public void setUserFId(long userFId) {
        this.userFId = userFId;
    }

    @Column(name = "second_user", nullable = false)
    @Id
    public long getUserSId() {
        return userSId;
    }

    public void setUserSId(long userSId) {
        this.userSId = userSId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InDialogTwainDBPK that = (InDialogTwainDBPK) o;

        if (dialogId != that.dialogId) return false;
        if (userSId != that.userSId) return false;
        if (userFId != that.userFId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (dialogId ^ (dialogId >>> 32));
        result = 31 * result + (int) (userSId ^ (userSId >>> 32));
        result = 31 * result + (int) (userFId ^ (userFId >>> 32));
        return result;
    }
}
