package by.fastflow.DBModels;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "card", schema = "izh_scheme", catalog = "db")
public class CardDB {
    private long cardId;
    private Long userId;
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
        if (userId != null ? !userId.equals(cardDB.userId) : cardDB.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (cardId ^ (cardId >>> 32));
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (int) (moneyAmount ^ (moneyAmount >>> 32));
        return result;
    }
}
