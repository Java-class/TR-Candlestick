package tr.traderepublic.candlesticks.candlesticks.model.dto;

import java.util.Date;

/**
 * Candlestick Response Item for Represent Candlestick History to Specific Instrument
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 20:25
 */
public record CandlestickResponseDto(String id,
                                     Date openTimestamp,
                                     double openPrice,
                                     double lowPrice,
                                     double highPrice,
                                     double closingPrice,
                                     Date closeTimestamp) {
}
