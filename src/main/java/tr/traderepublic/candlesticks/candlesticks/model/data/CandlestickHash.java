package tr.traderepublic.candlesticks.candlesticks.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;

/**
 * Candlestick Redis Hash Item for Store Candlestick In redis DB
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 11:40
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@RedisHash(value = "Candlestick", timeToLive = 30 * 60)
@Builder
@AllArgsConstructor
public class CandlestickHash implements Serializable {
    @Id
    private String id;
    @Indexed
    private String isin;
    @Indexed
    private String timeChunk;
    private Date openTimestamp;
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private Date closeTimestamp;
    private long computeTimestamp;
}
