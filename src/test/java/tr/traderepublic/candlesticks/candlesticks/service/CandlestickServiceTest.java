package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tr.traderepublic.candlesticks.candlesticks.CandlesticksApplication;
import tr.traderepublic.candlesticks.candlesticks.model.data.CandlestickHash;
import tr.traderepublic.candlesticks.candlesticks.model.data.InstrumentHash;
import tr.traderepublic.candlesticks.candlesticks.repository.CandlestickRepository;
import tr.traderepublic.candlesticks.candlesticks.repository.InstrumentRepository;
import tr.traderepublic.candlesticks.candlesticks.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-21 15:11
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = CandlesticksApplication.class)
@Testcontainers
class CandlestickServiceTest {

    @Autowired
    private CandlestickService candlestickService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private CandlestickRepository candlestickRepository;
    public static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:5.0.3-alpine");

    @Container
    @ClassRule
    public static GenericContainer<?> redis = new GenericContainer<>(REDIS_IMAGE).withExposedPorts(6379);

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", () -> redis.getHost());
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeEach
    public void setup() {
        redis.start();
    }

    @Test
    @Order(1)
    public void _01testRedisStatus() {
        Assertions.assertTrue(redis.isRunning());
    }


    @Test
    void computeCandlestick() {
    }

    @Test
    void fetchLastCandlestick() {
        try {
            String randomIsin = UUID.randomUUID().toString();
            InstrumentHash instrumentHash = InstrumentHash.builder()
                    .id(randomIsin)
                    .description("this is instrument for test delete candlestick history")
                    .build();
            instrumentRepository.save(instrumentHash);
            List<CandlestickHash> candlestickHashList = new ArrayList<>();
            CandlestickHash ch1 = CandlestickHash.builder()
                    .id("ch1")
                    .isin(randomIsin)
                    .openTimestamp(new Date(System.currentTimeMillis()))
                    .openPrice(112.02)
                    .closePrice(123.5)
                    .lowPrice(109.0)
                    .highPrice(129.45)
                    .computeTimestamp(System.currentTimeMillis())
                    .build();

            CandlestickHash ch2 = CandlestickHash.builder()
                    .id("ch2")
                    .isin(randomIsin)
                    .openTimestamp(new Date(System.currentTimeMillis() + 60_000))
                    .openPrice(234.0)
                    .closePrice(218.11)
                    .lowPrice(209.5)
                    .highPrice(234.0)
                    .computeTimestamp(System.currentTimeMillis() + 60_000)
                    .build();

            CandlestickHash ch3 = CandlestickHash.builder()
                    .id("ch3")
                    .isin(randomIsin)
                    .openTimestamp(new Date(System.currentTimeMillis() + 60_000))
                    .openPrice(97.0)
                    .closePrice(98.6)
                    .lowPrice(95.11)
                    .highPrice(99.45)
                    .computeTimestamp(System.currentTimeMillis() + 60_000)
                    .build();
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
            InstrumentHash instrumentHash = InstrumentHash.builder()
                    .id(randomIsin)
                    .description("this is instrument for test save candlestick history")
                    .build();
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
    void computeOpenPrice() {
    }

    @Test
    void computeClosePrice() {
    }

    @Test
    void computeLowPrice() {
    }

    @Test
    void computeHighPrice() {
    }

    @Test
    void computeOpenDate() {
    }

    @Test
    void computeCloseDate() {
    }

    @Test
    void testComputeOpenDate() {
    }

    @Test
    void testComputeCloseDate() {
    }

    @Test
    void deleteCandlestickHistory() {
        try {
            String randomIsin = UUID.randomUUID().toString();
            InstrumentHash instrumentHash = InstrumentHash.builder()
                    .id(randomIsin)
                    .description("this is instrument for test delete candlestick history")
                    .build();
            instrumentRepository.save(instrumentHash);
            List<CandlestickHash> candlestickHashList = new ArrayList<>();
            CandlestickHash ch1 = CandlestickHash.builder()
                    .id(UUID.randomUUID().toString())
                    .isin(randomIsin)
                    .openTimestamp(new Date())
                    .openPrice(112.02)
                    .closePrice(123.5)
                    .lowPrice(109.0)
                    .highPrice(129.45)
                    .computeTimestamp(System.currentTimeMillis())
                    .build();

            CandlestickHash ch2 = CandlestickHash.builder()
                    .id(UUID.randomUUID().toString())
                    .isin(randomIsin)
                    .openTimestamp(new Date())
                    .openPrice(234.0)
                    .closePrice(218.11)
                    .lowPrice(209.5)
                    .highPrice(234.0)
                    .computeTimestamp(System.currentTimeMillis())
                    .build();

            CandlestickHash ch3 = CandlestickHash.builder()
                    .id(UUID.randomUUID().toString())
                    .isin(randomIsin)
                    .openTimestamp(new Date())
                    .openPrice(97.0)
                    .closePrice(98.6)
                    .lowPrice(95.11)
                    .highPrice(99.45)
                    .computeTimestamp(System.currentTimeMillis())
                    .build();
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
}