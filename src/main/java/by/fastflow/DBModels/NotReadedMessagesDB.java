package by.fastflow.DBModels;

import by.fastflow.DBModels.pk.NotReadedMessagesDBPK;

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
    private int number;

    @Id
    @Column(name = "user_id", nullable = false)
    public long getUserId() {
        return userId;
    }

    public NotReadedMessagesDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Id
    @Column(name = "dialog_id", nullable = false)
    public long getDialogId() {
        return dialogId;
    }

    public NotReadedMessagesDB setDialogId(long dialogId) {
        this.dialogId = dialogId;
        return this;
    }

    @Basic
    @Column(name = "number", nullable = false)
    public int getNumber() {
        return number;
    }

    public NotReadedMessagesDB setNumber(int number) {
        this.number = number;
        return this;
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

    public NotReadedMessagesDB readAll() {
        number = 0;
        return this;
    }

    public NotReadedMessagesDB next() {
        number++;
        return this;
    }

    public static NotReadedMessagesDB createNew(long dialogId, long userId, int i) {
        return new NotReadedMessagesDB()
                .setNumber(i)
                .setDialogId(dialogId)
                .setUserId(userId);
    }
}
