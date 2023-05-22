package tr.traderepublic.candlesticks.candlesticks.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * Redis configuration class
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 9:45
 */

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private Integer port;

    /**
     * This method creates redis connection factory
     * @return RedisConnectionFactory Object
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    /**
     * This method creates redis template
     * @param connectionFactory  is a mandatory object for create redis template
     * @return Redis Template Object
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate redis = new RedisTemplate();
        redis.setConnectionFactory(connectionFactory);
        return redis;
    }
}
