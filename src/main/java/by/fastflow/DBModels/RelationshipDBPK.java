package by.fastflow.DBModels;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by KuSu on 22.10.2016.
 */
public class RelationshipDBPK implements Serializable {
    private long state;
    private long senderId;

    @Column(name = "state", nullable = false)
    @Id
    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
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

        if (state != that.state) return false;
        if (senderId != that.senderId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (state ^ (state >>> 32));
        result = 31 * result + (int) (senderId ^ (senderId >>> 32));
        return result;
    }
}
