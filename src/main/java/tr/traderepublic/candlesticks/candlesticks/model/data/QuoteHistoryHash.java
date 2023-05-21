package tr.traderepublic.candlesticks.candlesticks.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * Quote Redis Hash Item for Store Quote In redis DB
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 12:31
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@RedisHash(value = "QuoteHistory", timeToLive = 10 * 60)
@Builder
public class QuoteHistoryHash implements Serializable {
    @Id
    private String id;
    @Indexed
    private String isin;
    private double price;
    /**
     * The time of received quote
     */
    private long receivedDate;
    /**
     * The time chunk of received quote
     */
    @Indexed
    private String timeChunk;
}
