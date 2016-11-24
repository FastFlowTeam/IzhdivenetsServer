package by.fastflow.DBModels.main;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "push", schema = "izh_scheme", catalog = "db")
public class PushDB {
    private String token;
    private long device;
    private long userId;

    @Id
    @Column(name = "token", nullable = false, length = 200)
    public String getToken() {
        return token;
    }

    public PushDB setToken(String token) {
        this.token = token;
        return this;
    }

    @Basic
    @Column(name = "device", nullable = false)
    public long getDevice() {
        return device;
    }

    public PushDB setDevice(long device) {
        this.device = device;
        return this;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public long getUserId() {
        return userId;
    }

    public PushDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PushDB pushDB = (PushDB) o;

        if (device != pushDB.device) return false;
        if (token != null ? !token.equals(pushDB.token) : pushDB.token != null) return false;
        if (userId !=  pushDB.userId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (int) (device ^ (device >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }

    public static PushDB createNew(long userId, long device, String token){
        return new PushDB()
                .setUserId(userId)
                .setDevice(device)
                .setToken(token);
    }
}
