package by.fastflow.DBModels.main;

import by.fastflow.DBModels.InDialogDB;
import by.fastflow.DBModels.pk.InDialogDBPK;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import by.fastflow.utils.UpdatableDB;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "dialog", schema = "izh_scheme", catalog = "db")
public class DialogDB extends UpdatableDB<DialogDB> {
    private Long dialogId;
    private String name;

    @Id
    @Column(name = "dialog_id", nullable = false)
    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getDialogId() {
        return dialogId;
    }

    public DialogDB setDialogId(Long dialogId) {
        this.dialogId = dialogId;
        return this;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 30)
    public String getName() {
        return name;
    }

    public DialogDB setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DialogDB dialogDB = (DialogDB) o;

        if (name != null ? !name.equals(dialogDB.name) : dialogDB.name != null) return false;
        if (dialogId != null ? !dialogId.equals(dialogDB.dialogId) : dialogDB.dialogId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (dialogId != null ? dialogId.hashCode() : 0);
        return result;
    }

    @Override
    public void updateBy(DialogDB up) {
        this.name = up.name;
    }

    @Override
    public DialogDB validate() throws RestException {
        if ((name == null) || (name.isEmpty()) || (name.length() > 30))
            throw new RestException(ErrorConstants.EMPTY_DIALOG_NAME);
        return this;
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
    }

    public static DialogDB createNew(String name) {
        return new DialogDB()
                .setName(name)
                .setDialogId(null);
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        List<UserDB> list = session.createQuery("from UserDB where token = '" + token + "'").list();
        if (list.size() == 0)
            throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
        for (UserDB user : list) {
            if (session.get(InDialogDB.class, InDialogDBPK.createKey(user.getUserId(), dialogId)) != null)
                return;
        }
        throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
    }

    public static DialogDB getDialog(Session session, long dialogId) throws RestException {
        DialogDB dialog = ((DialogDB) session.get(DialogDB.class, dialogId));
        if (dialog == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return dialog;
    }

    public JsonElement makeJson() {
        JsonObject object = new JsonObject();
        object.addProperty("dialogId", dialogId);
        object.addProperty("name", name);
        return object;
    }
}
