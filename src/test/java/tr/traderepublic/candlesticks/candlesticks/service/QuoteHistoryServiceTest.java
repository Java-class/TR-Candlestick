package tr.traderepublic.candlesticks.candlesticks.service;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.auth.AuthStateCacheable;
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
import tr.traderepublic.candlesticks.candlesticks.model.data.QuoteHistoryHash;
import tr.traderepublic.candlesticks.candlesticks.repository.QuoteHistoryRepository;
import tr.traderepublic.candlesticks.candlesticks.util.DateUtil;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-21 16:09
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = CandlesticksApplication.class)
@Testcontainers
class QuoteHistoryServiceTest {

    @Autowired
    private QuoteHistoryService quoteHistoryService;
    @Autowired
    private QuoteHistoryRepository quoteHistoryRepository;

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