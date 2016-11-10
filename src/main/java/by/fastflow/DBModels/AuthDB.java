package by.fastflow.DBModels;

import by.fastflow.utils.*;
import org.hibernate.Session;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "auth", schema = "izh_scheme", catalog = "db")
public class AuthDB implements NextableId {
    private long authId;
    private long type;
    private long userId;
    private String token;

    @Id
    @Column(name = "auth_id", nullable = false)
    public long getAuthId() {
        return authId;
    }

    public void setAuthId(long authId) {
        this.authId = authId;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "type", nullable = true)
    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    @Basic
    @Column(name = "token", nullable = true, length = -1)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthDB authDB = (AuthDB) o;

        if (authId != authDB.authId) return false;
        if (type != authDB.type) return false;
        if (userId != authDB.userId) return false;
        if (token != null ? !token.equals(authDB.token) : authDB.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (authId ^ (authId >>> 32));
        result = 31 * result + (int) (type ^ (type >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    public static AuthDB createNew(Session session, int type, String token, long userId) {
        AuthDB authDB = new AuthDB();
        authDB.setToken(token);
        authDB.setType((long) type);
        authDB.setUserId(userId);
        authDB.setNextId(session);
        return authDB;
    }

    @Override
    public void setNextId(Session session) {
        authId = ((AuthDB) session.createQuery("from AuthDB ORDER BY authId DESC").setMaxResults(1).uniqueResult()).getAuthId() + 1;
    }
}
