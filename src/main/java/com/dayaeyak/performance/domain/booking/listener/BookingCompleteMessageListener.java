package com.dayaeyak.performance.domain.booking.listener;

import com.dayaeyak.performance.domain.booking.dto.BookingCompleteMessage;
import com.dayaeyak.performance.domain.booking.dto.BulkSeatSoldOutRequestDto;
import com.dayaeyak.performance.domain.performance.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCompleteMessageListener {

    private final SeatService performanceSeatService;

    @KafkaListener(topics = "booking-complete", groupId = "performance-service-group")
    public void handleBookingComplete(@Payload BookingCompleteMessage message,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                      @Header(KafkaHeaders.RECEIVED_PARTITION) String partitionId,
                                      @Header(KafkaHeaders.OFFSET) long offset,
                                      Acknowledgment acknowledgment) {

        Integer partition = Integer.valueOf(partitionId);
        log.info("예약 완료/취소 메시지 수신 - topic: {}, partition: {}, offset: {}, message: {}",
                topic, partition, offset, message);

        try {
            // ServiceType이 PERFORMANCE인 경우만 처리
            if ("PERFORMANCE".equals(message.serviceType().name())) {
                processPerformanceBooking(message);
                log.info("공연 예약/취소 처리 완료 - userId: {}, serviceId: {}",
                        message.userId(), message.serviceId());
            } else {
                log.info("공연 서비스가 아닌 메시지 스킬 - serviceType: {}", message.serviceType());
            }

            // 메시지 처리 완료 확인
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("예약 완료/취소 메시지 처리 중 오류 발생", e);
            // 에러가 발생한 경우 acknowledge 하지 않아 재처리됨
            throw e;
        }
    }

    private void processPerformanceBooking(BookingCompleteMessage message) {
        var bookingDetail = message.bookingDetailRequest();

        // 좌석 품절 처리 요청 DTO 생성
        BulkSeatSoldOutRequestDto requestDto = new BulkSeatSoldOutRequestDto(
                bookingDetail.performanceId(),
                bookingDetail.sessionId(),
                bookingDetail.sectionId(),
                bookingDetail.seatIds()
        );

        // 좌석들을 품절 처리
        performanceSeatService.bulkChangeSeats(requestDto, bookingDetail.isSoldOut());

        log.info("좌석 품절 처리 변경 완료 - performanceId: {}, sessionId: {}, sectionId: {}, seatCount: {}",
                bookingDetail.performanceId(),
                bookingDetail.sessionId(),
                bookingDetail.sectionId(),
                bookingDetail.seatIds().size());
    }
}