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
    List<QuoteHistoryHash> findByIsinEqualsAndTimeChunkEquals(String isin, String timeChunk);
}
