package by.fastflow.DBModels.xml;

import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "card", schema = "izh_scheme", catalog = "db")
public class CardDB {
    private long cardId;
    private long userId;
    private long moneyAmount;

    @Id
    @Column(name = "card_id", nullable = false)
    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
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
    @Column(name = "money_amount", nullable = false)
    public long getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(long moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardDB cardDB = (CardDB) o;

        if (cardId != cardDB.cardId) return false;
        if (moneyAmount != cardDB.moneyAmount) return false;
        if (userId != cardDB.userId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (cardId ^ (cardId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (moneyAmount ^ (moneyAmount >>> 32));
        return result;
    }

    public void validate() throws RestException {
        if (moneyAmount<0||moneyAmount>1000000000)
            throw new RestException(ErrorConstants.NEGATIVE_CARD_MONEY);
    }
}
