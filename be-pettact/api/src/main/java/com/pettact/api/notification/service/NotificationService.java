package com.pettact.api.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.dto.NotificationResDTO;
import com.pettact.api.notification.entity.Notification;
import com.pettact.api.notification.publisher.RedisPublisher;
import com.pettact.api.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RedisPublisher notificationPublisher;
    private final ObjectMapper objectMapper;

    public void sendNotification(NotificationReqDTO dto) {
        Notification notification = Notification.from(dto);
        notificationRepository.save(notification);

        NotificationResDTO notificationResDTO = NotificationResDTO.from(notification);
        
        // 로그용 문자열 변환
        try {
            String message = objectMapper.writeValueAsString(notificationResDTO);
            System.out.println("[PUBLISH] 알림 전송됨: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        notificationPublisher.publish("notifications", notificationResDTO);
    }    

    // 특정 사용자의 모든 알림
    @Transactional(readOnly = true)
    public List<NotificationResDTO> getNotificationsByUser(Long userNo) {
        List<Notification> notifications = notificationRepository.findByReceiverUserNoAndIsDeletedFalseOrderByCreatedAtDesc(userNo);
        return notifications.stream()
                .map(NotificationResDTO::from)
                .toList();
    }

    // 읽지 않은 알림 개수 조회
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long userNo) {
        return notificationRepository.countByReceiverUserNoAndIsReadFalseAndIsDeletedFalse(userNo);
    }

    // 특정 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationNo) {
        Notification notification = notificationRepository.findById(notificationNo)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    // 모든 알림 읽음 처리
    @Transactional
    public void markAllAsRead(Long userNo) {
        List<Notification> notifications = notificationRepository.findByReceiverUserNoAndIsReadFalseAndIsDeletedFalse(userNo);
        for (Notification notification : notifications) {
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
            }
        }
        notificationRepository.saveAll(notifications);
    }
    
    // soft delete
    public void softDelete(Long notificationNo, Long userNo) {
        Notification notification = notificationRepository
            .findByNotificationNoAndReceiverUserNoAndIsDeletedFalse(notificationNo, userNo)
            .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        notification.setDeleted(true); // boolean이면 true, 문자열이면 "Y"
        notificationRepository.save(notification);
    }
    
    // soft delete all
    @Transactional
    public void softDeleteAll(Long userNo) {
        notificationRepository
            .findByReceiverUserNoAndIsDeletedFalseOrderByCreatedAtDesc(userNo)
            .forEach(n -> n.setDeleted(true));
    }
}