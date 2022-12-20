package com.example.weight_inspection.repositories;

import com.example.weight_inspection.models.Notification;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NotificationRepository extends PagingAndSortingRepository<Notification, Long> {
    Notification findById(long id);
    Notification findByType(String type);
}
