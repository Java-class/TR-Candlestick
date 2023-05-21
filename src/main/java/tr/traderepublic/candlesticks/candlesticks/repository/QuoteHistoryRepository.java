package tr.traderepublic.candlesticks.candlesticks.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tr.traderepublic.candlesticks.candlesticks.model.data.QuoteHistoryHash;

import java.util.List;

/**
 * Repository Class for CRUD Operations for Quote Item
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 12:32
 */
@Repository
public interface QuoteHistoryRepository extends CrudRepository<QuoteHistoryHash, String> {
    /**
     * The findByIsinEqualsAndTimeChunkEquals method for fetch list of quote history for an instrument based on desired timeChunk
     *
     * @param isin mandatory object of Instrument identifier
     * @param timeChunk desired time chunk for fetch quote history
     * @return List of CandlestickHash Object
     */
    List<QuoteHistoryHash> findByIsinEqualsAndTimeChunkEquals(String isin, String timeChunk);
}
