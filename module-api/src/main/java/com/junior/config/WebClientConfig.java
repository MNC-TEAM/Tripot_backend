package com.junior.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .uriBuilderFactory(defaultUriBuilderFactory())
                .exchangeStrategies(exchangeStrategies())
                .build();
    }


    private DefaultUriBuilderFactory defaultUriBuilderFactory() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return factory;
    }


    //DataBufferLimitException 해결
    private ExchangeStrategies exchangeStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(30 * 1024 * 1024))
                .build();
    }
}
