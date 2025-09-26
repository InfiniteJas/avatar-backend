package kz.nitec.sduchatbotapplication.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfiguration {

    @Value("${chat.azure-openai-url}")
    private String azureOpenAiUrl;

    @Value("${chat.azure-openai-key}")
    private String azureOpenAiKey;

    @Value("${assistants.endpoint}")
    private String endpoint;

    @Value("${assistants.api-key}")
    private String apiKey;

    @Value("${assistants.api-version}")
    private String apiVersion;


    @Bean("defaultWebClient")
    public WebClient defaultWebClient(){
        return WebClient.builder().build();
    }

    @Bean("chatWebClient")
    @Primary
    public WebClient webClient() {
        String base = endpoint.replaceAll("/+$", "") + "/openai";

        ExchangeFilterFunction apiVersionAppender = (request, next) -> {
            var original = request.url().toString();
            String newUrl = original.contains("api-version=")
                    ? original
                    : original + (original.contains("?") ? "&" : "?") + "api-version=" + apiVersion;

            ClientRequest newRequest = ClientRequest.from(request)
                    .url(java.net.URI.create(newUrl))
                    .build();

            return next.exchange(newRequest);
        };

        return WebClient.builder()
                .baseUrl(base)
                .defaultHeader("api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(8 * 1024 * 1024))
                        .build())
                .filter(apiVersionAppender)
                .build();
    }
}
