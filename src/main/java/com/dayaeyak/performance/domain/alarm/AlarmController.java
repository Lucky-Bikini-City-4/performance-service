package com.dayaeyak.performance.domain.alarm;


import com.dayaeyak.performance.domain.alarm.dto.ServiceRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performance")
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("/register")
    public String sendPerformanceRegisterAlarm(@RequestParam("topic") String topic,
                                               @RequestParam("key") String key,
                                               @RequestBody ServiceRegisterRequestDto dto) {
        alarmService.sendPerformanceRegisterAlarm(topic, key, dto);
        return "Performance register message sent to Kafka topic";
    }
}