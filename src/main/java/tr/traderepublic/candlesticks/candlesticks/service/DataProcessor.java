package tr.traderepublic.candlesticks.candlesticks.service;

/**
 * Incoming Data Processor Based on Message Type
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 0:40
 */

public interface DataProcessor<T> {
    /**
     * The processData method for store an incoming message from WebSocket
     *
     * @param t mandatory input for Message (Instrument or Quote)
     */
    void processData(T t);
}
