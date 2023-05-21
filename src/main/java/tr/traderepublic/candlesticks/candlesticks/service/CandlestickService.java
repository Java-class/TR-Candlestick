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
import java.util.Optional;
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
     * This method computed based on chunk time duration before and if there isn't any quote received, it will be generated
     * the candlestick based on last candlestick computed
     *
     * @param instrument mandatory object of instrument
     * @param timeChunk  current time chunk for computing candlestick
     * @return boolean true if successfully candlestick computed
     */
    protected boolean computeCandlestick(InstrumentHash instrument, String timeChunk) {
        boolean result = true;
        try {
            Date openDate = null;
            double openPrice = -1;
            double closePrice = -1;
            double lowPrice = -1;
            double highPrice = -1;
            Date closeDate = null;
            List<QuoteHistoryHash> quoteHistoryHashList = quoteHistoryRepository.findByIsinEqualsAndTimeChunkEquals(instrument.getId(), timeChunk);
            if (quoteHistoryHashList.size() > 0) {
                openDate = computeOpenDate(quoteHistoryHashList);
                openPrice = computeOpenPrice(quoteHistoryHashList);
                closePrice = computeClosePrice(quoteHistoryHashList);
                lowPrice = computeLowPrice(quoteHistoryHashList);
                highPrice = computeHighPrice(quoteHistoryHashList);
                closeDate = computeCloseDate(quoteHistoryHashList);
            } else {
                CandlestickHash lastCandlestickHash = fetchLastCandlestick(instrument);
                if (lastCandlestickHash != null) {
                    openDate = lastCandlestickHash.getOpenTimestamp();
                    openPrice = lastCandlestickHash.getOpenPrice();
                    closePrice = lastCandlestickHash.getClosePrice();
                    lowPrice = lastCandlestickHash.getLowPrice();
                    highPrice = lastCandlestickHash.getHighPrice();
                    closeDate = lastCandlestickHash.getCloseTimestamp();
                }
            }
            saveCandlestickHistory(instrument, timeChunk, openDate, openPrice, lowPrice, highPrice, closePrice, closeDate);
        } catch (Exception ex) {
            log.error("exception happened during compute candlestick for instrument:{}", instrument.getId(), ex);
            result = false;
        }
        return result;
    }

    public CandlestickHash fetchLastCandlestick(InstrumentHash instrumentHash) {
        List<CandlestickHash> candlestickHashList = candlestickRepository.findByIsinEquals(instrumentHash.getId());
        Optional<CandlestickHash> optionalLastCandlestick = candlestickHashList.stream().max(Comparator.comparingDouble(CandlestickHash::getComputeTimestamp));
        return optionalLastCandlestick.orElse(null);
    }

    /**
     * The computeCandlestick method for compute candlestick input instrument
     * We Call the listenQuotes method again for accept new quote
     *
     * @param instrument mandatory object of instrument
     * @param timeChunk  current time chunk for computing candlestick
     * @retutn CandlestickHash item
     */
    protected CandlestickHash saveCandlestickHistory(InstrumentHash instrument, String timeChunk, Date openDate, double openPrice,
                                                     double lowPrice, double highPrice, double closePrice, Date closeDate) {
        CandlestickHash computedCandlestick = CandlestickHash.builder()
                .id(UUID.randomUUID().toString())
                .timeChunk(timeChunk)
                .computeTimestamp(System.currentTimeMillis())
                .isin(instrument.getId())
                .openTimestamp(computeOpenDate(openDate))
                .openPrice(openPrice)
                .lowPrice(lowPrice)
                .highPrice(highPrice)
                .closePrice(closePrice)
                .closeTimestamp(computeCloseDate(closeDate))
                .build();
        return candlestickRepository.save(computedCandlestick);
    }


    /**
     * The computeOpenPrice method for compute open price for a list of quote history hash
     * Notice that we assume the first object of a list is the first received quote item
     *
     * @param quoteHistoryHashList mandatory input for a list of quote history hash
     * @return double of opening quote price
     */
    protected double computeOpenPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.stream().min(Comparator.comparing(QuoteHistoryHash::getReceivedDate)).get().getPrice();
    }

    /**
     * The computeClosePrice method for compute close price for list of quote history hash
     * Notice that we assume the last object of list is the last received quote item
     *
     * @param quoteHistoryHashList mandatory input for list of quote history hash
     * @return double of closing quote price
     */
    protected double computeClosePrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.stream().max(Comparator.comparing(QuoteHistoryHash::getReceivedDate)).get().getPrice();
    }

    /**
     * The computeLowPrice method for compute close price for list of quote history hash
     * input quote history list are sort based on min price
     *
     * @param quoteHistoryHashList mandatory input for list of quote history hash
     * @return double of lowest quote price
     */
    protected double computeLowPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.stream().min(Comparator.comparing(QuoteHistoryHash::getPrice)).get().getPrice();
    }

    /**
     * The computeHighPrice method for computed close price for list of a quote history hash
     * input quote history list is sorted based on max price
     *
     * @param quoteHistoryHashList mandatory input for list of quote history hash
     * @return double of highest quote price
     */
    protected double computeHighPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.stream().max(Comparator.comparing(QuoteHistoryHash::getPrice)).get().getPrice();
    }

    /**
     * The computeOpenDate method for compute open date for list of quote history hash
     * input quote history is Round Floor to compute open date
     *
     * @param receivedDate mandatory input for first quote history date received
     * @return date of open quote date
     */
    protected Date computeOpenDate(Date receivedDate) {
        return DateUtil.getRoundFloor(receivedDate.getTime());
    }

    /**
     * The computeCloseDate method for compute close date for list of quote history hash
     * input quote history is Round Ceiling Floor to compute close date
     *
     * @param closeDate mandatory input for last date quote history hash received
     * @return date of close quote date
     */
    protected Date computeCloseDate(Date closeDate) {
        return DateUtil.getRoundCeiling(closeDate.getTime());
    }


    /**
     * The computeOpenDate method for compute open date for list of quote history hash
     * input quote history is Round Floor to compute open date
     *
     * @param quoteHistoryHashList mandatory input for list of quote history date received
     * @return date of open quote date
     */
    protected Date computeOpenDate(List<QuoteHistoryHash> quoteHistoryHashList) {
        QuoteHistoryHash quoteHistoryHash = quoteHistoryHashList.get(ConstantConfig.FIRST_QUOTE_RECEIVED_INDEX);
        if (quoteHistoryHash != null) {
            return DateUtil.getRoundFloor(quoteHistoryHash.getReceivedDate());
        } else {
            return null;
        }
    }

    /**
     * The computeCloseDate method for compute close date for list of quote history hash
     * input quote history is Round Ceiling Floor to compute close date
     *
     * @param quoteHistoryHashList mandatory input for last date quote history hash received
     * @return date of close quote date
     */
    protected Date computeCloseDate(List<QuoteHistoryHash> quoteHistoryHashList) {
        QuoteHistoryHash quoteHistoryHash = quoteHistoryHashList.get(quoteHistoryHashList.size() - 1);
        if (quoteHistoryHash != null) {
            return DateUtil.getRoundCeiling(quoteHistoryHash.getReceivedDate());
        } else {
            return null;
        }
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
            List<CandlestickHash> candlestickHashList = candlestickRepository.findByIsinEquals(isin);
            candlestickRepository.deleteAll(candlestickHashList);
            log.info("successfully delete unnecessary candlestick for instrument:{}", isin);
        } catch (Exception ex) {
            log.error("exception happened during delete unnecessary candlestick for instrument:{}", isin);
        }
    }
}
