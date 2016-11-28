package by.fastflow.DBModels.main;

import by.fastflow.utils.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hibernate.Session;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "message", schema = "izh_scheme", catalog = "db")
public class MessageDB extends UpdatableDB<MessageDB> {
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

    public MessageDB setMessageId(long messageId) {
        this.messageId = messageId;
        return this;
    }

    @Basic
    @Column(name = "dialog_id", nullable = true)
    public long getDialogId() {
        return dialogId;
    }

    public MessageDB setDialogId(long dialogId) {
        this.dialogId = dialogId;
        return this;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public long getUserId() {
        return userId;
    }

    public MessageDB setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    @Basic
    @Column(name = "date", nullable = false)
    public long getDate() {
        return date;
    }

    public MessageDB setDate(long date) {
        this.date = date;
        return this;
    }

    @Basic
    @Column(name = "text", nullable = false, length = 500)
    public String getText() {
        return text;
    }

    public MessageDB setText(String text) {
        this.text = text;
        return this;
    }

    @Basic
    @Column(name = "type", nullable = false)
    public long getType() {
        return type;
    }

    public MessageDB setType(long type) {
        this.type = type;
        return this;
    }

    @Basic
    @Column(name = "link", nullable = true, length = 200)
    public String getLink() {
        return link;
    }

    public MessageDB setLink(String link) {
        this.link = link;
        return this;
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

    @Override
    public MessageDB validate() throws RestException {
        if ((text == null) || (text.isEmpty()))
            throw new RestException(ErrorConstants.EMPTY_MESSAGE);
        if (text.length() > 500)
            throw new RestException(ErrorConstants.TOO_LONG_MESSAGE);
        if (Constants.message_types.contains(type))
            throw new RestException(ErrorConstants.MESSAGE_TYPE);
        if ((link != null) && (link.length() > 200))
            throw new RestException(ErrorConstants.LONG_MESSAGE_LINK);
        return this;
    }

    @Override
    public void havePermissionToModify(Session session, String token) throws RestException {
        if (type != Constants.MESSAGE_TYPE_USER)
            throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
        UserDB.getUser(session, userId, token);
    }

    @Override
    public void havePermissionToDelete(Session session, String token) throws RestException {
        throw new RestException(ErrorConstants.NOT_NAVE_PERMISSION);
    }

    @Override
    public MessageDB setNextId(Session session) {
        date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        try {
            messageId = ((MessageDB) session.createQuery("from MessageDB ORDER BY messageId DESC").setMaxResults(1).uniqueResult()).getMessageId() + 1;
        } catch (Exception e) {
            messageId = 1;
        }
        return this;
    }

    public static MessageDB createNew(int msg_type, long userId, long dialogId, LIST list) {
        return new MessageDB()
                .setDialogId(dialogId)
                .setType(Constants.MESSAGE_TYPE_SYSTEM)
                .setText(Constants.getMSG(msg_type, list))
                .setUserId(userId)
                .setDialogId(dialogId)
                .setLink("")
                .setDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
    }

    public static JsonElement getJson(BigInteger date, BigInteger type, BigInteger messageId, String text, String link) {
        JsonObject json = new JsonObject();
        json.addProperty("date", date);
        json.addProperty("type", type);
        json.addProperty("messageId", messageId);
        json.addProperty("text", text);
        json.addProperty("link", link);
        return json;
    }

    public static MessageDB getMessage(Session session, long msgId) throws RestException {
        MessageDB messageDB = ((MessageDB) session.get(MessageDB.class, msgId));
        if (messageDB == null)
            throw new RestException(ErrorConstants.NOT_HAVE_ID);
        return messageDB;
    }
}
