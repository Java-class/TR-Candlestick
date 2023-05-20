package tr.traderepublic.candlesticks.candlesticks.exceptions;


import tr.traderepublic.candlesticks.candlesticks.model.enums.Type;

public class InstrumentTypeException extends Exception {
    public InstrumentTypeException(Type type) {
        super(String.format("Type for instrument is not correct!", type));
    }
}
