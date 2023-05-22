package tr.traderepublic.candlesticks.candlesticks.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tr.traderepublic.candlesticks.candlesticks.exceptions.InstrumentNotFoundException;

/**
 * Controller Class for Handle All Exception happened in RestController
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-21 20:29
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle InstrumentNotFoundException
     *
     * @param ex mandatory object of exception
     * @return ResponseEntity with error message
     */
    @ExceptionHandler({InstrumentNotFoundException.class})
    public ResponseEntity<Object> handleInstrumentNotFoundException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<Object>(
                ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
