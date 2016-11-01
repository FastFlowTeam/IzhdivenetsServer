package by.fastflow.DBModels;

import by.fastflow.utils.Constants;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "relationship", schema = "izh_scheme", catalog = "db")
@IdClass(RelationshipDBPK.class)
public class RelationshipDB {
    private long state;
    private long senderId;
    private long recipientId;

    @Id
    @Column(name = "state", nullable = false)
    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    @Basic
    @Column(name = "sender_id", nullable = false)
    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    @Id
    @Column(name = "recipient_id", nullable = false)
    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
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
        result = 31 * result + (int) (senderId ^ (senderId >>> 32));
        result = 31 * result + (int) (recipientId ^ (recipientId >>> 32));
        return result;
    }

    public static RelationshipDB createNew(UserDB sender, UserDB recipient) {
        RelationshipDB relationshipDB = new RelationshipDB();
        relationshipDB.setRecipientId(recipient.getUserId());
        relationshipDB.setSenderId(sender.getUserId());
        relationshipDB.setState(Constants.RELATIONSHIP_CREATE);
        return relationshipDB;
    }
}
