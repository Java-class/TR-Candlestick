package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Scheduled(fixedRate = 60_000)
    public void computeCandlestick() {
        try {
            log.info("start to compute candlestick data...");
            StopWatch watch = new StopWatch();
            watch.start();
            long minuteBefore = System.currentTimeMillis() - (60 * 1000);
            String timeChunk = DateUtil.getTimeChunk(minuteBefore);
            Iterable<InstrumentHash> instrumentList = instrumentRepository.findAll();
            instrumentList.forEach(instrument -> {
                if (instrument != null && instrument.getId() != null) {
                    List<QuoteHistoryHash> quoteHistoryHashList =
                            quoteHistoryRepository.findByIsinEqualsAndTimeChunkEquals(instrument.getId(), timeChunk);
                    if (quoteHistoryHashList.size() > 0) {
                        QuoteHistoryHash openPrice = computeOpenPrice(quoteHistoryHashList);

                        QuoteHistoryHash closePrice = computeClosePrice(quoteHistoryHashList);

                        QuoteHistoryHash lowPrice = computeLowPrice(quoteHistoryHashList);

                        QuoteHistoryHash highPrice = computeHighPrice(quoteHistoryHashList);

                        CandlestickHash computedCandlestick = CandlestickHash.builder()
                                .id(UUID.randomUUID().toString())
                                .timeChunk(timeChunk)
                                .computeTimestamp(System.currentTimeMillis())
                                .isin(instrument.getId())
                                .openTimestamp(computeOpenDate(openPrice))
                                .openPrice(openPrice.getPrice())
                                .lowPrice(lowPrice.getPrice())
                                .highPrice(highPrice.getPrice())
                                .closePrice(closePrice.getPrice())
                                .closeTimestamp(computeCloseDate(closePrice))
                                .build();
                        candlestickRepository.save(computedCandlestick);
                    }
                }
            });
            watch.stop();
            log.info("candlestick data process finished successfully, duration in milliseconds:{}",
                    watch.getTotalTimeMillis());
        } catch (Exception ex) {
            log.error("exception happened during process candlestick data.");
        }
    }

    protected QuoteHistoryHash computeOpenPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.get(ConstantConfig.FIRST_QUOTE_RECEIVED_INDEX);
    }

    protected QuoteHistoryHash computeClosePrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.get(quoteHistoryHashList.size() - 1);
    }

    protected QuoteHistoryHash computeLowPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.stream()
                .min(Comparator.comparingDouble(QuoteHistoryHash::getPrice)).get();
    }

    protected QuoteHistoryHash computeHighPrice(List<QuoteHistoryHash> quoteHistoryHashList) {
        return quoteHistoryHashList.stream()
                .max(Comparator.comparingDouble(QuoteHistoryHash::getPrice)).get();
    }

    protected Date computeOpenDate(QuoteHistoryHash openPrice) {
        return DateUtil.getRoundFloor(openPrice.getReceivedDate());
    }

    protected Date computeCloseDate(QuoteHistoryHash closePrice) {
        return DateUtil.getRoundCeiling(closePrice.getReceivedDate());
    }

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
