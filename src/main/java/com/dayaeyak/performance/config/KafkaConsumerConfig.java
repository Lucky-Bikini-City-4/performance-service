package com.dayaeyak.performance.config;

import com.dayaeyak.performance.domain.booking.dto.BookingCompleteMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private final String SERVER = "localhost:9092";

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "performance-service-group");

        // Key/Value Deserializer 설정
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // ErrorHandlingDeserializer 안에서 실제 JsonDeserializer 사용
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // DTO 패키지 신뢰
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.dayaeyak.performance.domain.booking.dto");

        // 역직렬화 타입 지정
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, BookingCompleteMessage.class);
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // 오프셋 설정
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configProps;
    }

    @Bean
    public ConsumerFactory<String, BookingCompleteMessage> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingCompleteMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookingCompleteMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // 수동 AckMode 설정
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // ErrorHandler
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) -> {
                    System.err.printf("Kafka 메시지 처리 실패: topic=%s, partition=%d, offset=%d, value=%s\n",
                            record.topic(), record.partition(), record.offset(), record.value());
                    exception.printStackTrace();
                },
                new FixedBackOff(0L, 1L) // 재시도 1회 후 건너뜀
        );
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
