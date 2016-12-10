package by.fastflow.DBModels.main;

import by.fastflow.utils.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "success", schema = "izh_scheme", catalog = "db")
public class SuccessDB extends UpdatableDB<SuccessDB> {
    private Long successId;
    private long userId;
    private String title;
    private String description;
    private String photo;
    private String link;
    private long state;

    @Id
    @Column(name = "success_id", nullable = false)
    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getSuccessId() {
        return successId;
    }

    public SuccessDB setSuccessId(Long successId) {
        this.successId = successId;
        return this;
    }

    @Basic
    @Column(name = "user_id", nullable = false)
    public long getUserId() {
        return userId;
    }

    public SuccessDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Basic
    @Column(name = "title", nullable = false, length = 30)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    @Column(name = "link", nullable = true, length = 200)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Basic
    @Column(name = "state", nullable = false)
    public long getState() {
        return state;
    }

    public SuccessDB setState(long state) {
        this.state = state;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuccessDB successDB = (SuccessDB) o;

        if (successId != successDB.successId) return false;
        if (userId != successDB.userId) return false;
        if (state != successDB.state) return false;
        if (title != null ? !title.equals(successDB.title) : successDB.title != null) return false;
        if (description != null ? !description.equals(successDB.description) : successDB.description != null)
            return false;
        if (photo != null ? !photo.equals(successDB.photo) : successDB.photo != null) return false;
        if (link != null ? !link.equals(successDB.link) : successDB.link != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (successId != null ? successId.hashCode() : 0);
        result = 31 * result + (int) (state ^ (state >>> 32));
        return result;
    }

    @Override
    public void updateBy(SuccessDB up) {
        title = up.title;
        description = up.description;
        photo = up.photo;
        link = up.link;
        if (photo == null)
            photo = "";
        if (link == null)
            link = "";
        if (description == null)
            description = "";
    }

    @Override
    public SuccessDB validate() throws RestException {
        if ((title == null) || (title.isEmpty()) || (title.length() > 30))
            throw new RestException(ErrorConstants.EMPTY_SUCCESS_TITLE);
        if (!Constants.contains(Constants.success_types, state))
            throw new RestException(ErrorConstants.WRONG_SUCCESS_STATE);
        if ((photo != null) && photo.length() > 200)
            throw new RestException(ErrorConstants.LONG_SUCCESS_PHOTO);
        if ((description != null) && description.length() > 200)
            throw new RestException(ErrorConstants.LONG_SUCCESS_DESCRIPTION);
        if ((link != null) && link.length() > 200)
            throw new RestException(ErrorConstants.LONG_SUCCESS_LINK);
        if (photo == null)
            photo = "";
        if (link == null)
            link = "";
        if (description == null)
            description = "";
        return this;
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        UserDB.getUser(session, userId, token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        UserDB.getUser(session, userId, token);
    }

    public static SuccessDB getSuccess(Session session, long successId) throws RestException {
        SuccessDB success = ((SuccessDB) session.get(SuccessDB.class, successId));
        if (success == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return success;
    }

    public static JsonObject getJson(SuccessDB s) {
        JsonObject object = new JsonObject();
        object.addProperty("successId", s.successId);
        object.addProperty("title", s.title);
        object.addProperty("description", s.description);
        object.addProperty("photo", s.photo);
        object.addProperty("link", s.link);
        object.addProperty("state", s.state);
        return object;
    }

    public static JsonObject getJson(BigInteger successId, String title, String description, String photo, String link, BigInteger state) {
        JsonObject object = new JsonObject();
        object.addProperty("successId", successId);
        object.addProperty("title", title);
        object.addProperty("description", description);
        object.addProperty("photo", photo);
        object.addProperty("link", link);
        object.addProperty("state", state);
        return object;
    }

    public boolean notRead() {
        return state == Constants.SUCCESS_NOT_READED;
    }

    public void read() {
        state = Constants.SUCCESS_READED;
    }

    public SuccessDB praised() {
        state = Constants.SUCCESS_PRAISED;
        return this;
    }

    public JsonElement makeJson() {
        return getJson(BigInteger.valueOf(successId),
                title,
                description,
                photo,
                link,
                BigInteger.valueOf(state));
    }
}
