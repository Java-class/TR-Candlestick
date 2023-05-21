package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.extern.slf4j.Slf4j;
import tr.traderepublic.candlesticks.candlesticks.BaseIT;
import tr.traderepublic.candlesticks.candlesticks.CandlestickApplication;
import tr.traderepublic.candlesticks.candlesticks.model.data.QuoteHistoryHash;
import tr.traderepublic.candlesticks.candlesticks.repository.QuoteHistoryRepository;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-21 16:09
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = CandlestickApplication.class)
@Testcontainers
class QuoteHistoryServiceTest extends BaseIT {

    @Autowired
    private QuoteHistoryService quoteHistoryService;
    @Autowired
    private QuoteHistoryRepository quoteHistoryRepository;

    @Test
    @Order(2)
    void _02testSave() {
        try {
            long currentTime = System.currentTimeMillis();
            String randomIsin = UUID.randomUUID().toString();
            double price = 120.5;
            QuoteHistoryHash quoteHistoryHash = quoteHistoryService.save(randomIsin, price, currentTime);
            assertThat(quoteHistoryRepository.findById(quoteHistoryHash.getId()).isPresent()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test add quote.", ex);
        }
    }
}