package by.fastflow.DBModels.xml;

import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "wish_list", schema = "izh_scheme", catalog = "db")
public class WishListDB {
    private long listId;
    private Long userId;
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
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (listId ^ (listId >>> 32));
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (visibility ^ (visibility >>> 32));
        return result;
    }

    public void validate() throws RestException {
        if ((name == null) || ((name.isEmpty()) || (name.length() > 30)))
            throw new RestException(ErrorConstants.EMPTY_WISH_LIST_NAME);
        // TODO: 10.11.2016 переписать
        if ((visibility != Constants.WISH_ITEM_VISIBLE) && (visibility != Constants.WISH_ITEM_INVISIBLE))
            throw new RestException(ErrorConstants.WRONG_WISH_ITEM_VISIBILITY);
        if (description.length() > 200)
            throw new RestException(ErrorConstants.LONG_WISH_LIST_DESCRIPTION);
    }
}
