package by.fastflow.DBModels;

import by.fastflow.utils.*;
import com.google.gson.JsonObject;
import org.hibernate.Session;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Calendar;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "user", schema = "izh_scheme", catalog = "db")
public class UserDB extends UpdatableDB<UserDB> implements NextableId {
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


    public long getgId() {
        return gId;
    }

    public void setgId(long gId) {
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
        if (!Constants.user_types.contains(type))
            throw new RestException(ErrorConstants.USER_TYPE);
        if ((chatName == null) || (chatName.isEmpty())||(chatName.length()>30))
            throw new RestException(ErrorConstants.USER_CHAT_NAME);
        if ((photo != null) && (photo.length()>200))
            throw new RestException(ErrorConstants.LONG_USER_PHOTO);
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        if (!this.getToken().equals(token)) {
            throw new RestException(ErrorConstants.PERMISSION_BY_TOKEN);
        }
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        havePermissionToModify(session, token);
    }

    @Override
    public void setNextId(Session session) {
        userId = ((UserDB) session.createQuery("from UserDB ORDER BY userId DESC").setMaxResults(1).uniqueResult()).getUserId() + 1;
    }

    public void updateToken() {
        token = hashCode() + Calendar.getInstance().getTimeInMillis() + "" + userId;
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
        userDB.gId = Calendar.getInstance().getTimeInMillis();
        //todo по другому генерить
        userDB.updateToken();
        return userDB;
    }

    public static UserDB getUser(Session session, Long userId, String token) throws RestException {
        UserDB user = getUser(session, userId);
        if (!user.getToken().equals(token))
            throw new RestException(ErrorConstants.NOT_CORRECT_TOKEN);
        return user;
    }

    public static UserDB getUser(Session session, long userId) throws RestException {
        UserDB user = ((UserDB) session.load(UserDB.class, userId));
        if (user == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return user;
    }

    public static JsonObject getJson(String chatName, BigInteger type, String photo, BigInteger gId) {
        JsonObject json = new JsonObject();
        json.addProperty("chatName", chatName);
        json.addProperty("type", type);
        json.addProperty("photo", photo);
        json.addProperty("gId", gId);
        return json;
    }

    public boolean isChild() {
        return type == Constants.USER_CHILD;
    }

    public boolean isParent() {
        return type == Constants.USER_PARENT;
    }
}
