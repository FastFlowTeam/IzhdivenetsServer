package by.fastflow.DBModels.main;

import by.fastflow.utils.*;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "user", schema = "izh_scheme", catalog = "db")
public class UserDB extends UpdatableDB<UserDB> {

    // TODO: 28.11.2016 добавить счетчик подтвержденных задач

    @Id
    @GenericGenerator(name="kaugen", strategy = "increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

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

    public Long getUserId() {
        return userId;
    }

    public UserDB setUserId(Long userId) {
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
        this.chatName = chatName.length()>30 ? chatName.substring(0,30) : chatName;
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

        if (type != userDB.type) return false;
        if (gId != userDB.gId) return false;
        if (token != null ? !token.equals(userDB.token) : userDB.token != null) return false;
        if (photo != null ? !photo.equals(userDB.photo) : userDB.photo != null) return false;
        if (userId != null ? !userId.equals(userDB.userId) : userDB.userId != null) return false;
        if (chatName != null ? !chatName.equals(userDB.chatName) : userDB.chatName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (type ^ (type >>> 32));
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (chatName != null ? chatName.hashCode() : 0);
        result = 31 * result + (userId!= null ? userId.hashCode() : 0);
        result = 31 * result + (int) (gId ^ (gId >>> 32));
        return result;
    }

    @Override
    public UserDB validate() throws RestException {
//        if (!Constants.contains(Constants.user_types,type))
//            throw new RestException(ErrorConstants.USER_TYPE);
        if ((chatName == null) || (chatName.isEmpty()) || (chatName.length() > 30))
            throw new RestException(ErrorConstants.USER_CHAT_NAME);
        if ((photo != null) && (photo.length() > 200))
            throw new RestException(ErrorConstants.LONG_USER_PHOTO);
        if (photo == null)
            photo = "";
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

    public UserDB updateToken() {
        token = hashCode() + Calendar.getInstance().getTimeInMillis() + "" + userId;
        return this;
    }

    @Override
    public void updateBy(UserDB up) {
        this.chatName = up.chatName;
        this.photo = up.photo;
        if (photo == null)
            photo = "";
        updateToken();
    }

    public static UserDB createNew(String s, int type) {
        return new UserDB()
                .setChatName(s)
                .setType(type)
                .setgId(GenerateGID())
                .updateToken()
                .setUserId(null);
    }

    public static long GenerateGID() {
        //1479645071000 - 12  символов
        String s = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()+"";
        String res = "";
        res += s.charAt(2);
        res += s.charAt(5);
        res += s.charAt(9);
        res += s.charAt(6);
        res += s.charAt(3);
        res += s.charAt(7);
        res += s.charAt(10);
        res += s.charAt(11);
        res += s.charAt(1);
        res += s.charAt(4);
        res += s.charAt(8);
        res += s.charAt(0);
        return Long.getLong(res);
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

    public static JsonObject getJson(UserDB userDB) {
        JsonObject json = new JsonObject();
        json.addProperty("chatName", userDB.chatName);
        json.addProperty("type", userDB.type);
        json.addProperty("photo", userDB.photo);
        json.addProperty("gId", userDB.gId);
        return json;
    }

    public boolean isChild() {
        return type == Constants.USER_CHILD;
    }

    public boolean isParent() {
        return type == Constants.USER_PARENT;
    }
}
