package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import tr.traderepublic.candlesticks.candlesticks.consts.ConstantConfig;
import tr.traderepublic.candlesticks.candlesticks.model.data.CandlestickHash;
import tr.traderepublic.candlesticks.candlesticks.model.data.InstrumentHash;
import tr.traderepublic.candlesticks.candlesticks.model.data.QuoteHistoryHash;
import tr.traderepublic.candlesticks.candlesticks.repository.CandlestickRepository;
import tr.traderepublic.candlesticks.candlesticks.repository.InstrumentRepository;
import tr.traderepublic.candlesticks.candlesticks.repository.QuoteHistoryRepository;
import tr.traderepublic.candlesticks.candlesticks.util.DateUtil;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Candlestick Service Class for Compute and Manage Candlestick History for Instrument
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 12:45
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class CandlestickService {

    private final InstrumentRepository instrumentRepository;

    private final QuoteHistoryRepository quoteHistoryRepository;

    private final CandlestickRepository candlestickRepository;

    /**
     * The time chunk duration load from config file
     */
    @Value("${service.candlestick.time.chunk.duration}")
    private long timeChunkDuration;


    /**
     * The computeCandlestickJob for compute previous time chunk
     * We Call computeCandlestick for each valid instrument
     */
    @Scheduled(fixedRateString = "${service.candlestick.schedule.time.rate}")
    public void computeCandlestickJob() {
        try {
            log.info("start to compute candlestick data...");
            StopWatch watch = new StopWatch();
            watch.start();
            long minuteBefore = System.currentTimeMillis() - (timeChunkDuration);
            String timeChunk = DateUtil.getTimeChunk(minuteBefore);
            Iterable<InstrumentHash> instrumentList = instrumentRepository.findAll();
            instrumentList.forEach(instrument -> {
                boolean result = computeCandlestick(instrument, timeChunk);
                log.info("candlestick history computed for instrument:{}, result:{}", instrument.getId(), result);
            });
            watch.stop();
            log.info("all candlestick data process finished successfully, duration in milliseconds:{}",
                    watch.getTotalTimeMillis());
        } catch (Exception ex) {
            log.error("exception happened during process candlestick data.");
        }
    }


    /**
     * The computeCandlestick method for compute candlestick input instrument
     * We Call the saveCandlestickHistory method after successfully candlestick computed
     *
     * @param instrument mandatory object of instrument
     * @param timeChunk  current time chunk for computing candlestick
     * @return boolad true if successfully candlestick computed
     */
    protected boolean computeCandlestick(InstrumentHash instrument, String timeChunk) {
        boolean result = true;
        try {
            List<QuoteHistoryHash> quoteHistoryHashList = quoteHistoryRepository.findByIsinEqualsAndTimeChunkEquals(instrument.getId(), timeChunk);
            QuoteHistoryHash openPrice = computeOpenPrice(quoteHistoryHashList);

            QuoteHistoryHash closePrice = computeClosePrice(quoteHistoryHashList);

            QuoteHistoryHash lowPrice = computeLowPrice(quoteHistoryHashList);

            QuoteHistoryHash highPrice = computeHighPrice(quoteHistoryHashList);

            saveCandlestickHistory(instrument, timeChunk, openPrice, closePrice, lowPrice, highPrice);
        } catch (Exception ex) {
            log.error("exception happened during compute candlestick for instrument:{}", instrument.getId(), ex);
            result = false;
        }
        return result;
    }

    /**
     * The computeCandlestick method for compute candlestick input instrument
     * We Call the listenQuotes method again for accept new quote
     *
     * @param instrument mandatory object of instrument
     * @param timeChunk  current time chunk for computing candlestick
     * @return boolean true if successfully candlestick computed
     */
    protected void saveCandlestickHistory(InstrumentHash instrument, String timeChunk, QuoteHistoryHash openPrice, QuoteHistoryHash closePrice, QuoteHistoryHash lowPrice, QuoteHistoryHash highPrice) {
        CandlestickHash computedCandlestick = CandlestickHash.builder().id(UUID.randomUUID().toString()).timeChunk(timeChunk).computeTimestamp(System.currentTimeMillis()).isin(instrument.getId()).openTimestamp(computeOpenDate(openPrice)).openPrice(openPrice.getPrice()).lowPrice(lowPrice.getPrice()).highPrice(highPrice.getPrice()).closePrice(closePrice.getPrice()).closeTimestamp(computeCloseDate(closePrice)).build();
        candlestickRepository.save(computedCandlestick);
    }


    /**
     * The computeOpenPrice method for compute open price for list of quote history hash
     * Notice that we assume the first object of list is the first received quote item
     *
     * @param quoteHistoryHashList mandatory input for list of quote history hash
     * @return QuoteHistoryHash of opening quote price
     */
    protected QuoteHistoryHash computeOpenPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.get(ConstantConfig.FIRST_QUOTE_RECEIVED_INDEX);
    }

    /**
     * The computeClosePrice method for compute close price for list of quote history hash
     * Notice that we assume the last object of list is the last received quote item
     *
     * @param quoteHistoryHashList mandatory input for list of quote history hash
     * @return QuoteHistoryHash of closing quote price
     */
    protected QuoteHistoryHash computeClosePrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.get(quoteHistoryHashList.size() - 1);
    }

    /**
     * The computeLowPrice method for compute close price for list of quote history hash
     * input quote history list are sorted based on min price
     *
     * @param quoteHistoryHashList mandatory input for list of quote history hash
     * @return QuoteHistoryHash of low quote price
     */
    protected QuoteHistoryHash computeLowPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.stream().min(Comparator.comparingDouble(QuoteHistoryHash::getPrice)).get();
    }

    /**
     * The computeHighPrice method for compute close price for list of quote history hash
     * input quote history list are sorted based on max price
     *
     * @param quoteHistoryHashList mandatory input for list of quote history hash
     * @return QuoteHistoryHash of high quote price
     */
    protected QuoteHistoryHash computeHighPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.stream().max(Comparator.comparingDouble(QuoteHistoryHash::getPrice)).get();
    }

    /**
     * The computeOpenDate method for compute open date for list of quote history hash
     * input quote history is Round Floor to compute open date
     *
     * @param openPrice mandatory input for first quote history hash received
     * @return date of open quote date
     */
    protected Date computeOpenDate(QuoteHistoryHash openPrice) {
        return DateUtil.getRoundFloor(openPrice.getReceivedDate());
    }

    /**
     * The computeCloseDate method for compute close date for list of quote history hash
     * input quote history is Round Ceiling Floor to compute close date
     *
     * @param closePrice mandatory input for last quote history hash received
     * @return date of close quote date
     */
    protected Date computeCloseDate(QuoteHistoryHash closePrice) {
        return DateUtil.getRoundCeiling(closePrice.getReceivedDate());
    }

    /**
     * The deleteCandlestickHistory method remove all candlestick history after an instrument was deleted
     *
     * @param isin mandatory input for specific instrument
     */

    @Async
    public void deleteCandlestickHistory(String isin) {
        try {
            log.info("try to delete unnecessary candlestick for instrument:{}", isin);
            candlestickRepository.deleteAllByIsinEquals(isin);
            log.info("successfully delete unnecessary candlestick for instrument:{}", isin);
        } catch (Exception ex) {
            log.error("exception happened during delete unnecessary candlestick for instrument:{}", isin);
        }
    }
}
