package tr.traderepublic.candlesticks.candlesticks.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import tr.traderepublic.candlesticks.candlesticks.BaseIT;
import tr.traderepublic.candlesticks.candlesticks.CandlestickApplication;
import tr.traderepublic.candlesticks.candlesticks.model.data.QuoteHistoryHash;
import tr.traderepublic.candlesticks.candlesticks.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-19 14:03
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = CandlestickApplication.class)
@Testcontainers
class QuoteRepositoryTest extends BaseIT {

    @Autowired
    private QuoteHistoryRepository quoteHistoryRepository;
    private final String isin = "123456789MA";

    @Test
    @Order(2)
    void _02testInsertQuote() {
        QuoteHistoryHash quoteHistoryHash = new QuoteHistoryHash(UUID.randomUUID().toString(),
                isin,
                120.01,
                System.currentTimeMillis(),
                DateUtil.getTimeChunk(System.currentTimeMillis()));
        try {
            quoteHistoryRepository.save(quoteHistoryHash);
            Optional<QuoteHistoryHash> optionalQuoteHistoryHash = quoteHistoryRepository.findById(quoteHistoryHash.getId());
            assertThat(optionalQuoteHistoryHash.isPresent()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test insert quote.", ex);
        }
    }

    @Test
    @Order(3)
    void _03testDeleteQuote() {
        QuoteHistoryHash quoteHistoryHash = new QuoteHistoryHash(UUID.randomUUID().toString(),
                isin,
                120.01,
                System.currentTimeMillis(),
                DateUtil.getTimeChunk(System.currentTimeMillis()));
        try {
            quoteHistoryRepository.save(quoteHistoryHash);
            quoteHistoryRepository.deleteById(quoteHistoryHash.getId());
            Optional<QuoteHistoryHash> optionalQuoteHistoryHash = quoteHistoryRepository.findById(quoteHistoryHash.getId());
            assertThat(optionalQuoteHistoryHash.isEmpty()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test insert quote.", ex);
        }
    }


    @Test
    @Order(4)
    void _04test1KBulkInsertWithLoop() {
        List<String> idList = new ArrayList<>();
        QuoteHistoryHash quoteHistoryHash;
        try {
            for (int i = 0; i < 1000; i++) {
                String isin = UUID.randomUUID().toString();
                quoteHistoryHash = new QuoteHistoryHash(UUID.randomUUID().toString(),
                        isin,
                        120.01,
                        System.currentTimeMillis(),
                        DateUtil.getTimeChunk(System.currentTimeMillis()));
                idList.add(quoteHistoryHash.getId());
                quoteHistoryRepository.save(quoteHistoryHash);
            }
            idList.forEach(id -> {
                if (quoteHistoryRepository.findById(id).isEmpty()) {
                    fail("quote data corrupted during save.");
                }
            });
        } catch (Exception ex) {
            fail("exception happened in test 1k bulk insert quote.", ex);
        }
    }
}