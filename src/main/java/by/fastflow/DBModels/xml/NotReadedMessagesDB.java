package by.fastflow.DBModels.xml;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "not_readed_messages", schema = "izh_scheme", catalog = "db")
@IdClass(NotReadedMessagesDBPK.class)
public class NotReadedMessagesDB {
    private long userId;
    private long dialogId;
    private long number;

    @Id
    @Column(name = "user_id", nullable = false)
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Id
    @Column(name = "dialog_id", nullable = false)
    public long getDialogId() {
        return dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
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

        NotReadedMessagesDB that = (NotReadedMessagesDB) o;

        if (userId != that.userId) return false;
        if (dialogId != that.dialogId) return false;
        if (number != that.number) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (dialogId ^ (dialogId >>> 32));
        result = 31 * result + (int) (number ^ (number >>> 32));
        return result;
    }
}
