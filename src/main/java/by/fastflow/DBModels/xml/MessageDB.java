package by.fastflow.DBModels.xml;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "message", schema = "izh_scheme", catalog = "db")
public class MessageDB {
    private long messageId;
    private Long dialogId;
    private Long userId;
    private Timestamp date;
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
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
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
        if (dialogId != null ? !dialogId.equals(messageDB.dialogId) : messageDB.dialogId != null) return false;
        if (userId != null ? !userId.equals(messageDB.userId) : messageDB.userId != null) return false;
        if (date != null ? !date.equals(messageDB.date) : messageDB.date != null) return false;
        if (text != null ? !text.equals(messageDB.text) : messageDB.text != null) return false;
        if (link != null ? !link.equals(messageDB.link) : messageDB.link != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (messageId ^ (messageId >>> 32));
        result = 31 * result + (dialogId != null ? dialogId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (int) (type ^ (type >>> 32));
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }
//    public void validate() throws RestException {
//        Date d = new Date(date.);
//        Calendar Current_Calendar = Calendar.getInstance();
//        Date Current_Date = Current_Calendar.getTime();
//        if (date.d>Current_Date)
//            throw new RestException(ErrorConstants.MESSAGE_DATE_CONFLICTS);
//        if ((text==null)||(text.isEmpty()))
//            throw new RestException(ErrorConstants.EMPTY_MESSAGE);
//        if(text.length()>500)
//            throw new RestException(ErrorConstants.TOO_LONG_MESSAGE);
//        if((type!=Constants.MESSAGE_TYPE_SYSTEM)&&(type!=MESSAGE_TYPE_USER))
//            throw new RestException(ErrorConstants.MESSAGE_TYPE);
//        if (link.length()>200)
//            throw new RestException(ErrorConstants.LONG_MESSAGE_LINK);
//    }
}
