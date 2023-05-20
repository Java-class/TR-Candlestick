package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tr.traderepublic.candlesticks.candlesticks.CandlesticksApplication;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 23:30
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = CandlesticksApplication.class)
@Testcontainers
class CandlestickServiceTest {

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
    void deleteCandlestickHistory() {
    }
}