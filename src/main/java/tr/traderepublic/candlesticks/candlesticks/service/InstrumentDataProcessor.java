package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tr.traderepublic.candlesticks.candlesticks.model.dto.InstrumentMessage;

/**
 * Incoming Data Processor for Process Instrument Message
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 0:52
 */

@Component("instrumentDataProcessor")
@RequiredArgsConstructor
@Slf4j
public class InstrumentDataProcessor implements DataProcessor<InstrumentMessage> {

    private final InstrumentService instrumentService;

    /**
     * The processData method for store an incoming message from WebSocket
     * We call save or delete method of InstrumentService based on message type (ADD or DELETE)
     *
     * @param instrumentMessage mandatory input for incoming instrument message
     */
    @Override
    public void processData(InstrumentMessage instrumentMessage) {
        switch (instrumentMessage.getType()) {
            case ADD -> instrumentService.saveInstrument(instrumentMessage.getData().getIsin(),
                    instrumentMessage.getData().getDescription());
            case DELETE -> instrumentService.deleteInstrument(instrumentMessage.getData().getIsin());
        }
    }
}
