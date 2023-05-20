package tr.traderepublic.candlesticks.candlesticks.service;

/**
 * Incoming Data Processor Based on Message Type
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 0:40
 */

public interface DataProcessor<T> {
    void processData(T t);
}
