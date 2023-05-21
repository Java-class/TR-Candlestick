package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tr.traderepublic.candlesticks.candlesticks.model.data.QuoteHistoryHash;
import tr.traderepublic.candlesticks.candlesticks.repository.QuoteHistoryRepository;
import tr.traderepublic.candlesticks.candlesticks.util.DateUtil;

import java.util.UUID;

/**
 * Quote History Service Class for Manage Incoming Quote Item
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 11:44
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuoteHistoryService {

    private final QuoteHistoryRepository quoteHistoryRepository;

    /**
     * The save method for store new quote
     *
     * @param isin         mandatory input for instrument's identifier
     * @param price        double price of quote
     * @param receivedTime long timestamp received quote
     * @return QuoteHistoryHash saved quote item
     */
    public QuoteHistoryHash save(String isin, double price, long receivedTime) {
        QuoteHistoryHash quoteHistoryHash = QuoteHistoryHash.builder()
                .id(UUID.randomUUID().toString())
                .isin(isin)
                .price(price)
                .receivedDate(receivedTime)
                .timeChunk(DateUtil.getTimeChunk(receivedTime))
                .build();
        return quoteHistoryRepository.save(quoteHistoryHash);
    }
}
