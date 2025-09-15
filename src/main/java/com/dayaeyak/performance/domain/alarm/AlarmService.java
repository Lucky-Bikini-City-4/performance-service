package com.dayaeyak.performance.domain.alarm;


import com.dayaeyak.performance.domain.alarm.dto.ServiceRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final KafkaTemplate<String, ServiceRegisterRequestDto> kafkaTemplatePerformance;

    public void sendPerformanceRegisterAlarm(String topic, String key, ServiceRegisterRequestDto dto) {
        kafkaTemplatePerformance.send(topic, key, dto);
    }
}