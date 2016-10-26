package by.fastflow.DBModels;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "auth", schema = "izh_scheme", catalog = "db")
public class AuthDB {
    private long authId;
    private Long type;
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
    @Column(name = "type", nullable = true)
    public Long getType() {
        return type;
    }

    public void setType(Long type) {
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
        if (type != null ? !type.equals(authDB.type) : authDB.type != null) return false;
        if (token != null ? !token.equals(authDB.token) : authDB.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (authId ^ (authId >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
