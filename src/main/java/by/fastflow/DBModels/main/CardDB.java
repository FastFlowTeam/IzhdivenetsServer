package by.fastflow.DBModels.main;

import by.fastflow.utils.ErrorConstants;
import by.fastflow.utils.RestException;
import by.fastflow.utils.Validatable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by KuSu on 22.10.2016.
 */
@Entity
@Table(name = "card", schema = "izh_scheme", catalog = "db")
public class CardDB extends Validatable<CardDB> {
    private Long cardId;
    private long userId;
    private long moneyAmount;

    @Id
    @Column(name = "card_id", nullable = false)
    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getCardId() {
        return cardId;
    }

    public CardDB setCardId(Long cardId) {
        this.cardId = cardId;
        return this;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public long getUserId() {
        return userId;
    }

    public CardDB setUserId(long userId) {
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

        if (moneyAmount != cardDB.moneyAmount) return false;
        if (userId != cardDB.userId) return false;
        if (cardId != null ? !cardId.equals(cardDB.cardId) : cardDB.cardId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (moneyAmount ^ (moneyAmount >>> 32));
        result = 31 * result + (cardId != null ? cardId.hashCode() : 0);
        return result;
    }

    @Override
    public CardDB validate() throws RestException {
        if (moneyAmount < 0 || moneyAmount > 1000000000)
            throw new RestException(ErrorConstants.NEGATIVE_CARD_MONEY);
        return this;
    }

    public static CardDB createNew(long userId, int i) {
        return new CardDB()
                .setUserId(userId)
                .setMoneyAmount(i)
                .setCardId(null);
    }

    public CardDB sub(long money) {
        moneyAmount -= money;
        return this;
    }

    public CardDB add(long money) {
        moneyAmount += money;
        return this;
    }

    public JsonElement makeJson() {
        JsonObject object = new JsonObject();
        object.addProperty("cardId", cardId);
        object.addProperty("moneyAmount", moneyAmount);
        return object;
    }
}
