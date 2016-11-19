package by.fastflow.DBModels;

import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import by.fastflow.utils.UpdatableDB;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hibernate.Session;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "wish_list", schema = "izh_scheme", catalog = "db")
public class WishListDB extends UpdatableDB<WishListDB>{
    private long listId;
    private long userId;
    private String name;
    private String description;
    private long visibility;

    @Id
    @Column(name = "list_id", nullable = false)
    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public long getUserId() {
        return userId;
    }

    public WishListDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 30)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Column(name = "visibility", nullable = false)
    public long getVisibility() {
        return visibility;
    }

    public void setVisibility(long visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WishListDB that = (WishListDB) o;

        if (listId != that.listId) return false;
        if (visibility != that.visibility) return false;
        if (userId != that.userId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (listId ^ (listId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (visibility ^ (visibility >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }

    @Override
    public void updateBy(WishListDB up) {
        name = up.name;
        description = up.description;
        visibility = up.visibility;
    }

    @Override
    public WishListDB validate() throws RestException {
        if ((name == null) || ((name.isEmpty()) || (name.length() > 30)))
            throw new RestException(ErrorConstants.EMPTY_WISH_LIST_NAME);
        if (!Constants.wishList_visibility.contains(visibility))
            throw new RestException(ErrorConstants.WRONG_WISH_ITEM_VISIBILITY);
        if ((description!= null) && (description.length() > 200))
            throw new RestException(ErrorConstants.LONG_WISH_LIST_DESCRIPTION);
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

    @Override
    public WishListDB setNextId(Session session) {
        try {
            listId = ((WishListDB) session.createQuery("from WishListDB ORDER BY listId DESC").setMaxResults(1).uniqueResult()).getListId() + 1;
        } catch (Exception e) {
            listId = 1;
        }
        return this;
    }

    public static WishListDB getWishList(Session session, long wishListId) throws RestException {
        WishListDB wishListDB = ((WishListDB) session.get(WishListDB.class, wishListId));
        if (wishListDB == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return wishListDB;
    }

    public JsonElement makeJson() {
        JsonObject object = new JsonObject();
        object.addProperty("listId", listId);
        object.addProperty("name", name);
        object.addProperty("description", description);
        object.addProperty("visibility", visibility);
        return object;
    }
}
