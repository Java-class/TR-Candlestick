package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tr.traderepublic.candlesticks.candlesticks.BaseIT;
import tr.traderepublic.candlesticks.candlesticks.model.data.CandlestickHash;
import tr.traderepublic.candlesticks.candlesticks.model.data.InstrumentHash;
import tr.traderepublic.candlesticks.candlesticks.model.data.QuoteHistoryHash;
import tr.traderepublic.candlesticks.candlesticks.repository.CandlestickRepository;
import tr.traderepublic.candlesticks.candlesticks.repository.InstrumentRepository;
import tr.traderepublic.candlesticks.candlesticks.util.DateUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-21 15:11
 */
@Slf4j
class CandlestickServiceTest extends BaseIT {

    @Autowired
    private CandlestickService candlestickService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private QuoteHistoryService quoteHistoryService;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private CandlestickRepository candlestickRepository;


    @Test
    void computeCandlestick() {
        long startTime = System.currentTimeMillis();
        String timeChunk = DateUtil.getTimeChunk(startTime);
        List<String> randomIsin = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            randomIsin.add(UUID.randomUUID().toString());
        }
        randomIsin.forEach(isin -> {
            instrumentService.saveInstrument(isin, "Random Description," + UUID.randomUUID());
        });

        HashMap<String, List<QuoteHistoryHash>> quoteData = new HashMap<>();
        randomIsin.forEach(isin -> {
            List<QuoteHistoryHash> quoteHistoryHashList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                double randomPrice = RandomUtils.nextDouble(100.0, 150.0);
                long randomReceivedTime = RandomUtils.nextLong(startTime, startTime + 59000);
                QuoteHistoryHash quoteHistoryHash = quoteHistoryService.save(isin, randomPrice, randomReceivedTime);
                quoteHistoryHashList.add(quoteHistoryHash);
            }
            quoteData.put(isin, quoteHistoryHashList);
        });

        HashMap<String, CandlestickItem> computedCandlestickHashHashMap = new HashMap<>();
        randomIsin.forEach(isin -> {
            Optional<InstrumentHash> optionalInstrumentHash = instrumentRepository.findById(isin);
            if (optionalInstrumentHash.isPresent()) {
                InstrumentHash instrumentHash = optionalInstrumentHash.get();
                candlestickService.computeCandlestick(instrumentHash, timeChunk);
                List<CandlestickHash> candlestickHashList = candlestickRepository.findByIsinEquals(isin);
                if (candlestickHashList.size() == 1) {
                    CandlestickHash computedCandlestickHash = candlestickHashList.get(0);
                    CandlestickItem candlestickItem = new CandlestickItem(computedCandlestickHash.getOpenTimestamp(),
                            computedCandlestickHash.getOpenPrice(),
                            computedCandlestickHash.getLowPrice(),
                            computedCandlestickHash.getHighPrice(),
                            computedCandlestickHash.getClosePrice(),
                            computedCandlestickHash.getCloseTimestamp());
                    computedCandlestickHashHashMap.put(isin, candlestickItem);
                }
            }
        });

        HashMap<String, CandlestickItem> actualCandlestickHashHashMap = new HashMap<>();
        for (String isin : randomIsin) {
            List<QuoteHistoryHash> quoteHistoryHashList = quoteData.get(isin);
            Date actualOpenPriceDate = DateUtil.getRoundFloor(quoteHistoryHashList.stream().min(Comparator.comparingLong(QuoteHistoryHash::getReceivedDate)).get().getReceivedDate());
            double actualOpenPrice = quoteHistoryHashList.stream().min(Comparator.comparing(QuoteHistoryHash::getReceivedDate)).get().getPrice();
            double actualLowestPrice = quoteHistoryHashList.stream().min(Comparator.comparing(QuoteHistoryHash::getPrice)).get().getPrice();
            double actualHighestPrice = quoteHistoryHashList.stream().max(Comparator.comparing(QuoteHistoryHash::getPrice)).get().getPrice();
            double actualClosePrice = quoteHistoryHashList.stream().max(Comparator.comparing(QuoteHistoryHash::getReceivedDate)).get().getPrice();
            Date actualClosePriceDate = DateUtil.getRoundCeiling(quoteHistoryHashList.stream().max(Comparator.comparingLong(QuoteHistoryHash::getReceivedDate)).get().getReceivedDate());
            CandlestickItem candlestickItem = new CandlestickItem(actualOpenPriceDate,
                    actualOpenPrice,
                    actualLowestPrice,
                    actualHighestPrice,
                    actualClosePrice,
                    actualClosePriceDate);
            actualCandlestickHashHashMap.put(isin, candlestickItem);
        }

        randomIsin.forEach(isin -> {
            if (!actualCandlestickHashHashMap.get(isin).equals(computedCandlestickHashHashMap.get(isin))) {
                log.info("@@@@@@@@@ actual item:{}, \n computedItem:{}", actualCandlestickHashHashMap.get(isin), computedCandlestickHashHashMap.get(isin));
                fail("test of candlestick computation algorithm failed");
            }
        });
    }


    @Test
    void fetchLastCandlestick() {
        try {
            String randomIsin = UUID.randomUUID().toString();
            InstrumentHash instrumentHash = InstrumentHash.builder().id(randomIsin).description("this is instrument for test delete candlestick history").build();
            instrumentRepository.save(instrumentHash);
            List<CandlestickHash> candlestickHashList = new ArrayList<>();
            CandlestickHash ch1 = CandlestickHash.builder().id("ch1").isin(randomIsin).openTimestamp(new Date(System.currentTimeMillis())).openPrice(112.02).closePrice(123.5).lowPrice(109.0).highPrice(129.45).computeTimestamp(System.currentTimeMillis()).build();

            CandlestickHash ch2 = CandlestickHash.builder().id("ch2").isin(randomIsin).openTimestamp(new Date(System.currentTimeMillis() + 60_000)).openPrice(234.0).closePrice(218.11).lowPrice(209.5).highPrice(234.0).computeTimestamp(System.currentTimeMillis() + 60_000).build();

            CandlestickHash ch3 = CandlestickHash.builder().id("ch3").isin(randomIsin).openTimestamp(new Date(System.currentTimeMillis() + 60_000)).openPrice(97.0).closePrice(98.6).lowPrice(95.11).highPrice(99.45).computeTimestamp(System.currentTimeMillis() + 60_000).build();
            candlestickHashList.add(ch1);
            candlestickHashList.add(ch2);
            candlestickHashList.add(ch3);
            candlestickRepository.saveAll(candlestickHashList);

            CandlestickHash lastCandlestick = candlestickService.fetchLastCandlestick(instrumentHash);

            Assertions.assertEquals(ch3.getId(), lastCandlestick.getId());
        } catch (Exception ex) {
            fail("exception happened in test delete candlestick history.", ex);
        }
    }

    @Test
    void saveCandlestickHistory() {
        try {
            String randomIsin = UUID.randomUUID().toString();
            InstrumentHash instrumentHash = InstrumentHash.builder().id(randomIsin).description("this is instrument for test save candlestick history").build();
            instrumentRepository.save(instrumentHash);
            CandlestickHash savedCandle = candlestickService.saveCandlestickHistory(instrumentHash,
                    DateUtil.getTimeChunk(System.currentTimeMillis()),
                    new Date(System.currentTimeMillis()),
                    112.0,
                    113.9,
                    111.0,
                    119.04,
                    new Date(System.currentTimeMillis() + 10_000));
            assertThat(candlestickRepository.findById(savedCandle.getId()).isPresent()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test save candlestick history.", ex);
        }
    }

    @Test
    void computeClosePrice() {
        try {
            List<QuoteHistoryHash> quoteHistoryHashList = new ArrayList<>();
            String randomIsin = UUID.randomUUID().toString();
            String timeChunk = DateUtil.getTimeChunk(System.currentTimeMillis());
            QuoteHistoryHash qh1 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(112.07).build();

            QuoteHistoryHash qh2 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(119.11).build();

            QuoteHistoryHash qh3 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(123.09).build();

            quoteHistoryHashList.add(qh1);
            quoteHistoryHashList.add(qh2);
            quoteHistoryHashList.add(qh3);

            double computedPrice = candlestickService.computeClosePrice(quoteHistoryHashList);
            Assertions.assertEquals(qh3.getPrice(), computedPrice);
        } catch (Exception ex) {
            fail("exception happened in test compute close price.", ex);
        }
    }


    @Test
    void computeLowPrice() {
        try {
            List<QuoteHistoryHash> quoteHistoryHashList = new ArrayList<>();
            String randomIsin = UUID.randomUUID().toString();
            String timeChunk = DateUtil.getTimeChunk(System.currentTimeMillis());
            QuoteHistoryHash qh1 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(112.07).build();

            QuoteHistoryHash qh2 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(109.11).build();

            QuoteHistoryHash qh3 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(123.09).build();

            quoteHistoryHashList.add(qh1);
            quoteHistoryHashList.add(qh2);
            quoteHistoryHashList.add(qh3);

            double computedPrice = candlestickService.computeLowPrice(quoteHistoryHashList);
            Assertions.assertEquals(qh2.getPrice(), computedPrice);
        } catch (Exception ex) {
            fail("exception happened in test compute lowest price.", ex);
        }
    }

    @Test
    void computeHighPrice() {
        try {
            List<QuoteHistoryHash> quoteHistoryHashList = new ArrayList<>();
            String randomIsin = UUID.randomUUID().toString();
            String timeChunk = DateUtil.getTimeChunk(System.currentTimeMillis());
            QuoteHistoryHash qh1 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(112.07).build();

            QuoteHistoryHash qh2 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(137.11).build();

            QuoteHistoryHash qh3 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(123.09).build();

            quoteHistoryHashList.add(qh1);
            quoteHistoryHashList.add(qh2);
            quoteHistoryHashList.add(qh3);

            double computedPrice = candlestickService.computeHighPrice(quoteHistoryHashList);
            Assertions.assertEquals(qh2.getPrice(), computedPrice);
        } catch (Exception ex) {
            fail("exception happened in test compute highest price.", ex);
        }
    }

    @Test
    void computeOpenDate() {
        try {
            List<QuoteHistoryHash> quoteHistoryHashList = new ArrayList<>();
            String randomIsin = UUID.randomUUID().toString();
            String timeChunk = DateUtil.getTimeChunk(System.currentTimeMillis());
            QuoteHistoryHash qh1 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(112.07).build();

            QuoteHistoryHash qh2 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis() + 25_000).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(109.11).build();

            QuoteHistoryHash qh3 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(123.09).build();

            quoteHistoryHashList.add(qh1);
            quoteHistoryHashList.add(qh2);
            quoteHistoryHashList.add(qh3);

            Date openDate = candlestickService.computeOpenDate(quoteHistoryHashList);
            Assertions.assertEquals(DateUtil.getRoundFloor(qh1.getReceivedDate()), openDate);
        } catch (Exception ex) {
            fail("exception happened in test compute open date.", ex);
        }
    }

    @Test
    void computeCloseDate() {
        try {
            List<QuoteHistoryHash> quoteHistoryHashList = new ArrayList<>();
            String randomIsin = UUID.randomUUID().toString();
            String timeChunk = DateUtil.getTimeChunk(System.currentTimeMillis());
            QuoteHistoryHash qh1 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(112.07).build();

            QuoteHistoryHash qh2 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis() + 25_000).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(109.11).build();

            QuoteHistoryHash qh3 = QuoteHistoryHash.builder().receivedDate(System.currentTimeMillis()).id(UUID.randomUUID().toString()).isin(randomIsin).timeChunk(timeChunk).price(123.09).build();

            quoteHistoryHashList.add(qh1);
            quoteHistoryHashList.add(qh2);
            quoteHistoryHashList.add(qh3);

            Date closeDate = candlestickService.computeCloseDate(quoteHistoryHashList);
            Assertions.assertEquals(DateUtil.getRoundCeiling(qh3.getReceivedDate()), closeDate);
        } catch (Exception ex) {
            fail("exception happened in test compute open date.", ex);
        }
    }

    @Test
    void deleteCandlestickHistory() {
        try {
            String randomIsin = UUID.randomUUID().toString();
            InstrumentHash instrumentHash = InstrumentHash.builder().id(randomIsin).description("this is instrument for test delete candlestick history").build();
            instrumentRepository.save(instrumentHash);
            List<CandlestickHash> candlestickHashList = new ArrayList<>();
            CandlestickHash ch1 = CandlestickHash.builder().id(UUID.randomUUID().toString()).isin(randomIsin).openTimestamp(new Date()).openPrice(112.02).closePrice(123.5).lowPrice(109.0).highPrice(129.45).computeTimestamp(System.currentTimeMillis()).build();

            CandlestickHash ch2 = CandlestickHash.builder().id(UUID.randomUUID().toString()).isin(randomIsin).openTimestamp(new Date()).openPrice(234.0).closePrice(218.11).lowPrice(209.5).highPrice(234.0).computeTimestamp(System.currentTimeMillis()).build();

            CandlestickHash ch3 = CandlestickHash.builder().id(UUID.randomUUID().toString()).isin(randomIsin).openTimestamp(new Date()).openPrice(97.0).closePrice(98.6).lowPrice(95.11).highPrice(99.45).computeTimestamp(System.currentTimeMillis()).build();
            candlestickHashList.add(ch1);
            candlestickHashList.add(ch2);
            candlestickHashList.add(ch3);
            candlestickRepository.saveAll(candlestickHashList);
            candlestickService.deleteCandlestickHistory(randomIsin);
            Thread.sleep(10_000);
            assertThat(candlestickRepository.findByIsinEquals(randomIsin).isEmpty()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test delete candlestick history.", ex);
        }
    }

    private record CandlestickItem(Date openTimestamp,
                                   double openPrice,
                                   double lowPrice,
                                   double highPrice,
                                   double closePrice,
                                   Date closeTimestamp) {

    }
}