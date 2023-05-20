package tr.traderepublic.candlesticks.candlesticks.model.dto;

import lombok.*;
import tr.traderepublic.candlesticks.candlesticks.model.enums.Type;

/**
 * Incoming Instrument Message from Websocket Producer
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-17 21:02
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InstrumentMessage {
    private Type type;
    private InstrumentDTO data;
}
