package by.fastflow.DBModels;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "push", schema = "izh_scheme", catalog = "db")
public class PushDB {
    private String token;
    private long device;
    private Long userId;

    @Id
    @Column(name = "token", nullable = false, length = 200)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Basic
    @Column(name = "device", nullable = false)
    public long getDevice() {
        return device;
    }

    public void setDevice(long device) {
        this.device = device;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PushDB pushDB = (PushDB) o;

        if (device != pushDB.device) return false;
        if (token != null ? !token.equals(pushDB.token) : pushDB.token != null) return false;
        if (userId != null ? !userId.equals(pushDB.userId) : pushDB.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (int) (device ^ (device >>> 32));
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    public void validate() throws RestException {
        if ((token == null) || ((token.isEmpty()))
        throw new RestException(ErrorConstants.EMPTY_PUSH_TOKEN);
        if(!(Constants.device_types.contains(device))
            throw new RestException(ErrorConstants.WRONG_DEVICE);
        if (token.length()>200)
            throw new RestException(ErrorConstants.LONG_TOKEN);
    }
}
