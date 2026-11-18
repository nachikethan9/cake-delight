package com.cakedelight.notification_service.repository;

import com.cakedelight.notification_service.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByEventId(String eventId);

    Page<Notification> findByUserId(String userId, Pageable pageable);

    List<Notification> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}