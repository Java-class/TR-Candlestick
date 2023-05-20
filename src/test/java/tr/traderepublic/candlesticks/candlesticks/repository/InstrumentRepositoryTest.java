package tr.traderepublic.candlesticks.candlesticks.repository;

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
import tr.traderepublic.candlesticks.candlesticks.model.data.InstrumentHash;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 1:23
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = CandlesticksApplication.class)
@Testcontainers
public class InstrumentRepositoryTest {

    @Autowired
    private InstrumentRepository instrumentRepository;

    private final String isin = "123456789MA";

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
    public void _02testInsertInstrument() {
        InstrumentHash instrumentHash = new InstrumentHash(isin, "this is first instrument");
        try {
            instrumentRepository.save(instrumentHash);
            Optional<InstrumentHash> optionalInstrumentHash = instrumentRepository.findById(isin);
            assertThat(optionalInstrumentHash.isPresent()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test insert instrument.", ex);
        }
    }


    @Test
    @Order(3)
    public void _03testDeleteInstrument() {
        try {
            instrumentRepository.deleteById(isin);
            Optional<InstrumentHash> optionalInstrumentHash = instrumentRepository.findById(isin);
            assertThat(optionalInstrumentHash.isEmpty()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test delete instrument.", ex);
        }
    }

    @Test
    @Order(4)
    public void _03test1KBulkInsertWithLoop() {
        List<String> isinList = new ArrayList<>();
        InstrumentHash instrumentHash;
        try {
            for (int i = 0; i < 1000; i++) {
                String isin = UUID.randomUUID().toString();
                isinList.add(isin);
                instrumentHash = new InstrumentHash(isin, "dynamic description is" + System.currentTimeMillis());
                instrumentRepository.save(instrumentHash);
            }
            isinList.forEach(isin -> {
                if (instrumentRepository.findById(isin).isEmpty()) {
                    fail("instrument data corrupted during save.");
                }
            });
        } catch (Exception ex) {
            fail("exception happened in test 1k bulk insert instrument.", ex);
        }
    }

}
