package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.extern.slf4j.Slf4j;
import tr.traderepublic.candlesticks.candlesticks.BaseIT;
import tr.traderepublic.candlesticks.candlesticks.CandlestickApplication;
import tr.traderepublic.candlesticks.candlesticks.exceptions.InstrumentNotFoundException;
import tr.traderepublic.candlesticks.candlesticks.model.data.CandlestickHash;
import tr.traderepublic.candlesticks.candlesticks.model.data.InstrumentHash;
import tr.traderepublic.candlesticks.candlesticks.model.dto.CandlestickResponseDto;
import tr.traderepublic.candlesticks.candlesticks.repository.CandlestickRepository;
import tr.traderepublic.candlesticks.candlesticks.repository.InstrumentRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 22:42
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = CandlestickApplication.class)
@Testcontainers
class InstrumentServiceTest extends BaseIT {

    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private CandlestickRepository candlestickRepository;

    @Test
    @Order(2)
    void _02saveInstrument() {
        try {
            String isin = UUID.randomUUID().toString();
            instrumentService.saveInstrument(isin, "this is instrument create from InstrumentServiceTest");
            Optional<InstrumentHash> optionalInstrumentHash = instrumentRepository.findById(isin);
            assertThat(optionalInstrumentHash.isPresent()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test insert instrument.", ex);
        }
    }

    @Test
    @Order(3)
    void _03deleteInstrument() {
        try {
            String isin = UUID.randomUUID().toString();
            instrumentService.deleteInstrument(isin);
            Optional<InstrumentHash> optionalInstrumentHash = instrumentRepository.findById(isin);
            assertThat(optionalInstrumentHash.isEmpty()).isEqualTo(true);
        } catch (Exception ex) {
            fail("exception happened in test delete instrument.", ex);
        }
    }

    @Test
    @Order(4)
    void _04getCandlestickHistory() {
        try {
            String isin = UUID.randomUUID().toString();
            instrumentService.saveInstrument(isin, "this is instrument create from InstrumentServiceTest");
            Date date = new Date();
            CandlestickHash ch1 = CandlestickHash.builder()
                    .id(UUID.randomUUID().toString())
                    .isin(isin)
                    .openTimestamp(date)
                    .openPrice(123.5)
                    .lowPrice(120.0)
                    .highPrice(154.0)
                    .closePrice(128.6)
                    .closeTimestamp(new Date(date.getTime() + 60_000))
                    .computeTimestamp(date.getTime()).build();
            candlestickRepository.save(ch1);
            List<CandlestickResponseDto> candlestickHistory = instrumentService.getCandlestickHistory(isin);
            if (candlestickHistory.size() == 1) {
                Assertions.assertEquals(ch1.getId(), candlestickHistory.get(0).id());
            } else {
                fail("error in loading candlestick history for isin:{}", isin);
            }
        } catch (Exception ex) {
            fail("exception happened in test get instrument.", ex);
        }
    }

    @Test
    @Order(5)
    void _05getCandlestickHistoryWithException() {
        try {
            String isin = UUID.randomUUID().toString();
            instrumentService.getCandlestickHistory(isin);
        } catch (InstrumentNotFoundException ex) {
            log.info("instrument nou found during test..");
            assert true;
        } catch (Exception ex) {
            fail("exception happened in test get instrument.", ex);
        }
    }
}