package by.fastflow.DBModels;

import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "user", schema = "izh_scheme", catalog = "db")
public class UserDB {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false, unique = true)
    private long userId;

    private String token;
    private String photo;
    private String chatName;
    private long type;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "token", nullable = false, length = 200)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Basic
    @Column(name = "photo", nullable = true, length = 200)
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Basic
    @Column(name = "chat_name", nullable = true, length = 30)
    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    @Basic
    @Column(name = "type", nullable = false)
    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDB userDB = (UserDB) o;

        if (userId != userDB.userId) return false;
        if (type != userDB.type) return false;
        if (token != null ? !token.equals(userDB.token) : userDB.token != null) return false;
        if (photo != null ? !photo.equals(userDB.photo) : userDB.photo != null) return false;
        if (chatName != null ? !chatName.equals(userDB.chatName) : userDB.chatName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (chatName != null ? chatName.hashCode() : 0);
        result = 31 * result + (int) (type ^ (type >>> 32));
        return result;
    }

    public void validate() throws RestException {
        if ((type != Constants.USER_CHILD)&&(type != Constants.USER_PARENT))
            throw new RestException("Not having user type", ErrorConstants.USER_TYPE);
        if ((chatName == null)||(chatName.isEmpty()))
            throw new RestException("Not having chat name", ErrorConstants.USER_CHAT_NAME);
    }

    public void setUserNextId(Session session) {
        userId = ((UserDB)session.createQuery("from UserDB ORDER BY userId DESC").setMaxResults(1).uniqueResult()).getUserId()+1;
    }

    public void updateToken(){
        token = hashCode()+Calendar.getInstance().getTimeInMillis()+""+userId;
    }
}
