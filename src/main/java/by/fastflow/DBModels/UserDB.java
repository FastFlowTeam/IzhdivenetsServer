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
public class UserDB extends UpdatableDB<UserDB> {
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

    public UserDB setgId(long gId) {
        this.gId = gId;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public UserDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Basic
    @Column(name = "token", nullable = false, length = 200)
    public String getToken() {
        return token;
    }

    public UserDB setToken(String token) {
        this.token = token;
        return this;
    }

    @Basic
    @Column(name = "photo", nullable = true, length = 200)
    public String getPhoto() {
        return photo;
    }

    public UserDB setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    @Basic
    @Column(name = "chat_name", nullable = true, length = 30)
    public String getChatName() {
        return chatName;
    }

    public UserDB setChatName(String chatName) {
        this.chatName = chatName;
        return this;
    }

    @Basic
    @Column(name = "type", nullable = false)
    public long getType() {
        return type;
    }

    public UserDB setType(long type) {
        this.type = type;
        return this;
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

    @Override
    public UserDB validate() throws RestException {
        if (!Constants.user_types.contains(type))
            throw new RestException(ErrorConstants.USER_TYPE);
        if ((chatName == null) || (chatName.isEmpty()) || (chatName.length() > 30))
            throw new RestException(ErrorConstants.USER_CHAT_NAME);
        if ((photo != null) && (photo.length() > 200))
            throw new RestException(ErrorConstants.LONG_USER_PHOTO);
        return this;
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        if (!this.getToken().equals(token)) {
            throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
        }
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        havePermissionToModify(session, token);
    }

    @Override
    public UserDB setNextId(Session session) {
        try {
            userId = ((UserDB) session.createQuery("from UserDB ORDER BY userId DESC").setMaxResults(1).uniqueResult()).getUserId() + 1;
        } catch (Exception e) {
            userId = 1;
        }
        return this;
    }

    public UserDB updateToken() {
        token = hashCode() + Calendar.getInstance().getTimeInMillis() + "" + userId;
        return this;
    }

    @Override
    public void updateBy(UserDB up) {
        this.chatName = up.chatName;
        this.photo = up.photo;
        updateToken();
    }

    public static UserDB createNew(String s, int type) {
        return new UserDB()
                .setChatName(s)
                .setType(type)
                .setgId(GenerateGID())
                .updateToken();
    }

    public static long GenerateGID() {
        //todo по другому генерить
        return Calendar.getInstance().getTimeInMillis();
    }

    public static UserDB getUser(Session session, long userId, String token) throws RestException {
        UserDB user = getUser(session, userId);
        if (!user.getToken().equals(token))
            throw new RestException(ErrorConstants.NOT_CORRECT_TOKEN);
        return user;
    }

    public static UserDB getUser(Session session, long userId) throws RestException {
        UserDB user = ((UserDB) session.get(UserDB.class, userId));
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
