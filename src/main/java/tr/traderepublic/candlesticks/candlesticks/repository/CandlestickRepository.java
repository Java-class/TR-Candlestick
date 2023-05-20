package tr.traderepublic.candlesticks.candlesticks.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tr.traderepublic.candlesticks.candlesticks.model.data.CandlestickHash;

import java.util.List;

/**
 * Repository Class for CRUD Operations for CandlestickHash Item
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 18:11
 */
@Repository
public interface CandlestickRepository extends CrudRepository<CandlestickHash, String> {

    List<CandlestickHash> findByIsinEquals(String isin);

    void deleteAllByIsinEquals(String isin);
}
