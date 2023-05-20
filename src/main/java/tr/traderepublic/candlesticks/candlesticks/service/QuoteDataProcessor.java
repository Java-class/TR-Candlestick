package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tr.traderepublic.candlesticks.candlesticks.model.dto.QuoteMessage;

/**
 * Incoming Data Processor for Process Quote Message
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 0:56
 */
@Component("quoteDataProcessor")
@RequiredArgsConstructor
public class QuoteDataProcessor implements DataProcessor<QuoteMessage> {

    private final QuoteHistoryService quoteHistoryService;

    @Override
    public void processData(QuoteMessage quoteMessage) {
        quoteHistoryService.save(quoteMessage.getData().getIsin(),
                quoteMessage.getData().getPrice(), System.currentTimeMillis());
    }
}
