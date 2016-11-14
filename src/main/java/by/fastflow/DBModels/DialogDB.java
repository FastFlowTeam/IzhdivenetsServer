package by.fastflow.DBModels;

import by.fastflow.DBModels.pk.InDialogDBPK;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.NextableId;
import by.fastflow.utils.RestException;
import by.fastflow.utils.UpdatableDB;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.List;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "dialog", schema = "izh_scheme", catalog = "db")
public class DialogDB extends UpdatableDB<DialogDB> implements NextableId {
    private long dialogId;
    private String name;

    @Id
    @Column(name = "dialog_id", nullable = false)
    public long getDialogId() {
        return dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DialogDB dialogDB = (DialogDB) o;

        if (dialogId != dialogDB.dialogId) return false;
        if (name != null ? !name.equals(dialogDB.name) : dialogDB.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (dialogId ^ (dialogId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public void updateBy(DialogDB up) {
        this.name = up.name;
    }

    public void validate() throws RestException {
        if ((name == null) || (name.isEmpty()) || (name.length() > 30))
            throw new RestException(ErrorConstants.EMPTY_DIALOG_NAME);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        throw new RestException(ErrorConstants.PERMISSION_BY_TOKEN);
    }

    public static DialogDB createNew(String name) {
        DialogDB dialogDB = new DialogDB();
        dialogDB.setName(name);
        return dialogDB;
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        List<UserDB> list = session.createQuery("from UserDB where token = " + token).list();
        if (list.size() == 0)
            throw new RestException(ErrorConstants.PERMISSION_BY_TOKEN);
        for (UserDB user : list) {
            if (session.get(InDialogDB.class, new InDialogDBPK(user.getUserId(), dialogId)) != null)
                return;
        }
        throw new RestException(ErrorConstants.PERMISSION_BY_TOKEN);
    }

    @Override
    public void setNextId(Session session) {
        try {
            dialogId = ((DialogDB) session.createQuery("from DialogDB ORDER BY dialogId DESC").setMaxResults(1).uniqueResult()).getDialogId() + 1;
        } catch (Exception e) {
            dialogId = 1;
        }
    }

    public static DialogDB getDialog(Session session, long dialogId) throws RestException {
        DialogDB dialog = ((DialogDB) session.get(DialogDB.class, dialogId));
        if (dialog == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return dialog;
    }
}
