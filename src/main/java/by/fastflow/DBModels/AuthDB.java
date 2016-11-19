package by.fastflow.DBModels;

import by.fastflow.utils.*;
import org.hibernate.Session;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "auth", schema = "izh_scheme", catalog = "db")
public class AuthDB extends NextableId {
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

    public AuthDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Basic
    @Column(name = "type", nullable = true)
    public long getType() {
        return type;
    }

    public AuthDB setType(long type) {
        this.type = type;
        return this;
    }

    @Basic
    @Column(name = "token", nullable = true, length = -1)
    public String getToken() {
        return token;
    }

    public AuthDB setToken(String token) {
        this.token = token;
        return this;
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
        return new AuthDB()
                .setToken(token)
                .setType((long) type)
                .setUserId(userId)
                .setNextId(session);
    }

    @Override
    public AuthDB setNextId(Session session) {
        try {
            authId = ((AuthDB) session.createQuery("from AuthDB ORDER BY authId DESC").setMaxResults(1).uniqueResult()).getAuthId() + 1;
        } catch (Exception e) {
            authId = 1;
        }
        return this;
    }

    @Override
    public AuthDB validate() throws RestException {
        return this;
    }
}
