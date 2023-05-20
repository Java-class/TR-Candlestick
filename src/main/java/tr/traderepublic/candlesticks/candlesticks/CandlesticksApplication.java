package tr.traderepublic.candlesticks.candlesticks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The Main Candlestick Application Runner
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-17 13:21
 */

@SpringBootApplication
@EnableScheduling
public class CandlesticksApplication {
    public static void main(String[] args) {
        SpringApplication.run(CandlesticksApplication.class, args);
    }
}
