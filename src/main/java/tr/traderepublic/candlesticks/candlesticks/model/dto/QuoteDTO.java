package tr.traderepublic.candlesticks.candlesticks.model.dto;

import lombok.*;

/**
 * Incoming Quote Item from Websocket Producer
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-17 14:36
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuoteDTO {
    private double price;
    private String isin;
}
