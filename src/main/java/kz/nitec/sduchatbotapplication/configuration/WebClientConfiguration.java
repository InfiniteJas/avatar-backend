//package kz.nitec.sduchatbotapplication.configuration;
//
//import io.netty.channel.ChannelOption;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//
//import java.time.Duration;
//
//@Configuration
//public class WebClientConfiguration {
//
//    @Bean("chatWebClient")
//    public WebClient chatWebClient(
//            @Value("${chat.base-url}") String baseUrl,
//            @Value("${chat.timeout-ms}") int timeoutMs,
//            @Value("${chat.api-key}") String apiKey
//    ) {
//        HttpClient http = HttpClient.create()
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutMs)
//                .responseTimeout(Duration.ofMillis(timeoutMs))
//                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeoutMs / 1000)));
//
//        return WebClient.builder()
//                .baseUrl(baseUrl)
//                .clientConnector(new ReactorClientHttpConnector(http))
//                .defaultHeaders(h -> h.setBearerAuth(apiKey))
//                .exchangeStrategies(ExchangeStrategies.builder()
//                        .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
//                        .build())
//                .build();
//    }
//
//    @Bean("sttWebClient")
//    public WebClient sttWebClient(
//            @Value("${stt.base-url}") String baseUrl,
//            @Value("${stt.timeout-ms}") int timeoutMs,
//            @Value("${stt.api-key}") String apiKey
//    ) {
//        HttpClient http = HttpClient.create()
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutMs)
//                .responseTimeout(Duration.ofMillis(timeoutMs))
//                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeoutMs / 1000)));
//
//        return WebClient.builder()
//                .baseUrl(baseUrl)
//                .clientConnector(new ReactorClientHttpConnector(http))
//                .defaultHeaders(h -> h.setBearerAuth(apiKey))
//                .build();
//    }
//}
