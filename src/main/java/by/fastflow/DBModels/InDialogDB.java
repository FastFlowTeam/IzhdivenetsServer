package by.fastflow.DBModels;

import by.fastflow.DBModels.pk.InDialogDBPK;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "in_dialog", schema = "izh_scheme", catalog = "db")
@IdClass(InDialogDBPK.class)
public class InDialogDB {
    private long dialogId;
    private long userId;

    public static InDialogDB createNew(Long user, long dialogId) {
        return new InDialogDB()
                .setUserId(user)
                .setDialogId(dialogId);
    }

    @Id
    @Column(name = "dialog_id", nullable = false)
    public long getDialogId() {
        return dialogId;
    }

    public InDialogDB setDialogId(long dialogId) {
        this.dialogId = dialogId;
        return this;
    }

    @Id
    @Column(name = "user_id", nullable = false)
    public long getUserId() {
        return userId;
    }

    public InDialogDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InDialogDB that = (InDialogDB) o;

        if (dialogId != that.dialogId) return false;
        if (userId != that.userId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (dialogId ^ (dialogId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
}
