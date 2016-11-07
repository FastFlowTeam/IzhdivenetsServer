package by.fastflow.DBModels;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "dialog", schema = "izh_scheme", catalog = "db")
public class DialogDB {
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
    public void validate() throws RestException {

        if ((name == null) || (name.isEmpty())|| (name.length()>30))
            throw new RestException(ErrorConstants.EMPTY_DIALOG_NAME);

    }

}
