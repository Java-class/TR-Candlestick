package tr.traderepublic.candlesticks.candlesticks.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class for instrument not found
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 11:49
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InstrumentNotFoundException extends Exception {

    /**
     * The contractor method by ISIN of instrument
     *
     * @param isin instrument's identifier
     */
    public InstrumentNotFoundException(String isin) {
        super(String.format("Instrument with id:%s does not exist!", isin));
    }
}
