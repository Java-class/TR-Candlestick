package tr.traderepublic.candlesticks.candlesticks.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * Instrument Redis Hash Item for Store Instrument In redis DB
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 9:49
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@RedisHash(value = "Instrument")
@Builder
public class InstrumentHash implements Serializable {
    @Id
    private String id;
    private String description;
}
