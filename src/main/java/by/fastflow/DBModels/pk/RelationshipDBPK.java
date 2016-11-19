package by.fastflow.DBModels.pk;

import by.fastflow.DBModels.UserDB;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by KuSu on 22.10.2016.
 */
public class RelationshipDBPK implements Serializable {
    private long recipientId;
    private long senderId;

    @Column(name = "recipient_id", nullable = false)
    @Id
    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    @Column(name = "sender_id", nullable = false)
    @Id
    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationshipDBPK that = (RelationshipDBPK) o;

        if (recipientId != that.recipientId) return false;
        if (senderId != that.senderId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (recipientId ^ (recipientId >>> 32));
        result = 31 * result + (int) (senderId ^ (senderId >>> 32));
        return result;
    }

    public static RelationshipDBPK newKey(UserDB sender, UserDB recipient) {
        RelationshipDBPK key = new RelationshipDBPK();
        key.setSenderId(sender.getUserId());
        key.setRecipientId(recipient.getUserId());
        return key;
    }

    public static RelationshipDBPK newKey(long sender, long recipient) {
        RelationshipDBPK key = new RelationshipDBPK();
        key.setSenderId(sender);
        key.setRecipientId(recipient);
        return key;
    }
}
