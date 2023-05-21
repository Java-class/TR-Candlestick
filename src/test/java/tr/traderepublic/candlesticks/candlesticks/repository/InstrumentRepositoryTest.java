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
@SpringBootTest(classes = CandlestickApplication.class)
@Testcontainers
class InstrumentRepositoryTest extends BaseIT {

    @Autowired
    private InstrumentRepository instrumentRepository;

    private final String isin = "123456789MA";

    @Test
    @Order(2)
    void _02testInsertInstrument() {
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
    void _03testDeleteInstrument() {
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
    void _03test1KBulkInsertWithLoop() {
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
