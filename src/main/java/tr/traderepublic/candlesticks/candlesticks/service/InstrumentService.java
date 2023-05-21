package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tr.traderepublic.candlesticks.candlesticks.exceptions.InstrumentNotFoundException;
import tr.traderepublic.candlesticks.candlesticks.model.data.CandlestickHash;
import tr.traderepublic.candlesticks.candlesticks.model.data.InstrumentHash;
import tr.traderepublic.candlesticks.candlesticks.model.dto.CandlestickResponseDto;
import tr.traderepublic.candlesticks.candlesticks.repository.CandlestickRepository;
import tr.traderepublic.candlesticks.candlesticks.repository.InstrumentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Instrument Service Class for Manage Instrument Item
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 9:50
 */
@Service
@RequiredArgsConstructor
public class InstrumentService {

    private final InstrumentRepository instrumentRepository;

    private final CandlestickRepository candlestickRepository;

    private final CandlestickService candlestickService;

    /**
     * The saveInstrument method for store new instrument
     *
     * @param isin        mandatory input for instrument's identifier
     * @param description optional input for instrument's description
     */

    public void saveInstrument(String isin, String description) {
        InstrumentHash instrumentHash = new InstrumentHash(isin, description);
        instrumentRepository.save(instrumentHash);
    }

    /**
     * The deleteInstrument method for delete instrument
     * We call deleteCandlestick history also for remove unnecessary candlestick history
     * Execution type is Async because this process might takes long
     *
     * @param isin mandatory input for instrument's identifier
     */
    @Async
    public void deleteInstrument(String isin) {
        instrumentRepository.deleteById(isin);
        candlestickService.deleteCandlestickHistory(isin);
    }


    /**
     * The getCandlestickHistory for fetch List of Candlestick history for specific instrument
     *
     * @param isin mandatory input for instrument's identifier
     * @throws InstrumentNotFoundException where instrument not found
     */
    public List<CandlestickResponseDto> getCandlestickHistory(String isin) throws InstrumentNotFoundException {
        List<CandlestickResponseDto> result;
        Optional<InstrumentHash> optionalInstrumentHash = instrumentRepository.findById(isin);
        InstrumentHash instrumentHash = optionalInstrumentHash.orElseThrow(() -> new InstrumentNotFoundException(isin));
        List<CandlestickHash> candlestickHashList = candlestickRepository.findByIsinEquals(instrumentHash.getId());
        result = parseListOfCandlestick(candlestickHashList);
        return result;
    }

    private List<CandlestickResponseDto> parseListOfCandlestick(List<CandlestickHash> candlestickHashList) {
        List<CandlestickResponseDto> result = null;
        if (candlestickHashList.size() > 0) {
            result = candlestickHashList.stream().map(candlestickHash ->
                            new CandlestickResponseDto(candlestickHash.getId(),
                                    candlestickHash.getOpenTimestamp(),
                                    candlestickHash.getOpenPrice(),
                                    candlestickHash.getLowPrice(),
                                    candlestickHash.getHighPrice(),
                                    candlestickHash.getClosePrice(),
                                    candlestickHash.getCloseTimestamp()))
                    .collect(Collectors.toList());
        }
        return result;
    }
}
