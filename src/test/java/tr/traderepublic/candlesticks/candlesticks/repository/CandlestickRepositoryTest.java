package tr.traderepublic.candlesticks.candlesticks.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import tr.traderepublic.candlesticks.candlesticks.BaseIT;
import tr.traderepublic.candlesticks.candlesticks.CandlestickApplication;
import tr.traderepublic.candlesticks.candlesticks.model.data.CandlestickHash;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-19 16:29
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = CandlestickApplication.class)
@Testcontainers
class CandlestickRepositoryTest extends BaseIT {

    @Autowired
    private CandlestickRepository candlestickRepository;
    private final String isin = "123456789MA";
    private final String randomId = UUID.randomUUID().toString();

    @Test
    @Order(1)
    void _01testRedisStatus() {
        Assertions.assertTrue(redis.isRunning());
    }

    @Test
    @Order(2)
    void _02testInsertCandlestick() {
        CandlestickHash candlestickHash = new CandlestickHash();
        candlestickHash.setId(randomId);
        candlestickHash.setIsin(isin);
        candlestickHash.setOpenTimestamp(new Date());
        candlestickHash.setOpenPrice(120.19);
        candlestickHash.setLowPrice(112.0);
        candlestickHash.setHighPrice(127.50);
        candlestickHash.setClosePrice(123.0);
        candlestickHash.setCloseTimestamp(new Date(System.currentTimeMillis() + 10));
        candlestickHash.setComputeTimestamp(System.currentTimeMillis());
        try {
            candlestickRepository.save(candlestickHash);
            Optional<CandlestickHash> optionalCandlestickHash = candlestickRepository.findById(randomId);
            assertThat(optionalCandlestickHash.isPresent()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test insert candlestick.", ex);
        }
    }

    @Test
    @Order(3)
    void _03testDeleteInstrument() {
        try {
            candlestickRepository.deleteById(randomId);
            Optional<CandlestickHash> optionalCandlestickHash = candlestickRepository.findById(randomId);
            assertThat(optionalCandlestickHash.isEmpty()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test delete candlestick.", ex);
        }
    }

}
