package by.fastflow.DBModels;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "success", schema = "izh_scheme", catalog = "db")
public class SuccessDB {
    private long successId;
    private long userId;
    private String title;
    private String description;
    private String photo;
    private String link;
    private long state;

    @Id
    @Column(name = "success_id", nullable = false)
    public long getSuccessId() {
        return successId;
    }

    public void setSuccessId(long successId) {
        this.successId = successId;
    }

    @Basic
    @Column(name = "user_id", nullable = false)
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public void setState(long state) {
        this.state = state;
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
        int result = (int) (successId ^ (successId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (int) (state ^ (state >>> 32));
        return result;
    }
}
