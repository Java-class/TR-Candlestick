package tr.traderepublic.candlesticks.candlesticks.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InstrumentNotFoundException extends Exception{
    public InstrumentNotFoundException(String isin) {
        super(String.format("Instrument with id:%s does not exist!", isin));
    }
}
