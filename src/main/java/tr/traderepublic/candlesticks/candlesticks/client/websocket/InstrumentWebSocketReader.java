package tr.traderepublic.candlesticks.candlesticks.client.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tr.traderepublic.candlesticks.candlesticks.consts.ConstantConfig;
import tr.traderepublic.candlesticks.candlesticks.exceptions.WebSocketNotConnectedException;
import tr.traderepublic.candlesticks.candlesticks.model.dto.InstrumentMessage;
import tr.traderepublic.candlesticks.candlesticks.service.DataProcessor;
import tr.traderepublic.candlesticks.candlesticks.service.InstrumentDataProcessor;

import java.net.http.WebSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletionStage;

/**
 * Here is the Listener Class for Instrument Item
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 14:36
 */

@Service
@Slf4j
public class InstrumentWebSocketReader implements WebSocket.Listener {
    private final WebsocketClient websocketClient;
    private final DataProcessor processDataService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${service.partnet.producer.address}")
    private String url;


    /**
     * The Contractor of InstrumentWebSocketReader class
     *
     * @param websocketClient    mandatory object of WebSocketClientClass class
     * @param processDataService is the Parser of Incoming Object from WebSocket
     */
    public InstrumentWebSocketReader(WebsocketClient websocketClient, InstrumentDataProcessor processDataService) {
        this.websocketClient = websocketClient;
        this.processDataService = processDataService;
    }

    /**
     * The Listener of Websocket Client for Incoming Instrument Message
     */
    @PostConstruct
    public void listenInstrument() {
        try {
            websocketClient.connect(url, ConstantConfig.INSTRUMENTS_ENDPOINT, this);
        } catch (WebSocketNotConnectedException e) {
            log.error(e.getMessage());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    listenInstrument();
                }
            }, 1000);
        }

    }

    /**
     * The onText Method for Parsing Incoming Instrument Message
     *
     * @param webSocket mandatory object of WebSocket interface
     * @param data      is the message
     * @param last      is the boolean flog for closing message
     */
    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        try {
            InstrumentMessage instrumentMessage =
                    objectMapper.readValue(data.toString(), InstrumentMessage.class);
            log.info("Received Instrument: ===> {}", instrumentMessage);
            processDataService.processData(instrumentMessage);
        } catch (Exception ex) {
            log.error("exception happened during parse instrument data, input message:{}", data.toString(), ex);
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    /**
     * The onClose method where WebSocket Method Closed.
     * We Call the listenInstrument method again for accept new instrument
     *
     * @param webSocket  mandatory object of WebSocket interface
     * @param statusCode is the status code of message
     */

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        listenInstrument();
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }


    /**
     * The onError method where WebSocket method closed.
     * We Call the listenInstrument method again for accept new instrument
     *
     * @param webSocket mandatory object of WebSocket interface
     * @param error     is the throwable object to handle exception
     */
    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        log.error("Error (Websocket) on {} with message: {}", ConstantConfig.INSTRUMENTS_ENDPOINT, error.getMessage());
        listenInstrument();
        WebSocket.Listener.super.onError(webSocket, error);
    }

    /**
     * The onOpen method to Open WebSocket connection
     *
     * @param webSocket mandatory object of WebSocket interface
     */
    @Override
    public void onOpen(WebSocket webSocket) {
        log.error("Opened {} Websocket endpoint with protocol: {}", ConstantConfig.INSTRUMENTS_ENDPOINT, webSocket.getSubprotocol());
        WebSocket.Listener.super.onOpen(webSocket);
    }
}
