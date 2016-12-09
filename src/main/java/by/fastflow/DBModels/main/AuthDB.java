package by.fastflow.DBModels.main;

import by.fastflow.utils.*;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "auth", schema = "izh_scheme", catalog = "db")
public class AuthDB extends Validatable<AuthDB>{
    private Long authId;
    private long type;
    private long userId;
    private String token;

    @Id
    @Column(name = "auth_id", nullable = false)
    @GenericGenerator(name="kaugen", strategy = "increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getAuthId() {
        return authId;
    }

    public AuthDB setAuthId(Long authId) {
        this.authId = authId;
        return this;
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

        if (type != authDB.type) return false;
        if (userId != authDB.userId) return false;
        if (token != null ? !token.equals(authDB.token) : authDB.token != null) return false;
        if (authId != null ? !authId.equals(authDB.authId) : authDB.authId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (type ^ (type >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (authId != null ? authId.hashCode() : 0);
        return result;
    }

    public static AuthDB createNew(int type, String token, long userId) {
        return new AuthDB()
                .setToken(token)
                .setType((long) type)
                .setUserId(userId)
                .setAuthId(null);
    }

    @Override
    public AuthDB validate() throws RestException {
        return this;
    }
}
