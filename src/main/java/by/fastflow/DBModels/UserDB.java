package by.fastflow.DBModels;

import by.fastflow.utils.*;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "user", schema = "izh_scheme", catalog = "db")
public class UserDB extends UpdatableDB<UserDB> implements NextableId{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false, unique = true)
    private long userId;

    private String token;
    private String photo;
    private String chatName;
    private long type;

    @Basic
    @Column(name = "g_id", nullable = false)
    private long gId;


    public long getGId() {
        return gId;
    }

    public void setGId(long gId) {
        this.gId = gId;
    }

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
        if (gId != userDB.gId) return false;
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
        result = 31 * result + (int) (gId ^ (gId >>> 32));
        return result;
    }

    public void validate() throws RestException {
        if ((type != Constants.USER_CHILD)&&(type != Constants.USER_PARENT))
            throw new RestException("Not having user type", ErrorConstants.USER_TYPE);
        if ((chatName == null)||(chatName.isEmpty()))
            throw new RestException("Not having chat name", ErrorConstants.USER_CHAT_NAME);
    }

    @Override
    public boolean havePermissionToModify(Session session, String token) {
        return this.token.equals(token);
    }

    @Override
    public UserDB anonimize() {
        this.token = null;
        this.gId = -1;
        this.token = "";
        return null;
    }

    @Override
    public void setNextId(Session session) {
        userId = ((UserDB)session.createQuery("from UserDB ORDER BY userId DESC").setMaxResults(1).uniqueResult()).getUserId()+1;
    }

    public void updateToken(){
        token = hashCode()+Calendar.getInstance().getTimeInMillis()+""+userId;
        gId = Calendar.getInstance().getTimeInMillis();
        //todo по другому генерить
    }

    @Override
    public void updateBy(UserDB up) {
        this.chatName = up.chatName;
        this.photo = up.photo;
        updateToken();
    }

    public static UserDB createNew(Session session, String s, int type) {
        UserDB userDB = new UserDB();
        userDB.setNextId(session);
        userDB.setChatName(s);
        userDB.setType(type);
        userDB.updateToken();
        return userDB;
    }
}
