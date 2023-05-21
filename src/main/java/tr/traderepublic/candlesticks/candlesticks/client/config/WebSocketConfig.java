package tr.traderepublic.candlesticks.candlesticks.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import tr.traderepublic.candlesticks.candlesticks.consts.ConstantConfig;

/**
 * WebSocket configuration class
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-17 13:45
 */

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    /**
     * This method registers available endpoint to listen on websocket
     *
     * @param registry mandatory object of StompEndpointRegistry class
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ConstantConfig.INSTRUMENTS_ENDPOINT, ConstantConfig.QUOTES_ENDPOINT);
    }
}
