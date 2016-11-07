package by.fastflow.DBModels;

import by.fastflow.utils.Constants;
import com.vk.api.sdk.objects.users.User;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "relationship", schema = "izh_scheme", catalog = "db")
@IdClass(RelationshipDBPK.class)
public class RelationshipDB {
    private long state;
    private UserDB senderId;
    private UserDB recipientId;

    @Basic
    @Column(name = "state", nullable = false)
    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
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

    public void setSenderId(UserDB senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(UserDB recipientId) {
        this.recipientId = recipientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationshipDB that = (RelationshipDB) o;

        if (state != that.state) return false;
        if (senderId != that.senderId) return false;
        if (recipientId != that.recipientId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (state ^ (state >>> 32));
//        result = 31 * result + (int) (senderId ^ (senderId >>> 32));
//        result = 31 * result + (int) (recipientId ^ (recipientId >>> 32));
        return result;
    }

    public static RelationshipDB createNew(UserDB sender, UserDB recipient) {
        RelationshipDB relationshipDB = new RelationshipDB();
//        relationshipDB.setRecipientId(recipient.getUserId());
//        relationshipDB.setSenderId(sender.getUserId());
        relationshipDB.setState(Constants.RELATIONSHIP_CREATE);
        return relationshipDB;
    }
}
