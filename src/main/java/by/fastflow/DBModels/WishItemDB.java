package by.fastflow.DBModels;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "wish_item", schema = "izh_scheme", catalog = "db")
public class WishItemDB {
    private long itemId;
    private String title;
    private String comment;
    private String link;
    private String photo;
    private Long cost;
    private Long wantRate;
    private long visibility;
    private Long listId;

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
    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    @Basic
    @Column(name = "want_rate", nullable = true)
    public Long getWantRate() {
        return wantRate;
    }

    public void setWantRate(Long wantRate) {
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
    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WishItemDB that = (WishItemDB) o;

        if (itemId != that.itemId) return false;
        if (visibility != that.visibility) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        if (photo != null ? !photo.equals(that.photo) : that.photo != null) return false;
        if (cost != null ? !cost.equals(that.cost) : that.cost != null) return false;
        if (wantRate != null ? !wantRate.equals(that.wantRate) : that.wantRate != null) return false;
        if (listId != null ? !listId.equals(that.listId) : that.listId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (itemId ^ (itemId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (wantRate != null ? wantRate.hashCode() : 0);
        result = 31 * result + (int) (visibility ^ (visibility >>> 32));
        result = 31 * result + (listId != null ? listId.hashCode() : 0);
        return result;
    }
}
