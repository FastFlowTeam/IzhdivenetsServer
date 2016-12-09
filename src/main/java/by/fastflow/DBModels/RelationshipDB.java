package by.fastflow.DBModels;

import by.fastflow.DBModels.main.UserDB;
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
    private long senderId;
    private long recipientId;

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
    @Column(name = "sender_id", nullable = false)
    public long getSenderId() {
        return senderId;
    }

    @Id
    @Column(name = "recipient_id", nullable = false)
    public long getRecipientId() {
        return recipientId;
    }


    public RelationshipDB setSenderId(long senderId) {
        this.senderId = senderId;
        return this;
    }

    public RelationshipDB setRecipientId(long recipientId) {
        this.recipientId = recipientId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipDB that = (RelationshipDB) o;
        if (state != that.state) return false;
        if (recipientId != that.recipientId) return false;
        if (senderId != that.senderId) return false;
        return true;
    }


    @Override
    public int hashCode() {
        int result = state;
        result = 31 * result + (int) (recipientId ^ (recipientId >>> 32));
        result = 31 * result + (int) (senderId ^ (senderId >>> 32));
        return result;
    }

    public static RelationshipDB createNew(UserDB sender, UserDB recipient, int state) {
        return new RelationshipDB()
                .setRecipientId(recipient.getUserId())
                .setSenderId(sender.getUserId())
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
    public RelationshipDB validate() throws RestException {
        if (!Constants.contains(Constants.relationship_types,state))
            throw new RestException(ErrorConstants.WRONG_RELATIONSHIP_STATE);
        return this;
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        UserDB.getUser(session, recipientId, token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        UserDB userDB1 = UserDB.getUser(session, recipientId, token);
//        UserDB userDB2 = UserDB.getUser(session, senderId.getUserId());
//        if ((!userDB1.getToken().equals(token)) && (!userDB2.getToken().equals(token)))
//            throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
    }

    public boolean notAccepted() {
        return state != Constants.RELATIONSHIP_ACCEPT;
    }
}
