package tr.traderepublic.candlesticks.candlesticks.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tr.traderepublic.candlesticks.candlesticks.exceptions.InstrumentNotFoundException;
import tr.traderepublic.candlesticks.candlesticks.model.dto.CandlestickResponseDto;
import tr.traderepublic.candlesticks.candlesticks.service.InstrumentService;

import java.util.List;

/**
 * Controller Class for Represent Candlestick History for Specific Instrument
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 20:22
 */

@RestController
@RequestMapping("/candlesticks")
@RequiredArgsConstructor
@Slf4j
public class CandlestickController {

    private final InstrumentService instrumentService;

    /**
     * Get Candlestick History for specific ISIN web service
     *
     * @param isin mandatory object of instrument's identifier
     * @return list of Candlestick ResponseDto object for represent candlestick history
     * @throws InstrumentNotFoundException where instrument not found
     */
    @GetMapping
    public List<CandlestickResponseDto> getCandlestickHistory(@RequestParam(name = "isin") String isin)
            throws InstrumentNotFoundException {
        log.debug("get candlestick history called for instrument:{}", isin);
        return instrumentService.getCandlestickHistory(isin);
    }
}
