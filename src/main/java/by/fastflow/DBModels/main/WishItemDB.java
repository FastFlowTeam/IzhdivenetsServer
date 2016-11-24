package by.fastflow.DBModels.main;

import by.fastflow.utils.Constants;
import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import by.fastflow.utils.UpdatableDB;
import org.hibernate.Session;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "wish_item", schema = "izh_scheme", catalog = "db")
public class WishItemDB extends UpdatableDB<WishItemDB> {
    private long itemId;
    private String title;
    private String comment;
    private String link;
    private String photo;
    private long cost;
    private long wantRate;
    private long visibility;
    private long listId;

    @Id
    @Column(name = "item_id", nullable = false)
    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
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
    @Column(name = "comment", nullable = true, length = 200)
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
    @Column(name = "photo", nullable = true, length = 200)
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Basic
    @Column(name = "cost", nullable = true)
    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    @Basic
    @Column(name = "want_rate", nullable = true)
    public long getWantRate() {
        return wantRate;
    }

    public void setWantRate(long wantRate) {
        this.wantRate = wantRate;
    }

    @Basic
    @Column(name = "visibility", nullable = false)
    public long getVisibility() {
        return visibility;
    }

    public void setVisibility(long visibility) {
        this.visibility = visibility;
    }

    @Basic
    @Column(name = "list_id", nullable = true)
    public long getListId() {
        return listId;
    }

    public WishItemDB setListId(long listId) {
        this.listId = listId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WishItemDB that = (WishItemDB) o;

        if (itemId != that.itemId) return false;
        if (listId != that.listId) return false;
        if (cost != that.cost) return false;
        if (wantRate != that.wantRate) return false;
        if (visibility != that.visibility) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        if (photo != null ? !photo.equals(that.photo) : that.photo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (itemId ^ (itemId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (int) (visibility ^ (visibility >>> 32));
        result = 31 * result + (int) (listId ^ (listId >>> 32));
        result = 31 * result + (int) (wantRate ^ (cost >>> 32));
        result = 31 * result + (int) (cost ^ (wantRate >>> 32));
        return result;
    }

    @Override
    public WishItemDB validate() throws RestException {
        if ((title == null) || ((title.isEmpty()) || (title.length() > 30)))
            throw new RestException(ErrorConstants.EMPTY_WISH_ITEM_TITLE);
        if (!Constants.wishItem_visibility.contains(visibility))
            throw new RestException(ErrorConstants.WRONG_WISH_ITEM_VISIBILITY);
        if ((cost < 0) || (cost > 1000000000))
            throw new RestException(ErrorConstants.WRONG_WISH_COST);
        if (!(Constants.wish_rates.contains(wantRate)))
            throw new RestException(ErrorConstants.WRONG_WANT_RATE);
        if ((comment!= null) && (comment.length() > 200))
            throw new RestException(ErrorConstants.LONG_WISH_COMMENT);
        if ((link!= null) && (link.length() > 200))
            throw new RestException(ErrorConstants.LONG_WISH_LINK);
        if ((photo!= null) && (photo.length() > 200))
            throw new RestException(ErrorConstants.LONG_WISH_PHOTO);
        return this;
    }

    @Override
    public void updateBy(WishItemDB up) {
        title = up.title;
        comment = up.comment;
        link = up.link;
        photo = up.photo;
        cost = up.cost;
        wantRate = up.wantRate;
        visibility = up.visibility;
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        UserDB.getUser(session, WishListDB.getWishList(session, listId).getUserId(), token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        UserDB.getUser(session, WishListDB.getWishList(session, listId).getUserId(), token);
    }

    public static WishItemDB getWishItem(Session session, long wishItemId) throws RestException {
        WishItemDB wishItemDB = ((WishItemDB) session.get(WishItemDB.class, wishItemId));
        if (wishItemDB == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return wishItemDB;
    }

    @Override
    public WishItemDB setNextId(Session session) {
        try {
            itemId = ((WishItemDB) session.createQuery("from WishItemDB ORDER BY itemId DESC").setMaxResults(1).uniqueResult()).getItemId() + 1;
        } catch (Exception e) {
            itemId = 1;
        }
        return this;
    }
}
