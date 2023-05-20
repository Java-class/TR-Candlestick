package tr.traderepublic.candlesticks.candlesticks.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

    public void saveInstrument(String isin, String description) {
        InstrumentHash instrumentHash = new InstrumentHash(isin, description);
        instrumentRepository.save(instrumentHash);
    }

    @Async
    public void deleteInstrument(String isin) {
        instrumentRepository.deleteById(isin);
        candlestickService.deleteCandlestickHistory(isin);
    }

    public List<CandlestickResponseDto> getCandlestickHistory(String isin) {
        List<CandlestickResponseDto> result = null;
        Optional<InstrumentHash> optionalInstrumentHash = instrumentRepository.findById(isin);
        if (optionalInstrumentHash.isPresent()) {
            List<CandlestickHash> candlestickHashList = candlestickRepository.findByIsinEquals(isin);
            if (candlestickHashList.size() > 0) {
                result = candlestickHashList.stream().map(candlestickHash ->
                        new CandlestickResponseDto(candlestickHash.getId(),
                                candlestickHash.getOpenTimestamp(),
                                candlestickHash.getOpenPrice(),
                                candlestickHash.getLowPrice(),
                                candlestickHash.getHighPrice(),
                                candlestickHash.getClosePrice(),
                                candlestickHash.getCloseTimestamp())).collect(Collectors.toList());
            }
        } else {
            //Todo throw exception here
        }
        return result;
    }
}
