package by.fastflow.DBModels;

import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.NextableId;
import by.fastflow.utils.RestException;
import org.hibernate.Session;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "card", schema = "izh_scheme", catalog = "db")
public class CardDB extends NextableId {
    private long cardId;
    private long userId;
    private long moneyAmount;

    @Id
    @Column(name = "card_id", nullable = false)
    public long getCardId() {
        return cardId;
    }

    public CardDB setCardId(long cardId) {
        this.cardId = cardId;
        return this;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public Long getUserId() {
        return userId;
    }

    public CardDB setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    @Basic
    @Column(name = "money_amount", nullable = false)
    public long getMoneyAmount() {
        return moneyAmount;
    }

    public CardDB setMoneyAmount(long moneyAmount) {
        this.moneyAmount = moneyAmount;
        return this;
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
        if (moneyAmount < 0 || moneyAmount > 1000000000)
            throw new RestException(ErrorConstants.NEGATIVE_CARD_MONEY);
    }

    @Override
    public CardDB setNextId(Session session) {
        try {
            cardId = ((CardDB) session.createQuery("from CardDB ORDER BY cardId DESC").setMaxResults(1).uniqueResult()).getUserId() + 1;
        } catch (Exception e) {
            cardId = 1;
        }
        return this;
    }

    public static CardDB createNew(long userId, int i) {
        return new CardDB()
                .setUserId(userId)
                .setMoneyAmount(i);
    }

    public CardDB sub(long money) {
        moneyAmount -= money;
        return this;
    }

    public CardDB add(long money) {
        moneyAmount += money;
        return this;
    }
}
