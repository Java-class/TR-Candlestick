package tr.traderepublic.candlesticks.candlesticks.exceptions;


/**
 * Exception class for web socket connection issue
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 08:11
 */
public class WebSocketNotConnectedException extends Throwable {

    /**
     * The contractor method requested URL to WebSocket
     *
     * @param url url of listener to websocket
     */
    public WebSocketNotConnectedException(String url) {
        super(String.format("Websocket not connected to %s, trying to reconnect...", url));
    }
}
