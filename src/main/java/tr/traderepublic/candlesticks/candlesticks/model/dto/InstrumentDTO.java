package tr.traderepublic.candlesticks.candlesticks.model.dto;

import lombok.*;
import tr.traderepublic.candlesticks.candlesticks.model.enums.Type;

/**
 * Incoming Instrument Item from Websocket Producer
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-17 21:01
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InstrumentDTO {
    private String isin;
    private String description;
    private Type type;
}
