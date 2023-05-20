package tr.traderepublic.candlesticks.candlesticks.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tr.traderepublic.candlesticks.candlesticks.model.data.InstrumentHash;

/**
 * Repository Class for CRUD Operations for Instrument Item
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 9:48
 */
@Repository
public interface InstrumentRepository extends CrudRepository<InstrumentHash, String> {
}
