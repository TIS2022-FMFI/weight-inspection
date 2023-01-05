package com.example.weight_inspection.controllers;

import com.example.weight_inspection.models.Notification;
import com.example.weight_inspection.repositories.NotificationRepository;
import com.example.weight_inspection.transfer.ListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/notification")
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<ListResponse<Notification>> GetNotifications(
            @RequestParam(value = "page", defaultValue = "0") int currentPage,
            @RequestParam(value = "page_size", defaultValue = "100") int pageSize) {

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("id").descending());
        Page<Notification> page = notificationRepository.findAll(pageable);
        ListResponse<Notification> listResponse = new ListResponse<>(page);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

    @GetMapping("{notificationId}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long notificationId) {

        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (!notification.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(notification.get(), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Notification> saveNotification(@RequestBody @Valid Notification notification, BindingResult bindingResult) {

        if (bindingResult.hasErrors() || notification == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        notification.setId(null);
        notificationRepository.save(notification);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    @PutMapping("{notificationId}")
    public ResponseEntity<Notification> replaceNotification(@RequestBody @Valid Notification notification,
                                                            BindingResult bindingResult,
                                                            @PathVariable Long notificationId) {

        if (bindingResult.hasErrors() || notification == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Notification> replacedNotification = notificationRepository.findById(notificationId);
        if (!replacedNotification.isPresent()) {
            notification.setId(null);
            notificationRepository.save(notification);
            return new ResponseEntity<>(notification, HttpStatus.NO_CONTENT);
        }

        notification.setId(notificationId);
        notificationRepository.save(notification);
        return new ResponseEntity<>(notification, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{notificationId}")
    public ResponseEntity<Notification> deleteNotification(@PathVariable Long notificationId) {

        Optional<Notification> notification  = notificationRepository.findById(notificationId);
        if (!notification.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Notification delNotification  = notification.get();
        delNotification.setId(notificationId);
        notificationRepository.delete(delNotification);
        return new ResponseEntity<>(delNotification, HttpStatus.NO_CONTENT);
    }
}