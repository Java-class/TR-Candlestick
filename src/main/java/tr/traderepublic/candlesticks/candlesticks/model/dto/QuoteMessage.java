package tr.traderepublic.candlesticks.candlesticks.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.traderepublic.candlesticks.candlesticks.model.enums.Type;

/**
 * Incoming Quote Message from Websocket Producer
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 14:29
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuoteMessage {
    private Type type;
    private QuoteDTO data;
}
