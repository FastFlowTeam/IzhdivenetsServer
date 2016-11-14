package by.fastflow.DBModels;

import by.fastflow.utils.*;
import org.hibernate.Session;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "message", schema = "izh_scheme", catalog = "db")
public class MessageDB extends UpdatableDB<MessageDB> implements NextableId {
    private long messageId;
    private long dialogId;
    private long userId;
    private long date;
    private String text;
    private long type;
    private String link;

    @Id
    @Column(name = "message_id", nullable = false)
    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Basic
    @Column(name = "dialog_id", nullable = true)
    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
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
    @Column(name = "date", nullable = false)
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Basic
    @Column(name = "text", nullable = false, length = 500)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "type", nullable = false)
    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    @Basic
    @Column(name = "link", nullable = false, length = 200)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageDB messageDB = (MessageDB) o;

        if (messageId != messageDB.messageId) return false;
        if (type != messageDB.type) return false;
        if (dialogId != messageDB.dialogId) return false;
        if (userId != messageDB.userId) return false;
        if (date != messageDB.date) return false;
        if (text != null ? !text.equals(messageDB.text) : messageDB.text != null) return false;
        if (link != null ? !link.equals(messageDB.link) : messageDB.link != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (messageId ^ (messageId >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (type ^ (type >>> 32));
        result = 31 * result + (int) (dialogId ^ (dialogId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }

    @Override
    public void updateBy(MessageDB up) {

    }

    public void validate() throws RestException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        if (date > calendar.getTimeInMillis())
            throw new RestException(ErrorConstants.MESSAGE_DATE_CONFLICTS);
        if ((text == null) || (text.isEmpty()))
            throw new RestException(ErrorConstants.EMPTY_MESSAGE);
        if (text.length() > 500)
            throw new RestException(ErrorConstants.TOO_LONG_MESSAGE);
        if (Constants.message_types.contains(type))
            throw new RestException(ErrorConstants.MESSAGE_TYPE);
        if ((link != null) && (link.length() > 200))
            throw new RestException(ErrorConstants.LONG_MESSAGE_LINK);
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        throw new RestException(ErrorConstants.PERMISSION_BY_TOKEN);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        throw new RestException(ErrorConstants.PERMISSION_BY_TOKEN);
    }

    @Override
    public void setNextId(Session session) {
        try {
            messageId = ((MessageDB) session.createQuery("from MessageDB ORDER BY messageId DESC").setMaxResults(1).uniqueResult()).getMessageId() + 1;
        } catch (Exception e) {
            messageId = 1;
        }
    }

    public static MessageDB createNew(int msg_type, long userId, long dialogId, String name) {
        MessageDB messageDB = new MessageDB();
        messageDB.dialogId = dialogId;
        messageDB.userId = userId;
        messageDB.date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        messageDB.text = Constants.getMSG(msg_type, name);
        messageDB.type = Constants.MESSAGE_TYPE_SYSTEM;
        return messageDB;
    }
}
