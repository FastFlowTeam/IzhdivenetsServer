package by.fastflow.DBModels;

import by.fastflow.DBModels.pk.InDialogDBPK;
import by.fastflow.DBModels.pk.InDialogTwainDBPK;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "in_dialog_twain", schema = "izh_scheme", catalog = "db")
@IdClass(InDialogTwainDBPK.class)
public class InDialogTwainDB {
    private long dialogId;
    private long userFId;
    private long userSId;

    public static InDialogTwainDB createNew(long userF, long userS, long dialogId) {
        return new InDialogTwainDB()
                .setUserFId(userF)
                .setUserSId(userS)
                .setDialogId(dialogId);
    }

    @Id
    @Column(name = "dialog_id", nullable = false)
    public long getDialogId() {
        return dialogId;
    }

    public InDialogTwainDB setDialogId(long dialogId) {
        this.dialogId = dialogId;
        return this;
    }

    @Id
    @Column(name = "first_user", nullable = false)
    public long getUserFId() {
        return userFId;
    }

    public InDialogTwainDB setUserFId(long userFId) {
        this.userFId = userFId;
        return this;
    }

    @Id
    @Column(name = "second_user", nullable = false)
    public long getUserSId() {
        return userSId;
    }

    public InDialogTwainDB setUserSId(long userSId) {
        this.userSId = userSId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InDialogTwainDB that = (InDialogTwainDB) o;

        if (dialogId != that.dialogId) return false;
        if (userFId != that.userFId) return false;
        if (userSId != that.userSId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (dialogId ^ (dialogId >>> 32));
        result = 31 * result + (int) (userFId ^ (userFId >>> 32));
        result = 31 * result + (int) (userSId ^ (userSId >>> 32));
        return result;
    }
}
