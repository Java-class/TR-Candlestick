package tr.traderepublic.candlesticks.candlesticks.client.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tr.traderepublic.candlesticks.candlesticks.exceptions.WebSocketNotConnectedException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

/**
 * This is a Websocket Client class for Connecting to websocket
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 8:24
 */

@Service
@Slf4j
public class WebsocketClient {

    /**
     * The connection method for connect through WebSocket
     *
     * @param url      mandatory object of websocket producer
     * @param endpoint the endpoint of websocket to be listened
     * @param listener the listener method to handle incoming message from websocket
     */
    public void connect(String url, String endpoint, WebSocket.Listener listener) throws WebSocketNotConnectedException {
        try {
            CompletableFuture<WebSocket> webSocket = HttpClient
                    .newHttpClient()
                    .newWebSocketBuilder()
                    .buildAsync(URI.create(url + endpoint), listener);
            webSocket.get();
        } catch (Exception ex) {
            log.error("exception happened during connect to websocket", ex);
            throw new WebSocketNotConnectedException(url + endpoint);
        }
    }
}
