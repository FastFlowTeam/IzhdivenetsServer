package by.fastflow.DBModels;

import by.fastflow.DBModels.pk.RelationshipDBPK;
import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import by.fastflow.utils.UpdatableDB;
import org.hibernate.Session;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "relationship", schema = "izh_scheme", catalog = "db")
@IdClass(RelationshipDBPK.class)
public class RelationshipDB extends UpdatableDB<RelationshipDB> {
    private int state;
    private UserDB senderId;
    private UserDB recipientId;

    @Basic
    @Column(name = "state", nullable = false)
    public int getState() {
        return state;
    }

    public RelationshipDB setState(int state) {
        this.state = state;
        return this;
    }

    @Id
    @ManyToOne
    @Column(name = "sender_id", nullable = false)
    public UserDB getSenderId() {
        return senderId;
    }

    @Id
    @ManyToOne
    @Column(name = "recipient_id", nullable = false)
    public UserDB getRecipientId() {
        return recipientId;
    }


    public RelationshipDB setSenderId(UserDB senderId) {
        this.senderId = senderId;
        return this;
    }

    public RelationshipDB setRecipientId(UserDB recipientId) {
        this.recipientId = recipientId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipDB that = (RelationshipDB) o;
        if (state != that.state) return false;
        if (senderId != null ? !senderId.equals(that.senderId) : that.senderId != null) return false;
        if (recipientId != null ? !recipientId.equals(that.recipientId) : that.recipientId != null) return false;
        return true;
    }


    @Override
    public int hashCode() {
        int result = state;
        result = 31 * result + (senderId != null ? senderId.hashCode() : 0);
        result = 31 * result + (recipientId != null ? recipientId.hashCode() : 0);
        return result;
    }

    public static RelationshipDB createNew(UserDB sender, UserDB recipient, int state) {
        return new RelationshipDB()
                .setRecipientId(recipient)
                .setSenderId(sender)
                .setState(state);
    }

    public static RelationshipDB createNew(UserDB sender, UserDB recipient) {
        return createNew(sender, recipient, Constants.RELATIONSHIP_CREATE);
    }

    @Override
    public void updateBy(RelationshipDB up) {
        this.state = up.state;
    }

    @Override
    public void validate() throws RestException {
        if (!Constants.relationship_types.contains(state))
            throw new RestException(ErrorConstants.WRONG_RELATIONSHIP_STATE);
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        UserDB.getUser(session, recipientId.getUserId(), token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        UserDB userDB1 = UserDB.getUser(session, recipientId.getUserId());
        UserDB userDB2 = UserDB.getUser(session, senderId.getUserId());
        if ((!userDB1.getToken().equals(token)) && (!userDB2.getToken().equals(token)))
            throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
    }

    public boolean notAccepted() {
        return state != Constants.RELATIONSHIP_ACCEPT;
    }

    @Override
    public RelationshipDB setNextId(Session session) {
        return null;
    }
}
